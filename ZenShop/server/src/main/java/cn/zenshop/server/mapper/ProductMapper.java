package cn.zenshop.server.mapper;

import cn.zenshop.model.dto.ProductPageDto;
import cn.zenshop.model.entity.Product;
import cn.zenshop.model.vo.ProductPageVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    IPage<ProductPageVo> getProductList(Page<ProductPageVo> page, ProductPageDto dto);

    IPage<ProductPageVo> userGetProductList(Page<ProductPageVo> page, ProductPageDto dto);

    String getProductRating(@Param("productId") Long productId);

    List<ProductPageVo> selectUnratedProducts(@Param("businessId") String businessId, @Param("userId") Long userId);
}
