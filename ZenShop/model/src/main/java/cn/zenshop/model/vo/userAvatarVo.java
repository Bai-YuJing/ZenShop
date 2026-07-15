package cn.zenshop.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户头像")
public class userAvatarVo {
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "头像")
    private String avatar;
}
