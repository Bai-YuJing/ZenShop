package cn.zenshop.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("product_image")
@Schema(description = "商品图片")
public class ProductImage {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "商家ID")
    private String businessId;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "图片URL")
    private String url;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

}

