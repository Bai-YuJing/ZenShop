package cn.zenshop.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("business_address")
@Schema(description = "商家地址")
public class BusinessAddress {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;

    @Schema(description = "商家ID")
    private String businessId;

    @Schema(description = "省份ID")
    private Integer provinceId;

    @Schema(description = "城市ID")
    private Integer cityId;

    @Schema(description = "区县ID")
    private Integer districtId;

    @Schema(description = "详细地址")
    private String detailedAddress;
}
