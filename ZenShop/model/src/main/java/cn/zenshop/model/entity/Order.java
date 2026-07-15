package cn.zenshop.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
@Schema(description = "订单")
public class Order {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "商家ID")
    private Long businessId;

    @Schema(description = "收货地址ID")
    private Integer userAddressId;

    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;

    @Schema(description = "实付金额")
    private BigDecimal actualAmount;

    @Schema(description = "订单状态: 0=待付款, 1=待发货, 2=待收货, 3=已完成, 4=已取消, 5=取消审核, 6=取消失败")
    private Integer status;

    @Schema(description = "订单备注")
    private String remark;

    @Schema(description = "支付方式: 0=未支付, 1=微信, 2=支付宝")
    private Integer paymentMethod;

    @Schema(description = "支付时间")
    private LocalDateTime paymentTime;

    @Schema(description = "发货时间")
    private LocalDateTime deliveryTime;

    @Schema(description = "收货时间")
    private LocalDateTime receivedTime;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    @Schema(description = "是否删除: 1=未删, 0=已删")
    @TableLogic
    private Integer isDelete;
}

