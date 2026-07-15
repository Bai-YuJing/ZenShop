package cn.zenshop.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "商家订单商品项")
public class BusinessOrderItemVo {

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "商品图片")
    private String productImage;

    @Schema(description = "单价")
    private BigDecimal price;

    @Schema(description = "数量")
    private Integer quantity;

    @Schema(description = "评分")
    private Integer score;

    @Schema(description = "评价内容")
    private String ratingContent;

    @Schema(description = "是否已评价")
    private Boolean rated;
}
