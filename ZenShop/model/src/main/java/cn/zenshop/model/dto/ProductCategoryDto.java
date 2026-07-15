package cn.zenshop.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "商品分类DTO")
public class ProductCategoryDto {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "排序")
    private Integer sort;
}
