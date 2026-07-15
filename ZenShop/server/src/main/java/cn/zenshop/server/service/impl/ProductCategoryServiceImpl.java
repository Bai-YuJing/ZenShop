package cn.zenshop.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.zenshop.common.constant.CommonConstant;
import cn.zenshop.common.constant.MessageConstant;
import cn.zenshop.common.exception.BaseException;
import cn.zenshop.model.dto.ProductCategoryDto;
import cn.zenshop.model.dto.ProductDto;
import cn.zenshop.model.entity.Product;
import cn.zenshop.model.entity.ProductCategory;
import cn.zenshop.model.vo.ProductCategoryVo;
import cn.zenshop.server.mapper.ProductCategoryMapper;
import cn.zenshop.server.service.ProductCategoryService;
import cn.zenshop.server.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {
    @Autowired
    private ProductService productService;

    @Override
    public void addCategory(String id, ProductCategoryDto dto) {
        Long count = lambdaQuery().eq(ProductCategory::getBusinessId, id).count();
        if (count>= CommonConstant.FULL_SHOP_CATEGORY){
            throw new BaseException(MessageConstant.SHOP_CATEGORY_FULL);
        }
        ProductCategory productCategory = BeanUtil.copyProperties(dto, ProductCategory.class);
        productCategory.setBusinessId(id);
        save(productCategory);
    }

    /**
     * 获取所有分类
     * @param id
     * @param name
     * @return
     */
    @Override
    public List<ProductCategoryVo> getAll(String id, String name) {
        List<ProductCategory> list = lambdaQuery()
                .eq(ProductCategory::getBusinessId, id)
                .like(StrUtil.isNotBlank(name), ProductCategory::getName, name)
                .orderByAsc(ProductCategory::getSort)
                .orderByDesc(ProductCategory::getCreatedTime)
                .list();
         return BeanUtil.copyToList(list, ProductCategoryVo.class);
    }

    /**
     * 更新分类
     * @param dto
     */
    @Override
    public void updateCategory(ProductCategoryDto dto) {
        ProductCategory productCategory = BeanUtil.copyProperties(dto, ProductCategory.class);
        updateById(productCategory);
    }

    /**
     * 删除分类
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        removeById(id);
    }

    /**
     * 更新分类状态
     * @param id
     * @param status
     */
    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        if (status == null || (!status.equals(CommonConstant.ON_SALE) && !status.equals(CommonConstant.NOT_ON_SALE))) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }
        if (CommonConstant.NOT_ON_SALE.equals(status)) {
            productService.lambdaUpdate()
                    .eq(Product::getCategoryId, id)
                    .set(Product::getStatus, CommonConstant.NOT_ON_SALE)
                    .update();
        }
        lambdaUpdate()
                .eq(ProductCategory::getId,id)
                .set(ProductCategory::getStatus,status)
                .update();
    }
}
