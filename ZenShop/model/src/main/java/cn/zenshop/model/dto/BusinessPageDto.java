package cn.zenshop.model.dto;

import cn.zenshop.common.constant.MessageConstant;
import cn.zenshop.model.enums.BusinessStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BusinessPageDto {
    private Integer page;
    private Integer size;

    @Schema(description = "店铺名称")
    private String name;

    @Schema(description = "分类ID")
    private Integer categoryId;


    @Schema(description = "省份ID")
    private Integer provinceId;

    @Schema(description = "城市ID")
    private Integer cityId;

    @Schema(description = "区县ID")
    private Integer districtId;

    @Schema(description = "状态")
    private BusinessStatusEnum status;

    @Schema(description = "最低评分筛选")
    private Double minRating;

    @Schema(description = "最高评分筛选")
    private Double maxRating;

    @Schema(description = "评分排序: ASC升序, DESC降序, 不传则不按评分排序")
    private String ratingSort;
}
