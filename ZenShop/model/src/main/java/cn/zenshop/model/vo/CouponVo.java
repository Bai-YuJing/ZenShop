package cn.zenshop.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "优惠券VO")
public class CouponVo {

    @Schema(description = "用户优惠券ID（user_coupon主键）")
    private Long userCouponId;

    @Schema(description = "使用状态: 0=未使用, 1=已使用, 2=已过期")
    private Integer userCouponStatus;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "类型: 1=满减券, 2=折扣券")
    private Integer type;

    @Schema(description = "优惠券名称")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "有效期开始")
    private LocalDateTime validFrom;

    @Schema(description = "有效期结束")
    private LocalDateTime validTo;

    // ===== 满减券专属 =====

    @Schema(description = "满X元（满减券）")
    private BigDecimal minAmount;

    @Schema(description = "减Y元（满减券）")
    private BigDecimal discountAmount;

    // ===== 折扣券专属 =====

    @Schema(description = "折扣率（折扣券，如0.8=8折）")
    private BigDecimal discountRate;

    @Schema(description = "最高减免金额（折扣券，null=无上限）")
    private BigDecimal maxDiscountAmount;

    @Schema(description = "总数量（null=无限）")
    private Integer total;

    @Schema(description = "当前用户是否已领取")
    private Boolean claimed;
}
