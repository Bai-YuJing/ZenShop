package cn.zenshop.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "用户评价VO（管理员端）")
public class UserRatingVo {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "评分 1-5")
    private Integer score;

    @Schema(description = "评价内容")
    private String content;

    @Schema(description = "评价时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdTime;
}
