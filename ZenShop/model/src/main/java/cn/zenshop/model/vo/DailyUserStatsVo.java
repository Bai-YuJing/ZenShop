package cn.zenshop.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "每日用户统计VO")
public class DailyUserStatsVo {

    @Schema(description = "日期")
    private String date;

    @Schema(description = "总用户数")
    private Long totalUsers;

    @Schema(description = "新增用户数")
    private Long newUsers;

    @Schema(description = "活跃用户数（当日登录）")
    private Long activeUsers;
}
