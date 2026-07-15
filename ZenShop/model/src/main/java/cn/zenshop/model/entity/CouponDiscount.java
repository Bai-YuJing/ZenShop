package cn.zenshop.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("coupon_discount")
@Schema(description = "折扣券")
public class CouponDiscount {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "优惠券ID（关联coupon表）")
    private Long couponId;

    @Schema(description = "折扣率（如0.8=8折）")
    private BigDecimal discountRate;

    @Schema(description = "最高减免金额（null=无上限）")
    private BigDecimal maxDiscountAmount;
}
