package cn.zenshop.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "未评价订单商品VO（扁平结构，一商品一条记录）")
public class UnreviewedOrderVo {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "订单总金额（优惠前）")
    private BigDecimal totalAmount;

    @Schema(description = "实付金额")
    private BigDecimal actualAmount;

    @Schema(description = "订单状态: 0=待付款, 1=待发货, 2=待收货, 3=已完成")
    private Integer status;

    @Schema(description = "下单时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "商品图片")
    private String productImage;

    @Schema(description = "单价")
    private BigDecimal price;

    @Schema(description = "数量")
    private Integer quantity;
}
