package cn.zenshop.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "支付DTO")
public class PayDto {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "支付方式: 1=微信, 2=支付宝")
    private Integer paymentMethod;
}
