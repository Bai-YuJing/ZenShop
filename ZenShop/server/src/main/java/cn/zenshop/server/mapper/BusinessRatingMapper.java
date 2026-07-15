package cn.zenshop.server.mapper;

import cn.zenshop.model.dto.ProductRatingDto;
import cn.zenshop.model.entity.BusinessRating;
import cn.zenshop.model.vo.BusinessRatingVo;
import cn.zenshop.model.vo.UserRatingVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import java.util.List;

@Mapper
public interface BusinessRatingMapper extends BaseMapper<BusinessRating> {
    IPage<BusinessRatingVo> selectProductRatings(Page<BusinessRatingVo> page, @Param("dto") ProductRatingDto dto);

    List<UserRatingVo> selectUserRatings(@Param("userId") Long userId);
}
