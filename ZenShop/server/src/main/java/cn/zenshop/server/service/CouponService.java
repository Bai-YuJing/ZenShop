package cn.zenshop.server.service;

import cn.zenshop.model.dto.CouponDto;
import cn.zenshop.model.vo.CouponVo;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface CouponService {

    /**
     * 获取当前用户的优惠券
     * @return 用户优惠券列表（含满减/折扣详情）
     */
    List<CouponVo> getUserCoupons();

    /**
     * 查询所有上架的优惠券
     * @return 优惠券列表（含满减/折扣详情）
     */
    List<CouponVo> getAllCoupons();

    /**
     * 抢券
     * @param couponId 优惠券ID
     */
    void claimCoupon(Long couponId);

    // ===== 管理端 =====

    /**
     * 分页查询优惠券列表
     */
    IPage<CouponVo> adminGetCouponPage(CouponDto dto);

    /**
     * 根据ID获取优惠券详情
     */
    CouponVo adminGetCouponById(Long id);

    /**
     * 创建优惠券
     */
    void adminCreateCoupon(CouponDto dto);

    /**
     * 更新优惠券
     */
    void adminUpdateCoupon(CouponDto dto);

    /**
     * 删除/下架优惠券
     */
    void adminDeleteCoupon(Long id);
}
