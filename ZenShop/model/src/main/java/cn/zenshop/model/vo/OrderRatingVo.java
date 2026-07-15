package cn.zenshop.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "订单评价VO")
public class OrderRatingVo {

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "商品图片")
    private String productImage;

    @Schema(description = "评分 1-5")
    private Integer score;

    @Schema(description = "评价内容")
    private String content;

    @Schema(description = "评价时间")
    private LocalDateTime createdTime;
}
