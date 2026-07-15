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
public class ProductPageDto {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "商家id")
    private String businessId;

    @Schema(description = "库存排序")
    private String stockSort;

    @Schema(description = "销量排序")
    private String salesSort;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "最低价格")
    private BigDecimal minPrice;

    @Schema(description = "最高价格")
    private BigDecimal maxPrice;

    @Schema(description = "状态: 0=下架, 1=上架")
    private Integer status;

    private Integer page = 0;

    private Integer size = 15;
}
