package cn.zenshop.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "订单状态操作DTO")
public class OrderStatusDto {

    @Schema(description = "订单ID")
    private Long orderId;
}
