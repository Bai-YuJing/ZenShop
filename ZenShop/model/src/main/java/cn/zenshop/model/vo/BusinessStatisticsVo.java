package cn.zenshop.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "商家端统计报表VO")
public class BusinessStatisticsVo {

    @Schema(description = "订单数")
    private Integer orderCount;

    @Schema(description = "收入")
    private BigDecimal revenue;

    @Schema(description = "好评数（评分4-5）")
    private Integer goodRatingCount;

    @Schema(description = "中评数（评分3）")
    private Integer mediumRatingCount;

    @Schema(description = "差评数（评分1-2）")
    private Integer badRatingCount;
}
