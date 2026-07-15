package cn.zenshop.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "创建订单返回VO")
public class OrderCreateVo {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "实付金额")
    private BigDecimal actualAmount;
}
