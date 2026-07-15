package cn.zenshop.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "管理端仪表盘统计VO")
public class AdminDashboardVo {

    @Schema(description = "总用户数")
    private Long totalUsers;

    @Schema(description = "新增用户数（昨日）")
    private Long newUsers;

    @Schema(description = "活跃用户数（昨日登录）")
    private Long activeUsers;

    @Schema(description = "总商家数")
    private Long totalBusinesses;

    @Schema(description = "新增商家数（昨日）")
    private Long newBusinesses;
}
