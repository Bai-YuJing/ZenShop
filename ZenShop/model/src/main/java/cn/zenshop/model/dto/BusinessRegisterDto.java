package cn.zenshop.model.dto;

import cn.zenshop.common.constant.MessageConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "商家注册DTO")
public class BusinessRegisterDto {

    @Schema(description = "店铺名称")
    @NotBlank(message = MessageConstant.SHOP_NAME_NULL)
    private String name;

    @Schema(description = "分类ID")
    @NotNull(message = MessageConstant.CATEGORY_NULL)
    private Integer categoryId;

    @Schema(description = "手机号")
    @NotBlank(message = MessageConstant.PHONE_NULL)
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = MessageConstant.PHONE_NUMBER_ERROR)
    private String phone;

    @Schema(description = "验证码")
    @NotBlank(message = MessageConstant.CAPTCHA_NULL)
    private String captcha;

    @Schema(description = "省份ID")
    @NotNull(message = MessageConstant.PROVINCE_NULL)
    private Integer provinceId;

    @Schema(description = "城市ID")
    @NotNull(message = MessageConstant.CITY_NULL)
    private Integer cityId;

    @Schema(description = "区县ID")
    @NotNull(message = MessageConstant.DISTRICT_NULL)
    private Integer districtId;

    @Schema(description = "详细地址")
    private String detailedAddress;

}
