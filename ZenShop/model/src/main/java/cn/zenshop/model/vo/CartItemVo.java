package cn.zenshop.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "购物车商品VO")
public class CartItemVo {

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商家ID")
    private String businessId;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "商品图片")
    private String image;

    @Schema(description = "单价")
    private BigDecimal price;

    @Schema(description = "购买数量")
    private Integer quantity;

    @Schema(description = "库存")
    private Integer stock;

    @Schema(description = "状态: 0=下架, 1=上架")
    private Integer status;
}
