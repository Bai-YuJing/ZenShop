package cn.zenshop.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "每日订单统计VO")
public class DailyOrderStatsVo {

    @Schema(description = "日期")
    private String date;

    @Schema(description = "订单数")
    private Integer orderCount;

    @Schema(description = "收入")
    private BigDecimal revenue;
}
