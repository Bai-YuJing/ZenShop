package cn.zenshop.model.dto;

import cn.zenshop.common.constant.MessageConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "商品DTO")
public class ProductDto {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "商品图片")
    private List<String> images;

    @NotBlank(message = MessageConstant.PRODUCT_NAME_NULL)
    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "描述")
    private String description;

    @NotNull(message = MessageConstant.PRODUCT_PRICE_NULL)
    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "库存")
    private Integer stock;

}
