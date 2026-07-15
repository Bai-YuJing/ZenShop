package cn.zenshop.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("coupon")
@Schema(description = "优惠券")
public class Coupon {

    @TableId(type = IdType.AUTO)
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

    @Schema(description = "状态: 0=下架, 1=上架")
    private Integer status;

    @Schema(description = "总数量（null=无限）")
    private Integer total;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
