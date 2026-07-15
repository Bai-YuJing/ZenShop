package cn.zenshop.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("user_default_address")
@Schema(description = "用户默认地址")
public class UserDefaultAddress {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;

    @Schema(description = "地址ID")
    private Integer addressId;

    @Schema(description = "用户ID")
    private Long userId;
}
