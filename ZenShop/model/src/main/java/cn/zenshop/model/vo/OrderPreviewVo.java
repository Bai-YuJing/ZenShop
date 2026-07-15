package cn.zenshop.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "订单预览VO（按商家分组）")
public class OrderPreviewVo {

    @Schema(description = "商家ID")
    private String businessId;

    @Schema(description = "商家名称")
    private String businessName;

    @Schema(description = "商家logo")
    private String logo;

    @Schema(description = "商品列表")
    private List<OrderPreviewItemVo> items;

    @Schema(description = "该商家商品总金额")
    private BigDecimal totalAmount;
}
