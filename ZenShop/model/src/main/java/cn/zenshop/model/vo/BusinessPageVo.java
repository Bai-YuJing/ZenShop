package cn.zenshop.model.vo;

import cn.zenshop.model.enums.BusinessStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BusinessPageVo {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "店铺名称")
    private String name;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "联系邮箱")
    private String email;

    @Schema(description = "店铺简介")
    private String description;

    @Schema(description = "logo URL")
    private String logo;

    @Schema(description = "店铺分类")
    private String categoryName;

    @Schema(description = "店铺分类ID")
    private Integer categoryId;

    @Schema(description = "状态: 0=待提交, 1=待审核, 2=正常, 3=冻结, 4=已关闭")
    private BusinessStatusEnum status;

    @Schema(description = "省份ID")
    private Integer provinceId;

    @Schema(description = "城市ID")
    private Integer cityId;

    @Schema(description = "评分")
    private Double ratting;

    @Schema(description = "区县ID")
    private Integer districtId;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedTime;
}
