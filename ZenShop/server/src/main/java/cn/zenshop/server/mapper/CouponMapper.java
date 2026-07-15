package cn.zenshop.server.mapper;

import cn.zenshop.model.dto.CouponDto;
import cn.zenshop.model.entity.Coupon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {

    IPage<Coupon> selectCouponPage(Page<Coupon> page, @Param("dto") CouponDto dto);
}
