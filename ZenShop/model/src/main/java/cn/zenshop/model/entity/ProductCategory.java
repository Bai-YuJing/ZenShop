package cn.zenshop.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("product_category")
@Schema(description = "商品分类")
public class ProductCategory {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "商家ID")
    private String businessId;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @Schema(description = "是否上架: 1=上架, 0=下架")
    private Integer status;
}

