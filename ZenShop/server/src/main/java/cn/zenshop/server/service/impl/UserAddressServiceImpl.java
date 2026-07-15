package cn.zenshop.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zenshop.common.constant.CommonConstant;
import cn.zenshop.common.constant.MessageConstant;
import cn.zenshop.common.context.ThreadLocalContext;
import cn.zenshop.common.exception.BaseException;
import cn.zenshop.model.dto.UserAddressDto;
import cn.zenshop.model.entity.UserAddress;
import cn.zenshop.model.entity.UserDefaultAddress;
import cn.zenshop.model.vo.UserAddressListVo;
import cn.zenshop.server.mapper.UserAddressMapper;
import cn.zenshop.server.service.UserAddressService;
import cn.zenshop.server.service.UserDefaultAddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {
    @Autowired
    private UserDefaultAddressService userDefaultAddressService;

    @Override
    @Transactional
    public void addAddress(UserAddressDto dto) {
        Long id = ThreadLocalContext.get();

        // 查询用户已保存的地址数量
        long count = lambdaQuery().eq(UserAddress::getUserId, id).count();
        if (count >= 20) {
            throw new BaseException(MessageConstant.ADDRESS_LIMIT);
        }
        // 保存地址
        UserAddress address = BeanUtil.copyProperties(dto, UserAddress.class);
        address.setUserId(id);
        save(address);

        // 设置默认地址
        setDefaultAddress(address.getUserId(), address.getId(), dto.getIsDefault());
    }

    /**
     * 根据用户id获取地址信息
     * @return
     */
    @Override
    public UserAddressListVo getAddressByUserId() {
        Long id = ThreadLocalContext.get();
        List<UserAddress> list = lambdaQuery().eq(UserAddress::getUserId, id).list();
        UserAddressListVo vo = new UserAddressListVo();
        vo.setUserAddressVos(list);
        UserDefaultAddress userDefaultAddress = userDefaultAddressService
                .lambdaQuery()
                .eq(UserDefaultAddress::getUserId, id)
                .one();
        if (BeanUtil.isNotEmpty(userDefaultAddress)) {
            vo.setDefaultId(userDefaultAddress.getAddressId());
        }
        return vo;
    }

    @Override
    public void updateAddress(UserAddressDto dto) {
        UserAddress userAddress = BeanUtil.copyProperties(dto, UserAddress.class);
        userAddress.setUserId(ThreadLocalContext.get());
        updateById(userAddress);

        // 设置默认地址
        setDefaultAddress(userAddress.getUserId(), userAddress.getId(), dto.getIsDefault());
    }

    /**
     * 删除地址
     * @param id 地址ID
     */
    @Override
    @Transactional
    public void deleteAddress(Integer id) {
        Long userId = ThreadLocalContext.get();
        // 校验地址归属
        UserAddress address = getById(id);
        if (BeanUtil.isEmpty(address) || !address.getUserId().equals(userId)) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }
        // 如果删除的是默认地址，同时移除默认地址记录
        UserDefaultAddress defaultAddress = userDefaultAddressService
                .lambdaQuery()
                .eq(UserDefaultAddress::getAddressId, id)
                .one();
        if (BeanUtil.isNotEmpty(defaultAddress)) {
            userDefaultAddressService.removeById(defaultAddress.getId());
        }
        // 逻辑删除地址
        removeById(id);
    }

    /**
     * 设为默认地址
     * @param addressId 地址ID
     */
    @Override
    @Transactional
    public void setDefaultAddress(Integer addressId) {
        Long userId = ThreadLocalContext.get();
        // 校验地址归属
        UserAddress address = getById(addressId);
        if (BeanUtil.isEmpty(address) || !address.getUserId().equals(userId)) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }
        // 先删后增，设置为默认地址
        userDefaultAddressService
                .lambdaUpdate()
                .eq(UserDefaultAddress::getUserId, userId)
                .remove();
        UserDefaultAddress userDefaultAddress = new UserDefaultAddress();
        userDefaultAddress.setAddressId(addressId);
        userDefaultAddress.setUserId(userId);
        userDefaultAddressService.save(userDefaultAddress);
    }

    /**
     * 设置默认地址：用户没有默认地址 或 主动要求设为默认时生效
     * @param userId    用户ID
     * @param addressId 地址ID
     * @param isDefault 是否设为默认（1=是）
     */
    private void setDefaultAddress(Long userId, Integer addressId, Integer isDefault) {
        boolean hasDefault = userDefaultAddressService
                .lambdaQuery()
                .eq(UserDefaultAddress::getUserId, userId)
                .exists();

        if (!hasDefault || Objects.equals(isDefault, CommonConstant.IS_DEFAULT)) {
            userDefaultAddressService
                    .lambdaUpdate()
                    .eq(UserDefaultAddress::getUserId, userId)
                    .remove();
            UserDefaultAddress userDefaultAddress = new UserDefaultAddress();
            userDefaultAddress.setAddressId(addressId);
            userDefaultAddress.setUserId(userId);
            userDefaultAddressService.save(userDefaultAddress);
        }
    }
}
