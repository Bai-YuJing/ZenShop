package cn.zenshop.server.service.impl;

import cn.zenshop.model.dto.ProductRatingDto;
import cn.zenshop.model.entity.BusinessRating;
import cn.zenshop.model.vo.BusinessRatingVo;
import cn.zenshop.server.mapper.BusinessRatingMapper;
import cn.zenshop.server.service.BusinessRatingService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BusinessRatingServiceImpl extends ServiceImpl<BusinessRatingMapper, BusinessRating> implements BusinessRatingService {

    @Autowired
    private BusinessRatingMapper businessRatingMapper;

    @Override
    public IPage<BusinessRatingVo> getProductRatings(ProductRatingDto dto) {
        Page<BusinessRatingVo> page = new Page<>(dto.getPage(), dto.getSize());
        return businessRatingMapper.selectProductRatings(page, dto);
    }
}
