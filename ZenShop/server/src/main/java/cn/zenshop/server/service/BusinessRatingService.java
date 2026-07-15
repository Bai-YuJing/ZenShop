package cn.zenshop.server.service;

import cn.zenshop.model.dto.ProductRatingDto;
import cn.zenshop.model.entity.BusinessRating;
import cn.zenshop.model.vo.BusinessRatingVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface BusinessRatingService extends IService<BusinessRating> {
    IPage<BusinessRatingVo> getProductRatings(ProductRatingDto dto);
}
