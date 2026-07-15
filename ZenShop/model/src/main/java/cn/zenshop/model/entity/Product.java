package cn.zenshop.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
@Schema(description = "商品")
public class Product {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "商家ID")
    private String businessId;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "库存")
    private Integer stock;

    @Schema(description = "销量")
    private Integer sales;

    @Schema(description = "状态: 0=下架, 1=上架")
    private Integer status;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @Schema(description = "是否删除: 1=未删, 0=已删")
    @TableLogic
    private Integer isDelete;
}

