package cn.zenshop.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("category")
@Schema(description = "分类")
public class Category {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;

    @Schema(description = "分类名称")
    private String name;
}
