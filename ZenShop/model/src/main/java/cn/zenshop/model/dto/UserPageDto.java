package cn.zenshop.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户分页查询DTO")
public class UserPageDto {

    @Schema(description = "页码")
    private Integer page = 0;

    @Schema(description = "每页条数")
    private Integer size = 10;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "状态: 0=禁用, 1=启用")
    private Integer status;
}
