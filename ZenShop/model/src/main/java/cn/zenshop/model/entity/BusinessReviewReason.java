package cn.zenshop.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("business_review_reason")
@Schema(description = "商家审核原因记录")
public class BusinessReviewReason {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "商家ID")
    private String businessId;

    @Schema(description = "管理员ID")
    private Long adminId;

    @Schema(description = "类型: 1=驳回, 2=冻结")
    private Integer type;

    @Schema(description = "原因（对应枚举value）")
    private Integer reason;

    @Schema(description = "是否删除: 1=未删, 0=已删")
    @TableLogic
    private Integer isDelete;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
