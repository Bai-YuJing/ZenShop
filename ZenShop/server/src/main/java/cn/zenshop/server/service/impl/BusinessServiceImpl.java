package cn.zenshop.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zenshop.common.constant.CommonConstant;
import cn.zenshop.common.constant.MessageConstant;
import cn.zenshop.common.constant.RedisConstant;
import cn.zenshop.common.context.ThreadLocalContext;
import cn.zenshop.common.exception.BaseException;
import cn.zenshop.model.dto.BusinessLoginDto;
import cn.zenshop.model.dto.BusinessPageDto;
import cn.zenshop.model.dto.BusinessRegisterDto;
import cn.zenshop.model.dto.BusinessReviewDto;
import cn.zenshop.model.entity.Business;
import cn.zenshop.model.entity.BusinessAddress;
import cn.zenshop.model.entity.BusinessReviewReason;
import cn.zenshop.model.enums.BusinessStatusEnum;
import cn.zenshop.model.vo.BusinessDetailVo;
import cn.zenshop.model.vo.BusinessPageVo;
import cn.zenshop.model.vo.BusinessVo;
import cn.zenshop.model.vo.DailyOrderStatsVo;
import cn.zenshop.model.vo.DailyRatingStatsVo;
import cn.zenshop.model.vo.ProductCategoryVo;
import cn.zenshop.server.mapper.BusinessMapper;
import cn.zenshop.server.mapper.BusinessRatingMapper;
import cn.zenshop.server.mapper.OrderMapper;
import cn.zenshop.server.service.ProductCategoryService;
import cn.zenshop.server.service.BusinessAddressService;
import cn.zenshop.server.service.BusinessReviewReasonService;
import cn.zenshop.server.service.BusinessService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BusinessServiceImpl extends ServiceImpl<BusinessMapper, Business> implements BusinessService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private BusinessAddressService businessAddressService;
    @Autowired
    private BusinessMapper businessMapper;
    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private BusinessReviewReasonService businessReviewReasonService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private BusinessRatingMapper businessRatingMapper;

    private static final Map<Integer, Set<Integer>> ALLOWED_TRANSITIONS = Map.of(
            1, Set.of(2, 5),  // 待审核 → 正常, 驳回
            2, Set.of(3, 4),  // 正常 → 冻结, 关闭
            3, Set.of(1, 4),  // 冻结 → 待审核, 关闭
            4, Set.of(2),     // 已关闭 → 正常（开店）
            6, Set.of(2)      // 修改审核 → 审核未通过
    );

    private static final Map<Integer, Set<Integer>> ALLOWED_TRANSITIONS_WITH_REASON = Map.of(
            1, Set.of(5),  // 待审核 → 驳回
            2, Set.of(3),  // 正常 → 冻结
            6, Set.of(2)   // 修改审核 → 审核未通过
    );

    /**
     * 获取验证码
     * @param phone
     */
    @Override
    public void getCaptcha(String phone) {
        String code = RandomUtil.randomString(6);
        log.info("验证码：{}",code);
        String key= RedisConstant.BUSINESS_CAPTCHA_KEY+phone;
        stringRedisTemplate.opsForValue().set(key,code,RedisConstant.BUSINESS_CAPTCHA_KEY_TTL, TimeUnit.SECONDS);
    }

    /**
     * 注册
     * @param dto
     * @return
     */
    @Override
    @Transactional
    public String register(BusinessRegisterDto dto) {
        log.info("注册：{}",dto);
        //获取验证码
        String key=RedisConstant.BUSINESS_CAPTCHA_KEY+dto.getPhone();
        String code = stringRedisTemplate.opsForValue().get(key);
        //删除redis保存的验证码
        stringRedisTemplate.delete(key);
        //判断验证码是否过期
        if (StrUtil.isBlank(code)){
            throw new BaseException(MessageConstant.CAPTCHA_NOT_FOUND);
        }
        //判断验证码是否正确
        if (!code.equals(dto.getCaptcha())){
            throw new BaseException(MessageConstant.CAPTCHA_ERROR);
        }
        //判断手机号是否被注册
        Business business = lambdaQuery().eq(Business::getPhone, dto.getPhone()).one();
        if (BeanUtil.isNotEmpty(business)){
            // 已注册
            throw new BaseException(MessageConstant.PHONE_SAME);
        }
        //保存商家信息
        Business saveaBusiness = BeanUtil.copyProperties(dto, Business.class);
        saveaBusiness.setStatus(BusinessStatusEnum.PENDING_SUBMIT);
        saveaBusiness.setIsDelete(CommonConstant.IS_NOT_DELETE);
        save(saveaBusiness);
        //保存地址信息
        BusinessAddress businessAddress = BeanUtil.copyProperties(dto, BusinessAddress.class);
        businessAddress.setBusinessId(saveaBusiness.getId());
        businessAddressService.save(businessAddress);
        //  自动登录
        String token = UUID.randomUUID().toString();
        //保存token到redis
        String tokenKey=RedisConstant.BUSINESS_TOKEN+token;
        stringRedisTemplate.opsForValue().set(tokenKey,saveaBusiness.getId(),RedisConstant.BUSINESS_TOKEN_TTL,TimeUnit.SECONDS);

        //更新登陆时间
        lambdaUpdate().set(Business::getLastLoginTime, LocalDateTime.now()).eq(Business::getId,saveaBusiness.getId()).update();
        return token;
    }

    /**
     * 登陆
     * @param dto
     * @return
     */
    @Override
    public String businessLogin(BusinessLoginDto dto) {
        Business business = lambdaQuery().eq(Business::getPhone, dto.getPhone()).one();
        if (BeanUtil.isEmpty(business)){
            throw new BaseException(MessageConstant.PHONE_NOT_REGISTER);
        }
        //获取存放在redis中的验证码
        String key=RedisConstant.BUSINESS_CAPTCHA_KEY+dto.getPhone();
        String code = stringRedisTemplate.opsForValue().get(key);
        //删除redis保存的验证码
        stringRedisTemplate.delete(key);
        if (StrUtil.isEmpty(code) || !dto.getCaptcha().equals(code)){
            throw new BaseException(MessageConstant.CAPTCHA_ERROR);
        }
        String token = UUID.randomUUID().toString();
        //保存token到redis
        String tokenKey=RedisConstant.BUSINESS_TOKEN+token;
        stringRedisTemplate.opsForValue().set(tokenKey,business.getId(),RedisConstant.BUSINESS_TOKEN_TTL,TimeUnit.SECONDS);
        //更新登陆时间
        lambdaUpdate().set(Business::getLastLoginTime, LocalDateTime.now()).eq(Business::getId,business.getId()).update();
        return token;
    }


    /**
     * 提交审核
     * @param dto
     */
    @Transactional
    @Override
    public void review(BusinessReviewDto dto) {
        Business business = BeanUtil.copyProperties(dto, Business.class);
        business.setStatus(BusinessStatusEnum.PENDING_REVIEW);
        businessAddressService.lambdaUpdate()
                .eq(BusinessAddress::getBusinessId,dto.getId())
                .set(BusinessAddress::getDetailedAddress,dto.getDetailedAddress())
                .update();
        updateById(business);
    }

    /**
     * 分页查询员工信息
     * @param page
     * @param pageDto
     * @return
     */
    @Override
    public IPage<BusinessPageVo> businessPage(Page<BusinessVo> page, BusinessPageDto pageDto) {
        return businessMapper.adminGetBusinessPage(page,pageDto);
    }

    /**
     * 根据id获取员工信息
     * @param id
     * @return
     */
    @Override
    public BusinessVo getBusinessById(String id) {
        return businessMapper.getBusinessById(id);
    }

    @Override
    public BusinessDetailVo getBusinessDetail(String id) {
        BusinessVo businessVo = businessMapper.getBusinessById(id);
        if (businessVo == null) {
            throw new BaseException(MessageConstant.SHOP_NOT_FOUND);
        }
        BusinessDetailVo detailVo = BeanUtil.copyProperties(businessVo, BusinessDetailVo.class);
        List<ProductCategoryVo> categories = productCategoryService.getAll(id, null);
        detailVo.setCategories(categories);
        return detailVo;
    }

    /**
     * 修改状态
     * @param id
     * @param status
     */
    @Override
    @Transactional
    public void updateStatus(String id, Integer status) {
        // 1. 参数校验
        if (id == null || status == null) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }
        if (status < 0 || status > 6) {
            throw new BaseException(MessageConstant.UN_KNOW_STATUS);
        }

        // 2. 查询商家
        Business business = lambdaQuery().eq(Business::getId, id).one();
        if (BeanUtil.isEmpty(business)) {
            throw new BaseException(MessageConstant.USER_NOT_FOUND);
        }

        // 3. 状态流转校验
        Integer currentStatus = business.getStatus().getValue();
        if (!ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Set.of()).contains(status)) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }
        // 4. 更新
        lambdaUpdate()
                .eq(Business::getId, id)
                .set(Business::getStatus, status)
                .set(Business::getUpdatedTime, LocalDateTime.now())
                .update();

        // 5. 审核成功时清除该商家的驳回/冻结原因记录
        if (status == 2) {
            businessReviewReasonService.lambdaUpdate()
                    .eq(BusinessReviewReason::getBusinessId, id)
                    .remove();
        }
    }

    /**
     * 修改状态（驳回/冻结）
     * @param id
     * @param status
     * @param reason
     */
    @Transactional
    @Override
    public void updateStatus(String id, Integer status, Integer reason) {
        // 1. 参数校验
        if (id == null || status == null || reason == null) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }
        if (status < 0 || status > 6) {
            throw new BaseException(MessageConstant.UN_KNOW_STATUS);
        }

        // 2. 查询商家
        Business business = lambdaQuery().eq(Business::getId, id).one();
        if (BeanUtil.isEmpty(business)) {
            throw new BaseException(MessageConstant.USER_NOT_FOUND);
        }

        // 3. 状态流转校验（只允许：待审核→驳回、正常→冻结、修改审核→审核未通过）
        Integer currentStatus = business.getStatus().getValue();
        if (!ALLOWED_TRANSITIONS_WITH_REASON.getOrDefault(currentStatus, Set.of()).contains(status)) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }

        // 4. 更新商家状态
        lambdaUpdate()
                .eq(Business::getId, id)
                .set(Business::getStatus, status)
                .set(Business::getUpdatedTime, LocalDateTime.now())
                .update();

        // 5. 保存审核原因
        BusinessReviewReason reviewReason = new BusinessReviewReason();
        reviewReason.setBusinessId(id);
        reviewReason.setAdminId(ThreadLocalContext.get());
        // type: 1=驳回(REJECTED=5), 2=冻结(FROZEN=3)
        reviewReason.setType(status == 3 ? 1 : 2);
        reviewReason.setReason(reason);
        businessReviewReasonService.save(reviewReason);
    }

    @Override
    public void logout(String token) {
        String tokenKey = RedisConstant.BUSINESS_TOKEN + token;
        stringRedisTemplate.delete(tokenKey);
        ThreadLocalContext.remove();
    }

    /**
     * 修改信息前置
     * @param dto
     */
    @Override
    @Transactional
    public void updateBusiness(BusinessReviewDto dto) {
        // 1. 更新商家状态为修改审核
        lambdaUpdate().set(Business::getStatus,BusinessStatusEnum.MODIFYING)
                .eq(Business::getId,dto.getId())
                .update();

        // 2. 将修改信息存入redis
        String key = RedisConstant.BUSINESS_MODIFY_REVIEW_KEY + dto.getId();
        String jsonStr = JSONUtil.toJsonStr(dto);
        stringRedisTemplate.opsForValue().set(key, jsonStr, RedisConstant.BUSINESS_MODIFY_REVIEW_KEY_TTL, TimeUnit.SECONDS);
    }

    /**
     * 修改信息
     */
    @Override
    public void updateBusinessInfo(String id) {
        String key = RedisConstant.BUSINESS_MODIFY_REVIEW_KEY + id;
        String jsonStr = stringRedisTemplate.opsForValue().get(key);
        BusinessReviewDto dto = JSONUtil.toBean(jsonStr, BusinessReviewDto.class);
        //保存商家信息
        Business saveaBusiness = BeanUtil.copyProperties(dto, Business.class);
        saveaBusiness.setStatus(BusinessStatusEnum.ACTIVE);
        updateById(saveaBusiness);
        //保存地址信息
        BusinessAddress businessAddress = BeanUtil.copyProperties(dto, BusinessAddress.class);
        businessAddressService.lambdaUpdate()
                .eq(BusinessAddress::getBusinessId,saveaBusiness.getId())
                .update(businessAddress);
        stringRedisTemplate.delete(key);
            
    }

    @Override
    public BusinessVo getUpdateInfo(String id) {
        String key = RedisConstant.BUSINESS_MODIFY_REVIEW_KEY + id;
        String jsonStr = stringRedisTemplate.opsForValue().get(key);
        BusinessReviewDto dto = JSONUtil.toBean(jsonStr, BusinessReviewDto.class);
        BusinessVo business = businessMapper.getBusinessById(id);
        //用redis中待修改的数据修改查询结果
        business.setLogo(dto.getLogo());
        business.setDescription(dto.getDescription());
        business.setEmail(dto.getEmail());
        business.setCategoryId(dto.getCategoryId());
        business.setProvinceId(dto.getProvinceId());
        business.setCityId(dto.getCityId());
        business.setDistrictId(dto.getDistrictId());
        business.setDetailedAddress(dto.getDetailedAddress());

        return business;
    }

    /**
     * 用户端商家分页查询
     * @param page
     * @param dto
     * @return
     */
    @Override
    public IPage<BusinessPageVo> userGetBusinessPage(Page<BusinessVo> page, BusinessPageDto dto) {
        return businessMapper.userGetBusinessPage(page,dto);
    }

    @Override
    public List<DailyOrderStatsVo> getDailyOrderStats(String range) {
        Long businessId = ThreadLocalContext.get();
        List<DailyOrderStatsVo> result = new java.util.ArrayList<>();
        LocalDate today = LocalDate.now();
        int days = parseRange(range);

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);

            Long count = orderMapper.selectCount(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<cn.zenshop.model.entity.Order>()
                            .eq(cn.zenshop.model.entity.Order::getBusinessId, businessId)
                            .eq(cn.zenshop.model.entity.Order::getStatus, 3)
                            .between(cn.zenshop.model.entity.Order::getCreatedTime, start, end));

            List<cn.zenshop.model.entity.Order> orders = orderMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<cn.zenshop.model.entity.Order>()
                            .eq(cn.zenshop.model.entity.Order::getBusinessId, businessId)
                            .eq(cn.zenshop.model.entity.Order::getStatus, 3)
                            .between(cn.zenshop.model.entity.Order::getCreatedTime, start, end));
            BigDecimal revenue = orders.stream()
                    .map(cn.zenshop.model.entity.Order::getActualAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            DailyOrderStatsVo vo = new DailyOrderStatsVo();
            vo.setDate(date.toString());
            vo.setOrderCount(count.intValue());
            vo.setRevenue(revenue);
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<DailyRatingStatsVo> getDailyRatingStats(String range) {
        Long businessId = ThreadLocalContext.get();
        List<DailyRatingStatsVo> result = new java.util.ArrayList<>();
        LocalDate today = LocalDate.now();
        int days = parseRange(range);

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);

            Long good = businessRatingMapper.selectCount(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<cn.zenshop.model.entity.BusinessRating>()
                            .eq(cn.zenshop.model.entity.BusinessRating::getBusinessId, String.valueOf(businessId))
                            .ge(cn.zenshop.model.entity.BusinessRating::getScore, 4)
                            .between(cn.zenshop.model.entity.BusinessRating::getCreatedTime, start, end));

            Long medium = businessRatingMapper.selectCount(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<cn.zenshop.model.entity.BusinessRating>()
                            .eq(cn.zenshop.model.entity.BusinessRating::getBusinessId, String.valueOf(businessId))
                            .eq(cn.zenshop.model.entity.BusinessRating::getScore, 3)
                            .between(cn.zenshop.model.entity.BusinessRating::getCreatedTime, start, end));

            Long bad = businessRatingMapper.selectCount(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<cn.zenshop.model.entity.BusinessRating>()
                            .eq(cn.zenshop.model.entity.BusinessRating::getBusinessId, String.valueOf(businessId))
                            .le(cn.zenshop.model.entity.BusinessRating::getScore, 2)
                            .between(cn.zenshop.model.entity.BusinessRating::getCreatedTime, start, end));

            DailyRatingStatsVo vo = new DailyRatingStatsVo();
            vo.setDate(date.toString());
            vo.setGoodCount(good.intValue());
            vo.setMediumCount(medium.intValue());
            vo.setBadCount(bad.intValue());
            result.add(vo);
        }
        return result;
    }

    private int parseRange(String range) {
        if ("week".equals(range)) {
            return LocalDate.now().getDayOfWeek().getValue();
        } else if ("month".equals(range)) {
            return LocalDate.now().getDayOfMonth();
        } else if ("30d".equals(range)) {
            return 30;
        } else {
            return 7;
        }
    }
}
