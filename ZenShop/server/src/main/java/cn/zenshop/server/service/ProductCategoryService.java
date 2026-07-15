package cn.zenshop.server.service;

import cn.zenshop.model.dto.ProductCategoryDto;
import cn.zenshop.model.entity.ProductCategory;
import cn.zenshop.model.vo.ProductCategoryVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ProductCategoryService extends IService<ProductCategory> {
    /**
     * 添加商品分类
     * @param s
     * @param dto
     */
    void addCategory(String s, ProductCategoryDto dto);

    /**
     * 获取商家所有分类
     * @param id
     * @param name
     * @return
     */
    List<ProductCategoryVo> getAll(String id, String name);

    /**
     * 更新分类
     * @param dto
     */
    void updateCategory(ProductCategoryDto dto);

    /**
     * 删除分类
     * @param id
     */
    void deleteById(Long id);

    /**
     *
     * 修改状态
     * @param id
     * @param status
     */
    void updateStatus(Long id, Integer status);
}
