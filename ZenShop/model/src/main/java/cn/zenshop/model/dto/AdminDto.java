package cn.zenshop.model.dto;

import cn.zenshop.common.constant.MessageConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "管理员DTO")
public class AdminDto {

    @Schema(description = "id")
    private Integer id;

    @Schema(description = "用户名")
    @NotBlank(message = MessageConstant.NAME_NULL)
    private String username;

    @Schema(description = "显示名称")
    private String nickname;

    @Schema(description = "手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = MessageConstant.PHONE_NUMBER_ERROR)
    private String phone;

    @Schema(description = "邮箱")
    @Email(message = MessageConstant.EMAIL_ERROR)
    private String email;

    @Schema(description = "头像")
    private String avatar;
}
