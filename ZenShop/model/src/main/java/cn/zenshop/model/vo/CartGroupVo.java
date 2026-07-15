package cn.zenshop.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "购物车分组VO（按商家）")
public class CartGroupVo {

    @Schema(description = "商家ID")
    private String businessId;

    @Schema(description = "商家名称")
    private String businessName;

    @Schema(description = "商家logo")
    private String logo;

    @Schema(description = "商品列表")
    private List<CartItemVo> items;
}
