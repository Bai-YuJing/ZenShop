package cn.zenshop.server.service.impl;

import cn.zenshop.model.entity.BusinessAddress;
import cn.zenshop.server.mapper.BusinessAddressMapper;
import cn.zenshop.server.service.BusinessAddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BusinessAddressServiceImpl extends ServiceImpl<BusinessAddressMapper, BusinessAddress> implements BusinessAddressService {
}
