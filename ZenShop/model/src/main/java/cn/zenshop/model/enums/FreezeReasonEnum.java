package cn.zenshop.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "冻结原因枚举")
public enum FreezeReasonEnum {

    @Schema(description = "违规经营")
    VIOLATION(1),

    @Schema(description = "投诉过多")
    EXCESSIVE_COMPLAINTS(2),

    @Schema(description = "资质异常")
    QUALIFICATION_ABNORMAL(3),

    @Schema(description = "店铺长期未运营")
    LONG_TERM_INACTIVE(4),

    @Schema(description = "其他")
    OTHER(5);

    @EnumValue
    @JsonValue
    private final Integer value;
}
