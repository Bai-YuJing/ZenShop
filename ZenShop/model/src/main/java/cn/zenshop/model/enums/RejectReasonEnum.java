package cn.zenshop.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "驳回原因枚举")
public enum RejectReasonEnum {

    @Schema(description = "信息不完整")
    INCOMPLETE_INFO(1),

    @Schema(description = "证件不符")
    DOCUMENT_MISMATCH(2),

    @Schema(description = "经营范围不符")
    BUSINESS_SCOPE_MISMATCH(3),

    @Schema(description = "资质过期")
    QUALIFICATION_EXPIRED(4),

    @Schema(description = "其他")
    OTHER(5);

    @EnumValue
    @JsonValue
    private final Integer value;
}
