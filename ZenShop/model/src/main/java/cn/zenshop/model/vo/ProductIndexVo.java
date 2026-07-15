package cn.zenshop.model.vo;

import cn.zenshop.model.entity.ProductImage;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "商品详情VO")
public class ProductIndexVo {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "商品图片")
    private List<ProductImageVo> images;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "库存")
    private Integer stock;

    @Schema(description = "销量")
    private Integer sales;

    @Schema(description = "状态: 0=下架, 1=上架")
    private Integer status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdTime;
}
