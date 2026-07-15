package cn.zenshop.server.service;

import cn.zenshop.model.dto.ProductDto;
import cn.zenshop.model.dto.ProductPageDto;
import cn.zenshop.model.entity.Product;
import cn.zenshop.model.vo.ProductPageVo;
import cn.zenshop.model.vo.ProductResVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ProductService extends IService<Product> {
    /**
     * 添加分类
     * @param productDto
     */
    void addProduct(ProductDto productDto);

    /**
     * 商品分页查询
     * @param page
     * @param dto
     * @return
     */
    IPage<ProductPageVo> getProductList(Page<ProductPageVo> page, ProductPageDto dto);

    /**
     * 更新商品信息
     * @param productDto
     */
    void updateProduct(ProductDto productDto);

    /**
     * 更新商品状态
     * @param id
     * @param status
     */
    void updateStatus(Long id, Integer status);

    /**
     * 分页查询（用户端）
     * @param productPageVoPage
     * @param pageDto
     * @return
     */
    IPage<ProductPageVo> userGetProductList(Page<ProductPageVo> productPageVoPage, ProductPageDto pageDto);

    /**
     * 根据id获取商品信息
     * @param id
     * @return
     */
    ProductResVo getProductById(Integer id);

    /**
     * 获取用户未评价的商品列表
     * @param businessId 商家ID
     * @param userId 用户ID
     * @return
     */
    List<ProductPageVo> getUnratedProducts(String businessId, Long userId);
}
