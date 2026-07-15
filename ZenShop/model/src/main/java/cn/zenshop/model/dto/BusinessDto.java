package cn.zenshop.model.dto;

import cn.zenshop.model.enums.BusinessStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "商家DTO")
public class BusinessDto {

    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "店铺名称")
    private String name;

    @Schema(description = "店铺简介")
    private String description;

    @Schema(description = "logo URL")
    private String logo;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "联系邮箱")
    private String email;

    @Schema(description = "店铺分类ID")
    private Integer categoryId;

    @Schema(description = "状态")
    private BusinessStatusEnum status;

}
