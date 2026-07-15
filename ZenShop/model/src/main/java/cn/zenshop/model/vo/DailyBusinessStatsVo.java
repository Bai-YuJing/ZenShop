package cn.zenshop.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "每日商家统计VO")
public class DailyBusinessStatsVo {

    @Schema(description = "日期")
    private String date;

    @Schema(description = "总商家数")
    private Long totalBusinesses;

    @Schema(description = "新增商家数")
    private Long newBusinesses;
}
