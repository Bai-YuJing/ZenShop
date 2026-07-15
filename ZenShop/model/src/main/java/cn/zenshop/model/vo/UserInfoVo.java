package cn.zenshop.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
@Schema(description = "用户")
public class UserInfoVo {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "状态: 0=禁用, 1=启用")
    private Integer status;

}

