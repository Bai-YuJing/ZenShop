package cn.zenshop.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "商品评价查询DTO")
public class ProductRatingDto {

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "排序字段: score=按评分, time=按时间")
    private String sortBy;

    @Schema(description = "排序方式: ASC升序, DESC降序")
    private String sortOrder;

    @Schema(description = "页码")
    private Integer page = 0;

    @Schema(description = "每页条数")
    private Integer size = 10;
}
