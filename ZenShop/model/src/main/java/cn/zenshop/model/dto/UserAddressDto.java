package cn.zenshop.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("user_address")
@Schema(description = "用户地址")
public class UserAddressDto {

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

    @Schema(description = "是否设置为默认地址: 1=是, 0=否")
    private Integer isDefault;
}

