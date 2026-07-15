package cn.zenshop.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "管理员分页查询DTO")
public class AdminPageDto {

    @Schema(description = "页码")
    private Integer page;

    @Schema(description = "每页条数")
    private Integer size;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "显示名称")
    private String nickname;

    @Schema(description = "状态: 0=禁用, 1=启用")
    private Integer status;

    @Schema(description = "最近登陆时间")
    private LocalDateTime lastLoginTime;
}
