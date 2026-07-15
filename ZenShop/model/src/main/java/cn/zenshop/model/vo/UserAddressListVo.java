package cn.zenshop.model.vo;

import cn.zenshop.model.entity.UserAddress;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@TableName("user_address")
@Schema(description = "用户地址")
public class UserAddressListVo {

    @Schema(description = "主键ID")
    private List<UserAddress> userAddressVos ;

    @Schema(description = "默认地址id")
    private Integer defaultId;
}

