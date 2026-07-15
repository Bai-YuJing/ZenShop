package cn.zenshop.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zenshop.common.constant.CommonConstant;
import cn.zenshop.common.constant.MessageConstant;
import cn.zenshop.common.constant.RedisConstant;
import cn.zenshop.common.context.ThreadLocalContext;
import cn.zenshop.common.exception.BaseException;
import cn.zenshop.model.dto.ProductDto;
import cn.zenshop.model.dto.ProductPageDto;
import cn.zenshop.model.entity.Business;
import cn.zenshop.model.entity.BusinessRating;
import cn.zenshop.model.entity.Product;
import cn.zenshop.model.entity.ProductCategory;
import cn.zenshop.model.entity.ProductImage;
import cn.zenshop.model.vo.*;
import cn.zenshop.server.mapper.ProductImageMapper;
import cn.zenshop.server.mapper.ProductCategoryMapper;
import cn.zenshop.server.mapper.ProductMapper;
import cn.zenshop.server.service.BusinessRatingService;
import cn.zenshop.server.service.ProductImageService;
import cn.zenshop.server.service.ProductService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    @Autowired
    private ProductImageService productImageService;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductImageMapper productImageMapper;
    @Autowired
    private ProductCategoryMapper productCategoryMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private BusinessRatingService businessRatingService;

    @Autowired
    private cn.zenshop.server.mapper.BusinessMapper businessMapper;

    /**
     * 添加商品
     * @param dto
     */
    @Override
    public void addProduct(ProductDto dto) {
        //保存商品信息
        Product product = BeanUtil.copyProperties(dto, Product.class);
        product.setSales(CommonConstant.SALES);
        product.setStatus(CommonConstant.ON_SALE);
        product.setBusinessId(String.valueOf(ThreadLocalContext.get()));
        save(product);

        // 写入 Redis 缓存（销量、库存）
        String businessId = product.getBusinessId();
        Long productId = product.getId();
        stringRedisTemplate.opsForValue().set(
                RedisConstant.BUSINESS_PRODUCT_SALES_KEY + businessId + ":" + productId,
                String.valueOf(product.getSales())
        );
        stringRedisTemplate.opsForValue().set(
                RedisConstant.BUSINESS_PRODUCT_STOCK_KEY + businessId + ":" + productId,
                String.valueOf(product.getStock())
        );

        //保存图片信息
        List<String> image = dto.getImages();
        if (cn.hutool.core.collection.CollUtil.isNotEmpty(image)) {
            List<ProductImage> productImageList = image.stream()
                    .map(url -> {
                        ProductImage pi = new ProductImage();
                        pi.setUrl(url);
                        return pi;
                    })
                    .collect(Collectors.toList());
            productImageService.saveBatch(productImageList);
        }
    }

    /**
     * 分页查询商品信息
     * @param page
     * @param dto
     * @return
     */
    @Override
    public IPage<ProductPageVo> getProductList(Page<ProductPageVo> page, ProductPageDto dto) {
        // 1. 分页查询商品基本信息（含分类名称）
        IPage<ProductPageVo> productList = productMapper.getProductList(page, dto);

        // 2. 批量查询商品图片并组装到 VO 中
        List<ProductPageVo> records = productList.getRecords();
        attachImages(records);

        // 3. Redis 销量优先级高于数据库
        for (ProductPageVo vo : records) {
            String salesStr = stringRedisTemplate.opsForValue().get(
                    RedisConstant.BUSINESS_PRODUCT_SALES_KEY + vo.getBusinessId() + ":" + vo.getId());
            if (salesStr != null) {
                vo.setSales(Integer.valueOf(salesStr));
            }
        }
        return productList;
    }

    /**
     * 更新商品信息
     * @param dto
     */
    @Override
    @Transactional
    public void updateProduct(ProductDto dto) {
        Product product = BeanUtil.copyProperties(dto, Product.class);
        updateById(product);

        // 先删除旧图片
        productImageService.remove(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProductImage>()
                        .eq(ProductImage::getProductId, product.getId())
        );
        // 再插入新图片
        List<String> images = dto.getImages();
        if (cn.hutool.core.collection.CollUtil.isNotEmpty(images)) {
            String businessId = String.valueOf(ThreadLocalContext.get());
            List<ProductImage> productImageList = images.stream()
                    .map(url -> {
                        ProductImage pi = new ProductImage();
                        pi.setBusinessId(businessId);
                        pi.setProductId(product.getId());
                        pi.setUrl(url);
                        return pi;
                    })
                    .collect(Collectors.toList());
            productImageService.saveBatch(productImageList);
        }
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (status == null || (!status.equals(CommonConstant.ON_SALE) && !status.equals(CommonConstant.NOT_ON_SALE))) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }
        if (status.equals(CommonConstant.ON_SALE)){
            // 上架前检查商品分类是否已下架
            Product product = getById(id);
            if (product != null && product.getCategoryId() != null) {
                ProductCategory category = productCategoryMapper.selectById(product.getCategoryId());
                if (category == null || !CommonConstant.ON_SALE.equals(category.getStatus())) {
                    throw new BaseException(MessageConstant.CATEGORY_NOT_ON_SALE);
                }
            }
        }
        lambdaUpdate()
                .eq(Product::getId,id)
                .set(Product::getStatus,status)
                .set(Product::getUpdatedTime, LocalDateTime.now())
                .update();
    }

    /**
     * 商品分页查询（用户端）
     * @param page
     * @param dto
     */
    @Override
    public IPage<ProductPageVo> userGetProductList(Page<ProductPageVo> page, ProductPageDto dto) {
        // 1. 分页查询商品基本信息
        IPage<ProductPageVo> productList = productMapper.userGetProductList(page, dto);

        // 2. 批量查询商品图片并组装到 VO 中
        List<ProductPageVo> records = productList.getRecords();
        attachImages(records);

        // 3. Redis 销量优先级高于数据库（使用每个商品的 businessId 构建 key）
        for (ProductPageVo vo : records) {
            String salesStr = stringRedisTemplate.opsForValue().get(
                    RedisConstant.BUSINESS_PRODUCT_SALES_KEY + vo.getBusinessId() + ":" + vo.getId());
            if (salesStr != null) {
                vo.setSales(Integer.valueOf(salesStr));
            }
        }
        return productList;
    }

    /**
     * 根据id获取商品信息
     */
    @Override
    public ProductResVo getProductById(Integer id) {
        String key=RedisConstant.PRODUCT_INFO+id;
        // 先从redis查
        String cacheStr = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(cacheStr)) {
            ProductResVo cached = JSONUtil.toBean(cacheStr, ProductResVo.class);
            // 缓存中商家信息为空，说明是旧缓存脏数据，删除缓存后重新查询
            if (StrUtil.isBlank(cached.getBusinessName())) {
                stringRedisTemplate.delete(key);
            } else {
                return cached;
            }
        }

        // 没有则查数据库
        Product product = getById(id);
        List<ProductImage> list = productImageService
                .lambdaQuery()
                .eq(ProductImage::getBusinessId, product.getBusinessId())
                .eq(ProductImage::getProductId, id)
                .list();
        ProductIndexVo indexVo = BeanUtil.copyProperties(product, ProductIndexVo.class);
        List<ProductImageVo> voList = BeanUtil.copyToList(list, ProductImageVo.class);
        indexVo.setImages(voList);

        // Redis 销量/库存覆盖数据库值
        String salesStr = stringRedisTemplate.opsForValue().get(
                RedisConstant.BUSINESS_PRODUCT_SALES_KEY + product.getBusinessId() + ":" + id);
        if (salesStr != null) {
            indexVo.setSales(Integer.valueOf(salesStr));
        }
        String stockStr = stringRedisTemplate.opsForValue().get(
                RedisConstant.BUSINESS_PRODUCT_STOCK_KEY + product.getBusinessId() + ":" + id);
        if (stockStr != null) {
            indexVo.setStock(Integer.valueOf(stockStr));
        }

        // 获取商品评分
        String rating = productMapper.getProductRating(id.longValue());
        //获取商品评价
        List<BusinessRating> businessRatings = businessRatingService
                .lambdaQuery()
                .eq(BusinessRating::getBusinessId, product.getBusinessId())
                .eq(BusinessRating::getProductId, id)
                .list();
        List<BusinessRatingVo> ratingVos = BeanUtil.copyToList(businessRatings, BusinessRatingVo.class);
        // 封装返回
        ProductResVo resVo = new ProductResVo();
        resVo.setProductIndexVo(indexVo);
        resVo.setRating(rating);

        // 查询商家信息
        BusinessVo business = businessMapper.getBusinessById(product.getBusinessId());
        if (business != null) {
            resVo.setBusinessName(business.getName());
            resVo.setBusinessLogo(business.getLogo());
            resVo.setBusinessRating(business.getRatting());
        }

        // 商家信息不为空时才写入缓存，避免缓存脏数据
        if (resVo.getBusinessName() != null) {
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(resVo), RedisConstant.PRODUCT_INFO_TTL, TimeUnit.SECONDS);
        }
        return resVo;
    }

    @Override
    public List<ProductPageVo> getUnratedProducts(String businessId, Long userId) {
        List<ProductPageVo> list = productMapper.selectUnratedProducts(businessId, userId);
        attachImages(list);
        return list;
    }

    /**
     * 批量查询商品图片并组装到 VO 中
     */
    private void attachImages(List<ProductPageVo> records) {
        if (records.isEmpty()) return;
        List<Long> productIds = records.stream()
                .map(ProductPageVo::getId)
                .collect(Collectors.toList());
        List<ProductImage> imageList = productImageMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProductImage>()
                        .in(ProductImage::getProductId, productIds)
                        .select(ProductImage::getProductId, ProductImage::getUrl)
        );
        Map<Long, List<String>> imageMap = imageList.stream()
                .collect(Collectors.groupingBy(
                        ProductImage::getProductId,
                        Collectors.mapping(ProductImage::getUrl, Collectors.toList())
                ));
        for (ProductPageVo vo : records) {
            List<String> images = imageMap.get(vo.getId());
            if (images != null) {
                vo.setImages(images);
            }
        }
    }

}
