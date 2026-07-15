package cn.zenshop.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "创建订单DTO")
public class OrderDto {

    @Schema(description = "收货地址ID")
    private Integer userAddressId;

    @Schema(description = "优惠券ID")
    private Long couponId;

    @Schema(description = "商品ID列表")
    private List<Long> productIds;

    @Schema(description = "数量列表（与商品ID一一对应）")
    private List<Integer> quantities;
}
