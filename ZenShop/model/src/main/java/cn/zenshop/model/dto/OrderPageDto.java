package cn.zenshop.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "订单分页查询DTO")
public class OrderPageDto {

    @Schema(description = "页码")
    private Integer page = 0;

    @Schema(description = "每页条数")
    private Integer size = 10;

    @Schema(description = "下单开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "下单结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "订单状态筛选")
    private Integer status;

    @Schema(description = "订单状态筛选（取消订单列表专用）")
    private Integer orderStatus;

    @Schema(description = "金额排序: ASC升序, DESC降序, 不传则不按金额排序")
    private String amountSort;

    @Schema(description = "最低评分")
    private Integer minScore;

    @Schema(description = "最高评分")
    private Integer maxScore;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "是否已评价: 0=未评价, 1=已评价, 不传则全部")
    private Integer rated;
}
