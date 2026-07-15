package cn.zenshop.model.vo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "商品分类")
public class ProductCategoryVo {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdTime;

    @Schema(description = "是否上架: 1=上架, 0=下架")
    private Integer status;

}
