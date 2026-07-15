package cn.zenshop.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("order_cancel")
@Schema(description = "订单取消")
public class OrderCancel {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "取消原因类型: 1=不想要了, 2=地址填错, 3=商品质量问题, 4=其他")
    private Integer reasonType;

    @Schema(description = "取消原因")
    private String reason;

    @Schema(description = "状态: 0=待审核, 1=同意取消, 2=拒绝取消")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
