package cn.zenshop.model.dto;

import cn.zenshop.common.constant.MessageConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "用户注册DTO")
public class UserUpdatePwdDto {

    @Schema(description = "旧密码")
    @NotBlank(message = MessageConstant.OLD_PASSWORD_IS_NULL)
    private String oldPassword;

    @Schema(description = "新密码")
    @NotBlank(message = MessageConstant.NEW_PASSWORD_IS_NULL)
    private String newPassword;
}
