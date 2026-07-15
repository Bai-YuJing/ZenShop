package cn.zenshop.model.dto;

import cn.zenshop.common.constant.MessageConstant;
import cn.zenshop.model.enums.BusinessStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Schema(description = "商家审核DTO")
public class BusinessReviewDto {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "店铺简介")
    private String description;

    @Schema(description = "logo URL")
    private String logo;

    @Schema(description = "联系邮箱")
    private String email;

    @Schema(description = "店铺分类ID")
    private Integer categoryId;


    @Schema(description = "省份ID")
    private Integer provinceId;

    @Schema(description = "城市ID")
    private Integer cityId;

    @Schema(description = "区县ID")
    private Integer districtId;

    @Schema(description = "详细地址")
    @NotNull(message = MessageConstant.DETAILED_ADDRESS_NULL)
    private String detailedAddress;


}
