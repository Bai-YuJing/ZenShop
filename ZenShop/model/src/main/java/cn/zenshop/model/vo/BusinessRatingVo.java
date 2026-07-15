package cn.zenshop.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "商家评分")
public class BusinessRatingVo {
    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "评分 1-5")
    private Integer score;

    @Schema(description = "评价内容")
    private String content;

    @Schema(description = "评价时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdTime;
}
