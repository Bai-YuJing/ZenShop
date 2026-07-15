package cn.zenshop.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "优惠券DTO（管理端）")
public class CouponDto {

    @Schema(description = "页码（分页查询时使用）")
    private Integer page = 0;

    @Schema(description = "每页条数（分页查询时使用）")
    private Integer size = 10;

    @Schema(description = "主键ID（更新时必传）")
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

    @Schema(description = "状态: 0=下架, 1=上架")
    private Integer status;

    @Schema(description = "总数量（null=无限）")
    private Integer total;

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

    @Schema(description = "剩余数量排序: ASC升序, DESC降序, 不传则按创建时间倒序")
    private String remainingSort;
}
