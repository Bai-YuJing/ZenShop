package cn.zenshop.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_detail")
@Schema(description = "订单详情")
public class OrderDetail {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "下单时单价")
    private BigDecimal price;

    @Schema(description = "购买数量")
    private Integer quantity;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
