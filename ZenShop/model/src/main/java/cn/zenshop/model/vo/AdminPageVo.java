package cn.zenshop.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "管理员分页VO")
public class AdminPageVo {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "显示名称")
    private String nickname;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "角色: 1=普通管理员, 2=超级管理员")
    private Integer role;

    @Schema(description = "状态: 0=禁用, 1=启用")
    private Integer status;

    @Schema(description = "最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastLoginTime;

    @Schema(description = "是否删除: 1=未删, 0=已删")
    private Integer isDelete;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedTime;
}

