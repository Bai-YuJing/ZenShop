package cn.zenshop.server.service.impl;

import cn.zenshop.model.entity.UserDefaultAddress;
import cn.zenshop.server.mapper.UserDefaultAddressMapper;
import cn.zenshop.server.service.UserDefaultAddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDefaultAddressServiceImpl extends ServiceImpl<UserDefaultAddressMapper, UserDefaultAddress> implements UserDefaultAddressService {
}
