package cn.zenshop.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "商家端昨日订单统计VO")
public class OrderStatisticsVo {

    @Schema(description = "昨日订单数")
    private Integer orderCount;

    @Schema(description = "昨日收入")
    private BigDecimal revenue;
}
