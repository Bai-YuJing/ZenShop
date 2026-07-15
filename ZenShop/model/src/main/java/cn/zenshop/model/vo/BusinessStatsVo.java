package cn.zenshop.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "商家统计响应VO")
public class BusinessStatsVo {

    @Schema(description = "每日数据列表")
    private List<DailyBusinessStatsVo> dailyStats;

    @Schema(description = "当前商家总数")
    private Long currentTotal;

    @Schema(description = "待审核数量")
    private Long pendingCount;

    @Schema(description = "正常运营数量")
    private Long activeCount;

    @Schema(description = "已冻结数量")
    private Long frozenCount;
}
