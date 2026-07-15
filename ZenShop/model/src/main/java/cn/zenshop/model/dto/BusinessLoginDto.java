package cn.zenshop.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "商家登录DTO")
public class BusinessLoginDto {
    @Schema(description = "联系电话")
    private String phone;
    @Schema(description = "验证码")
    private String captcha;
}
