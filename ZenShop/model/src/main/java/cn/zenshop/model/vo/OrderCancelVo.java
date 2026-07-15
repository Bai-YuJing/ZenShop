package cn.zenshop.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "订单取消申请VO")
public class OrderCancelVo {

    @Schema(description = "取消记录ID")
    private Long id;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "订单金额")
    private BigDecimal actualAmount;

    @Schema(description = "取消原因类型")
    private Integer reasonType;

    @Schema(description = "取消原因")
    private String reason;

    @Schema(description = "审核状态: 0=待审核, 1=同意取消, 2=拒绝取消")
    private Integer status;

    @Schema(description = "订单状态: 0=待付款, 1=待发货, 2=待收货, 3=已完成, 4=已取消, 5=取消审核, 6=取消失败")
    private Integer orderStatus;

    @Schema(description = "申请时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
}
