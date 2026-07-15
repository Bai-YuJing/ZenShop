package cn.zenshop.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "用户分页VO")
public class UserPageVo {

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

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdTime;

    @Schema(description = "最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastLoginTime;
}
