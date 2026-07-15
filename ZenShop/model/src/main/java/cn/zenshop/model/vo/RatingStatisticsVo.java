package cn.zenshop.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "商家端昨日评价统计VO")
public class RatingStatisticsVo {

    @Schema(description = "好评数（评分4-5）")
    private Integer goodCount;

    @Schema(description = "中评数（评分2-4）")
    private Integer mediumCount;

    @Schema(description = "差评数（评分0-2）")
    private Integer badCount;
}
