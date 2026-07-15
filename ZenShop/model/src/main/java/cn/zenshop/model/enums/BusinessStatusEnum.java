package cn.zenshop.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "商家状态枚举")
public enum BusinessStatusEnum {

    @Schema(description = "待提交")
    PENDING_SUBMIT(0),

    @Schema(description = "待审核")
    PENDING_REVIEW(1),

    @Schema(description = "正常")
    ACTIVE(2),

    @Schema(description = "冻结")
    FROZEN(3),

    @Schema(description = "已关闭")
    CLOSED(4),

    @Schema(description = "审核未通过")
    REJECTED(5),

    @Schema(description = "修改审核")
    MODIFYING(6);

    @EnumValue
    @JsonValue
    private final Integer value;
}
