package cn.zenshop.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "评价DTO")
public class RatingDto {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "评分 1-5")
    private Integer score;

    @Schema(description = "评价内容")
    private String content;
}
