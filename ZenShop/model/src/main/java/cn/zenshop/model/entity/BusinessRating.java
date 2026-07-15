package cn.zenshop.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("business_rating")
@Schema(description = "商家评分")
public class BusinessRating {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "商家ID")
    private String businessId;

    @Schema(description = "商品ID")
    private String productId;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "评分 1-5")
    private Integer score;

    @Schema(description = "评价内容")
    private String content;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "评价时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
