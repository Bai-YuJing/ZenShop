package cn.zenshop.server.service.impl;

import cn.zenshop.common.constant.MessageConstant;
import cn.zenshop.common.context.ThreadLocalContext;
import cn.zenshop.common.exception.BaseException;
import cn.zenshop.model.dto.CouponDto;
import cn.zenshop.model.entity.Coupon;
import cn.zenshop.model.entity.CouponDiscount;
import cn.zenshop.model.entity.CouponFullReduction;
import cn.zenshop.model.entity.UserCoupon;
import cn.zenshop.model.vo.CouponVo;
import cn.zenshop.server.mapper.CouponDiscountMapper;
import cn.zenshop.server.mapper.CouponFullReductionMapper;
import cn.zenshop.server.mapper.CouponMapper;
import cn.zenshop.server.mapper.UserCouponMapper;
import cn.zenshop.server.service.CouponService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private CouponFullReductionMapper couponFullReductionMapper;

    @Autowired
    private CouponDiscountMapper couponDiscountMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Override
    public List<CouponVo> getAllCoupons() {
        // 1. 查询所有上架的优惠券
        List<Coupon> coupons = couponMapper.selectList(
                new LambdaQueryWrapper<Coupon>()
                        .eq(Coupon::getStatus, 1));

        if (coupons.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> couponIds = coupons.stream()
                .map(Coupon::getId)
                .collect(Collectors.toList());

        // 2. 批量查询满减券和折扣券详情
        List<CouponFullReduction> frList = couponFullReductionMapper.selectList(
                new LambdaQueryWrapper<CouponFullReduction>().in(CouponFullReduction::getCouponId, couponIds));
        List<CouponDiscount> discList = couponDiscountMapper.selectList(
                new LambdaQueryWrapper<CouponDiscount>().in(CouponDiscount::getCouponId, couponIds));

        Map<Long, CouponFullReduction> frMap = frList.stream()
                .collect(Collectors.toMap(CouponFullReduction::getCouponId, f -> f));
        Map<Long, CouponDiscount> discMap = discList.stream()
                .collect(Collectors.toMap(CouponDiscount::getCouponId, d -> d));

        // 3. 组装VO
        List<CouponVo> result = new ArrayList<>();
        for (Coupon coupon : coupons) {
            CouponVo vo = buildCouponVo(coupon, frMap, discMap);
            result.add(vo);
        }

        // 4. 标记当前用户已领取的优惠券
        Long userId = ThreadLocalContext.get();
        if (userId != null) {
            List<UserCoupon> userCoupons = userCouponMapper.selectList(
                    new LambdaQueryWrapper<UserCoupon>()
                            .eq(UserCoupon::getUserId, userId)
                            .in(UserCoupon::getCouponId, couponIds));
            Set<Long> claimedIds = userCoupons.stream()
                    .map(UserCoupon::getCouponId)
                    .collect(Collectors.toSet());
            for (CouponVo vo : result) {
                vo.setClaimed(claimedIds.contains(vo.getId()));
            }
        }

        return result;
    }

    @Override
    @Transactional
    public void claimCoupon(Long couponId) {
        Long userId = ThreadLocalContext.get();
        if (userId == null) {
            throw new BaseException(MessageConstant.NOT_LOGIN);
        }

        // 1. 校验优惠券
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new BaseException(MessageConstant.COUPON_NOT_FOUND);
        }
        if (coupon.getStatus() != 1) {
            throw new BaseException(MessageConstant.COUPON_OFFLINE);
        }

        // 2. 校验每人限领一张
        Long count = userCouponMapper.selectCount(
                new LambdaQueryWrapper<UserCoupon>()
                        .eq(UserCoupon::getUserId, userId)
                        .eq(UserCoupon::getCouponId, couponId));
        if (count > 0) {
            throw new BaseException(MessageConstant.COUPON_ALREADY_CLAIMED);
        }

        // 3. 扣减库存（原子操作，null=无限）
        if (coupon.getTotal() != null) {
            int updated = couponMapper.update(null,
                    new LambdaUpdateWrapper<Coupon>()
                            .eq(Coupon::getId, couponId)
                            .gt(Coupon::getTotal, 0)
                            .setSql("total = total - 1"));
            if (updated == 0) {
                throw new BaseException(MessageConstant.COUPON_SOLD_OUT);
            }
        }

        // 4. 领取
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus(0);
        userCoupon.setCreatedTime(LocalDateTime.now());
        userCouponMapper.insert(userCoupon);

        log.info("用户 {} 领取优惠券 {}", userId, couponId);
    }

    // ===== 管理端 =====

    @Override
    public IPage<CouponVo> adminGetCouponPage(CouponDto dto) {
        IPage<Coupon> page;
        if (dto.getRemainingSort() != null && !dto.getRemainingSort().isEmpty()) {
            page = couponMapper.selectCouponPage(
                    new Page<>(Optional.ofNullable(dto.getPage()).orElse(0),
                                Optional.ofNullable(dto.getSize()).orElse(10)),
                    dto);
        } else {
            page = couponMapper.selectPage(
                    new Page<>(Optional.ofNullable(dto.getPage()).orElse(0),
                                Optional.ofNullable(dto.getSize()).orElse(10)),
                    new LambdaQueryWrapper<Coupon>()
                            .like(dto.getName() != null, Coupon::getName, dto.getName())
                            .eq(dto.getType() != null, Coupon::getType, dto.getType())
                            .eq(dto.getStatus() != null, Coupon::getStatus, dto.getStatus())
                            .orderByDesc(Coupon::getCreatedTime)
            );
        }
        if (page.getRecords().isEmpty()) {
            return new Page<>(page.getCurrent(), page.getSize(), 0);
        }

        List<Long> couponIds = page.getRecords().stream()
                .map(Coupon::getId).collect(Collectors.toList());

        List<CouponFullReduction> frList = couponFullReductionMapper.selectList(
                new LambdaQueryWrapper<CouponFullReduction>().in(CouponFullReduction::getCouponId, couponIds));
        List<CouponDiscount> discList = couponDiscountMapper.selectList(
                new LambdaQueryWrapper<CouponDiscount>().in(CouponDiscount::getCouponId, couponIds));

        Map<Long, CouponFullReduction> frMap = frList.stream()
                .collect(Collectors.toMap(CouponFullReduction::getCouponId, f -> f));
        Map<Long, CouponDiscount> discMap = discList.stream()
                .collect(Collectors.toMap(CouponDiscount::getCouponId, d -> d));

        return page.convert(coupon -> buildCouponVo(coupon, frMap, discMap));
    }

    @Override
    public CouponVo adminGetCouponById(Long id) {
        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new BaseException(MessageConstant.COUPON_NOT_FOUND);
        }
        List<Long> couponIds = Collections.singletonList(id);

        List<CouponFullReduction> frList = couponFullReductionMapper.selectList(
                new LambdaQueryWrapper<CouponFullReduction>().in(CouponFullReduction::getCouponId, couponIds));
        List<CouponDiscount> discList = couponDiscountMapper.selectList(
                new LambdaQueryWrapper<CouponDiscount>().in(CouponDiscount::getCouponId, couponIds));

        Map<Long, CouponFullReduction> frMap = frList.stream()
                .collect(Collectors.toMap(CouponFullReduction::getCouponId, f -> f));
        Map<Long, CouponDiscount> discMap = discList.stream()
                .collect(Collectors.toMap(CouponDiscount::getCouponId, d -> d));

        return buildCouponVo(coupon, frMap, discMap);
    }

    @Override
    @Transactional
    public void adminCreateCoupon(CouponDto dto) {
        if (dto.getType() == null || dto.getName() == null) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }

        // 1. 保存优惠券主表
        Coupon coupon = new Coupon();
        coupon.setType(dto.getType());
        coupon.setName(dto.getName());
        coupon.setDescription(dto.getDescription());
        coupon.setValidFrom(dto.getValidFrom());
        coupon.setValidTo(dto.getValidTo());
        coupon.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        coupon.setTotal(dto.getTotal());
        couponMapper.insert(coupon);
        Long couponId = coupon.getId();

        // 2. 保存类型详情
        if (dto.getType() == 1) {
            CouponFullReduction fr = new CouponFullReduction();
            fr.setCouponId(couponId);
            fr.setMinAmount(dto.getMinAmount());
            fr.setDiscountAmount(dto.getDiscountAmount());
            couponFullReductionMapper.insert(fr);
        } else if (dto.getType() == 2) {
            CouponDiscount disc = new CouponDiscount();
            disc.setCouponId(couponId);
            disc.setDiscountRate(dto.getDiscountRate());
            disc.setMaxDiscountAmount(dto.getMaxDiscountAmount());
            couponDiscountMapper.insert(disc);
        }

        log.info("管理员创建优惠券: id={}, name={}", couponId, dto.getName());
    }

    @Override
    @Transactional
    public void adminUpdateCoupon(CouponDto dto) {
        if (dto.getId() == null) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }
        Coupon exist = couponMapper.selectById(dto.getId());
        if (exist == null) {
            throw new BaseException(MessageConstant.COUPON_NOT_FOUND);
        }

        // 1. 更新主表
        Coupon coupon = new Coupon();
        coupon.setId(dto.getId());
        coupon.setName(dto.getName());
        coupon.setDescription(dto.getDescription());
        coupon.setValidFrom(dto.getValidFrom());
        coupon.setValidTo(dto.getValidTo());
        coupon.setStatus(dto.getStatus());
        coupon.setTotal(dto.getTotal());
        couponMapper.updateById(coupon);

        // 2. 更新类型详情（先删后插）
        if (dto.getType() == 1) {
            couponFullReductionMapper.delete(
                    new LambdaQueryWrapper<CouponFullReduction>().eq(CouponFullReduction::getCouponId, dto.getId()));
            CouponFullReduction fr = new CouponFullReduction();
            fr.setCouponId(dto.getId());
            fr.setMinAmount(dto.getMinAmount());
            fr.setDiscountAmount(dto.getDiscountAmount());
            couponFullReductionMapper.insert(fr);
        } else if (dto.getType() == 2) {
            couponDiscountMapper.delete(
                    new LambdaQueryWrapper<CouponDiscount>().eq(CouponDiscount::getCouponId, dto.getId()));
            CouponDiscount disc = new CouponDiscount();
            disc.setCouponId(dto.getId());
            disc.setDiscountRate(dto.getDiscountRate());
            disc.setMaxDiscountAmount(dto.getMaxDiscountAmount());
            couponDiscountMapper.insert(disc);
        }

        log.info("管理员更新优惠券: id={}", dto.getId());
    }

    @Override
    public void adminDeleteCoupon(Long id) {
        if (id == null) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }
        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new BaseException(MessageConstant.COUPON_NOT_FOUND);
        }
        coupon.setStatus(0);
        couponMapper.updateById(coupon);
        log.info("管理员下架优惠券: id={}", id);
    }

    /**
     * 构建 CouponVo（不含用户领取状态）
     */
    private CouponVo buildCouponVo(Coupon coupon, Map<Long, CouponFullReduction> frMap, Map<Long, CouponDiscount> discMap) {
        CouponVo vo = new CouponVo();
        vo.setId(coupon.getId());
        vo.setType(coupon.getType());
        vo.setName(coupon.getName());
        vo.setDescription(coupon.getDescription());
        vo.setValidFrom(coupon.getValidFrom());
        vo.setValidTo(coupon.getValidTo());
        vo.setTotal(coupon.getTotal());
        vo.setClaimed(false);

        if (coupon.getType() == 1) {
            CouponFullReduction fr = frMap.get(coupon.getId());
            if (fr != null) {
                vo.setMinAmount(fr.getMinAmount());
                vo.setDiscountAmount(fr.getDiscountAmount());
            }
        } else if (coupon.getType() == 2) {
            CouponDiscount disc = discMap.get(coupon.getId());
            if (disc != null) {
                vo.setDiscountRate(disc.getDiscountRate());
                vo.setMaxDiscountAmount(disc.getMaxDiscountAmount());
            }
        }
        return vo;
    }

    @Override
    public List<CouponVo> getUserCoupons() {
        Long userId = ThreadLocalContext.get();

        // 1. 查询当前用户的所有优惠券记录
        List<UserCoupon> userCoupons = userCouponMapper.selectList(
                new LambdaQueryWrapper<UserCoupon>()
                        .eq(UserCoupon::getUserId, userId));

        if (userCoupons.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 批量查询优惠券信息
        List<Long> couponIds = userCoupons.stream()
                .map(UserCoupon::getCouponId)
                .collect(Collectors.toList());

        List<Coupon> coupons = couponMapper.selectBatchIds(couponIds);
        Map<Long, Coupon> couponMap = coupons.stream()
                .collect(Collectors.toMap(Coupon::getId, c -> c));

        // 3. 批量查询满减券和折扣券详情
        List<CouponFullReduction> frList = couponFullReductionMapper.selectList(
                new LambdaQueryWrapper<CouponFullReduction>().in(CouponFullReduction::getCouponId, couponIds));
        List<CouponDiscount> discList = couponDiscountMapper.selectList(
                new LambdaQueryWrapper<CouponDiscount>().in(CouponDiscount::getCouponId, couponIds));

        Map<Long, CouponFullReduction> frMap = frList.stream()
                .collect(Collectors.toMap(CouponFullReduction::getCouponId, f -> f));
        Map<Long, CouponDiscount> discMap = discList.stream()
                .collect(Collectors.toMap(CouponDiscount::getCouponId, d -> d));

        // 4. 组装VO
        List<CouponVo> result = new ArrayList<>();
        for (UserCoupon uc : userCoupons) {
            Coupon coupon = couponMap.get(uc.getCouponId());
            if (coupon == null) continue;

            CouponVo vo = buildCouponVo(coupon, frMap, discMap);
            vo.setUserCouponId(uc.getId());
            vo.setUserCouponStatus(uc.getStatus());
            vo.setClaimed(true);

            result.add(vo);
        }

        return result;
    }
}
