package cn.zenshop.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "订单状态数量VO")
public class OrderStatusCountVo {

    @Schema(description = "待付款数量")
    private Long pendingPayment;

    @Schema(description = "待发货数量")
    private Long pendingDelivery;

    @Schema(description = "待收货数量")
    private Long receiving;

    @Schema(description = "已完成数量")
    private Long completed;
}
