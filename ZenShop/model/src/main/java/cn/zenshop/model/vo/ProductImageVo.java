package cn.zenshop.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Schema(description = "商品图片")
public class ProductImageVo {
    @Schema(description = "图片URL")
    private String url;

}
