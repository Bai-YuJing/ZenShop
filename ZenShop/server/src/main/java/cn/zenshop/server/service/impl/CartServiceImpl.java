package cn.zenshop.server.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zenshop.common.constant.MessageConstant;
import cn.zenshop.common.constant.RedisConstant;
import cn.zenshop.common.context.ThreadLocalContext;
import cn.zenshop.common.exception.BaseException;
import cn.zenshop.model.entity.Business;
import cn.zenshop.model.entity.Product;
import cn.zenshop.model.entity.ProductImage;
import cn.zenshop.model.vo.CartGroupVo;
import cn.zenshop.model.vo.CartItemVo;
import cn.zenshop.server.mapper.BusinessMapper;
import cn.zenshop.server.mapper.ProductMapper;
import cn.zenshop.server.service.CartService;
import cn.zenshop.server.service.ProductImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private BusinessMapper businessMapper;

    /**
     * 获取当前用户购物车的Redis Key
     */
    private String getCartKey() {
        Long userId = ThreadLocalContext.get();
        return RedisConstant.USER_CART_KEY + userId;
    }

    /**
     * 刷新购物车TTL
     */
    private void refreshTtl(String key) {
        stringRedisTemplate.expire(key, RedisConstant.USER_CART_TTL, TimeUnit.SECONDS);
    }

    /**
     * 添加商品到购物车（已存在则累加数量）
     * @param productId 商品ID
     * @param quantity  数量
     */
    @Override
    public void addCart(Long productId, Integer quantity) {
        if (productId == null || quantity == null || quantity <= 0) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }

        // 查询商品信息
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BaseException(MessageConstant.PRODUCT_NOT_FOUND);
        }
        if (product.getStatus() != 1) {
            throw new BaseException(MessageConstant.PRODUCT_NOT_ON_SALE);
        }

        String key = getCartKey();
        String field = String.valueOf(productId);
        String cartItemJson = (String) stringRedisTemplate.opsForHash().get(key, field);

        if (StrUtil.isNotBlank(cartItemJson)) {
            // 已存在，累加数量
            CartItemVo existItem = JSONUtil.toBean(cartItemJson, CartItemVo.class);
            existItem.setQuantity(existItem.getQuantity() + quantity);
            existItem.setStock(product.getStock());
            stringRedisTemplate.opsForHash().put(key, field, JSONUtil.toJsonStr(existItem));
        } else {
            // 不存在，新建
            CartItemVo item = new CartItemVo();
            item.setProductId(product.getId());
            item.setBusinessId(product.getBusinessId());
            item.setName(product.getName());
            item.setPrice(product.getPrice());
            item.setQuantity(quantity);
            item.setStock(product.getStock());
            item.setStatus(product.getStatus());

            // 获取商品首图
            List<ProductImage> images = productImageService.lambdaQuery()
                    .eq(ProductImage::getProductId, productId)
                    .last("LIMIT 1")
                    .list();
            if (!images.isEmpty()) {
                item.setImage(images.get(0).getUrl());
            }

            stringRedisTemplate.opsForHash().put(key, field, JSONUtil.toJsonStr(item));
        }

        refreshTtl(key);
    }

    /**
     * 获取购物车列表（按商家分组）
     * @return 按商家分组的购物车列表
     */
    @Override
    public List<CartGroupVo> getCartList() {
        String key = getCartKey();
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);

        if (entries.isEmpty()) {
            return Collections.emptyList();
        }

        // 解析所有购物车项
        List<CartItemVo> allItems = new ArrayList<>();
        for (Object json : entries.values()) {
            CartItemVo item = JSONUtil.toBean((String) json, CartItemVo.class);
            allItems.add(item);
        }

        // 按商家分组
        Map<String, List<CartItemVo>> grouped = allItems.stream()
                .collect(Collectors.groupingBy(CartItemVo::getBusinessId));

        List<CartGroupVo> result = new ArrayList<>();
        for (Map.Entry<String, List<CartItemVo>> entry : grouped.entrySet()) {
            CartGroupVo group = new CartGroupVo();
            group.setBusinessId(entry.getKey());

            // 查询商家名称
            Business business = businessMapper.selectById(entry.getKey());
            group.setBusinessName(business != null ? business.getName() : "未知商家");
            group.setLogo(business != null ? business.getLogo() : null);
            group.setItems(entry.getValue());

            result.add(group);
        }

        refreshTtl(key);
        return result;
    }

    /**
     * 修改购物车商品数量
     * @param productId 商品ID
     * @param quantity  新数量
     */
    @Override
    public void updateQuantity(Long productId, Integer quantity) {
        if (productId == null || quantity == null || quantity <= 0) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }

        String key = getCartKey();
        String field = String.valueOf(productId);
        String cartItemJson = (String) stringRedisTemplate.opsForHash().get(key, field);

        if (StrUtil.isBlank(cartItemJson)) {
            throw new BaseException(MessageConstant.CART_ITEM_NOT_FOUND);
        }

        CartItemVo item = JSONUtil.toBean(cartItemJson, CartItemVo.class);
        item.setQuantity(quantity);
        stringRedisTemplate.opsForHash().put(key, field, JSONUtil.toJsonStr(item));
        refreshTtl(key);
    }

    /**
     * 删除购物车中的商品
     * @param productId 商品ID
     */
    @Override
    public void removeCart(Long productId) {
        String key = getCartKey();
        stringRedisTemplate.opsForHash().delete(key, String.valueOf(productId));
        refreshTtl(key);
    }

    /**
     * 清空购物车
     */
    @Override
    public void clearCart() {
        String key = getCartKey();
        stringRedisTemplate.delete(key);
    }
}
