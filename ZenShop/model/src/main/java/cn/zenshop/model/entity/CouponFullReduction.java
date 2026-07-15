package cn.zenshop.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("coupon_full_reduction")
@Schema(description = "满减券")
public class CouponFullReduction {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "优惠券ID（关联coupon表）")
    private Long couponId;

    @Schema(description = "满X元")
    private BigDecimal minAmount;

    @Schema(description = "减Y元")
    private BigDecimal discountAmount;
}
