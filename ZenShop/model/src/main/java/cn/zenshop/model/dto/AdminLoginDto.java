package cn.zenshop.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "管理员登录DTO")
public class AdminLoginDto {

    @Schema(description = "登录用户名")
    private String username;

    @Schema(description = "密码)")
    private String password;

    @Schema(description = "验证码")
    private String captcha;

    @Schema(description = "key")
    private String captchaKey;
}
