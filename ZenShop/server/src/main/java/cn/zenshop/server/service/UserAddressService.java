package cn.zenshop.server.service;

import cn.zenshop.model.dto.UserAddressDto;
import cn.zenshop.model.entity.UserAddress;
import cn.zenshop.model.vo.UserAddressListVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserAddressService extends IService<UserAddress> {
    /**
     * 添加地址
     * @param userAddressDto
     */
    void addAddress(UserAddressDto userAddressDto);

    /**
     * 获取用户地址
     */
    UserAddressListVo getAddressByUserId();

    /**
     * 更新地址
     * @param userAddressDto
     */
    void updateAddress(UserAddressDto userAddressDto);

    /**
     * 删除地址
     * @param id 地址ID
     */
    void deleteAddress(Integer id);

    /**
     * 设为默认地址
     * @param id 地址ID
     */
    void setDefaultAddress(Integer id);
}
