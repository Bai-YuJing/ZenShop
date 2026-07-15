package cn.zenshop.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户登录DTO")
public class UserLoginDto {

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "验证码")
    private String captcha;

    @Schema(description = "密码")
    private String password;
}
