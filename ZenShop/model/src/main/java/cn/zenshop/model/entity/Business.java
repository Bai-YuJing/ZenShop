package cn.zenshop.model.entity;

import cn.zenshop.model.enums.BusinessStatusEnum;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("business")
@Schema(description = "商家")
public class Business {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "店铺名称")
    private String name;

    @Schema(description = "店铺简介")
    private String description;

    @Schema(description = "logo URL")
    private String logo;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "联系邮箱")
    private String email;

    @Schema(description = "店铺分类ID")
    private Integer categoryId;

    @Schema(description = "状态: 0=待提交, 1=待审核, 2=正常, 3=冻结, 4=已关闭")
    private BusinessStatusEnum status;

    @Schema(description = "上次登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "是否删除: 1=未删, 0=已删")
    @TableLogic
    private Integer isDelete;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}

