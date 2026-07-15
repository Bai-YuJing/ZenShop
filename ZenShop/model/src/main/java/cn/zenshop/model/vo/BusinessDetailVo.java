package cn.zenshop.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "商家详情VO（含商品分类）")
public class BusinessDetailVo {

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

    @Schema(description = "店铺分类")
    private String categoryName;

    @Schema(description = "店铺分类ID")
    private Integer categoryId;

    @Schema(description = "评分")
    private Double ratting;

    @Schema(description = "省份ID")
    private Integer provinceId;

    @Schema(description = "城市ID")
    private Integer cityId;

    @Schema(description = "区县ID")
    private Integer districtId;

    @Schema(description = "详细地址")
    private String detailedAddress;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    @Schema(description = "商品分类列表")
    private List<ProductCategoryVo> categories;
}
