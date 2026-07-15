package cn.zenshop.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("user_address")
@Schema(description = "用户地址")
public class UserAddress {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "省份ID")
    private Integer provinceId;

    @Schema(description = "城市ID")
    private Integer cityId;

    @Schema(description = "区县ID")
    private Integer districtId;

    @Schema(description = "收货人姓名")
    private String name;

    @Schema(description = "收货人手机号")
    private String phone;

    @Schema(description = "详细地址")
    private String detailedAddress;

    @Schema(description = "是否删除: 1=未删, 0=已删")
    @TableLogic
    private Integer isDelete;
}

