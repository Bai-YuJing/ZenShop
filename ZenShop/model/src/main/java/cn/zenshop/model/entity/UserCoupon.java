package cn.zenshop.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_coupon")
@Schema(description = "用户优惠券")
public class UserCoupon {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "优惠券ID（关联coupon表）")
    private Long couponId;

    @Schema(description = "订单ID（使用后记录）")
    private Long orderId;

    @Schema(description = "状态: 0=未使用, 1=已使用, 2=已过期")
    private Integer status;

    @Schema(description = "使用时间")
    private LocalDateTime usedTime;

    @Schema(description = "领取时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
