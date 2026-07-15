package cn.zenshop.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "每日评价统计VO")
public class DailyRatingStatsVo {

    @Schema(description = "日期")
    private String date;

    @Schema(description = "好评数（评分4-5）")
    private Integer goodCount;

    @Schema(description = "中评数（评分3）")
    private Integer mediumCount;

    @Schema(description = "差评数（评分1-2）")
    private Integer badCount;
}
