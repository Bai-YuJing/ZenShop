package cn.zenshop.model.dto;

import cn.zenshop.common.constant.MessageConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "用户注册DTO")
public class UserRegisterDto {

    @Schema(description = "手机号")
    @NotBlank(message = MessageConstant.PHONE_NULL)
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = MessageConstant.PHONE_NUMBER_ERROR)
    private String phone;

    @Schema(description = "用户名")
    @NotBlank(message = MessageConstant.NAME_NULL)
    private String username;

    @Schema(description = "验证码")
    @NotBlank(message = MessageConstant.CAPTCHA_NULL)
    private String captcha;

    @Schema(description = "密码")
    @NotBlank(message = MessageConstant.PASSWORD_ERROR)
    private String password;
}
