package cn.zenshop.server.service.impl;

import cn.zenshop.model.entity.ProductImage;
import cn.zenshop.server.mapper.ProductImageMapper;
import cn.zenshop.server.service.ProductImageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductImageServiceImpl extends ServiceImpl<ProductImageMapper, ProductImage> implements ProductImageService {
}
