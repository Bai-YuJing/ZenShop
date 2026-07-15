package cn.zenshop.server.service;

import cn.zenshop.model.dto.BusinessLoginDto;
import cn.zenshop.model.dto.BusinessPageDto;
import cn.zenshop.model.dto.BusinessRegisterDto;
import cn.zenshop.model.dto.BusinessReviewDto;
import cn.zenshop.model.entity.Business;
import cn.zenshop.model.vo.BusinessDetailVo;
import cn.zenshop.model.vo.BusinessPageVo;
import cn.zenshop.model.vo.BusinessVo;
import cn.zenshop.model.vo.DailyOrderStatsVo;
import cn.zenshop.model.vo.DailyRatingStatsVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface BusinessService extends IService<Business> {
    /**
     * 获取验证码
     * @param phone
     */
    void getCaptcha(String phone);

    /**
     * 注册
     * @param businessRegisterDto
     * @return
     */
    String register(BusinessRegisterDto businessRegisterDto);

    /**
     * 登录
     * @param businessLoginDto
     * @return
     */
    String businessLogin(BusinessLoginDto businessLoginDto);

    /**
     * 提交审核
     * @param reviewDto
     */
    void review(BusinessReviewDto reviewDto);

    /**
     * 分页查询
     * @param page
     * @param pageDto
     * @return
     */
    IPage<BusinessPageVo> businessPage(Page<BusinessVo> page, BusinessPageDto pageDto);

    /**
     * 根据id获取商家信息
     * @param id
     * @return
     */
    BusinessVo getBusinessById(String id);

    /**
     * 更新状态
     * @param id
     * @param status
     * @return
     */
    void updateStatus(String id, Integer status);

    /**
     *  更新状态（冻结/驳回）
     * @param id
     * @param status
     * @param reason
     */
    void updateStatus(String id, Integer status, Integer reason);

    /**
     *  退出登录
     * @param token
     */
    void logout(String token);
    /**
     * 修改信息前置
     * @param reviewDto
     */
    void updateBusiness(BusinessReviewDto reviewDto);

    /**
     * 修改信息
     * @param id
     */
    void updateBusinessInfo(String id);

    /**
     * 获取更新信息
     * @param id
     * @return
     */
    BusinessVo getUpdateInfo(String id);

    /**
     * 用户端获取商家详情（含商品分类）
     * @param id 商家ID
     * @return BusinessDetailVo
     */
    BusinessDetailVo getBusinessDetail(String id);

    /**
     * 用户端获取商家信息
     * @param businessVoPage
     * @param pageDto
     * @return
     */
    IPage<BusinessPageVo> userGetBusinessPage(Page<BusinessVo> businessVoPage, BusinessPageDto pageDto);

    /**
     * 获取每日订单统计
     * @param range 时间范围：7d=近7天, 30d=近30天, week=本周
     * @return 每日订单数与收入
     */
    List<DailyOrderStatsVo> getDailyOrderStats(String range);

    /**
     * 获取每日评价统计
     * @param range 时间范围：7d=近7天, 30d=近30天, week=本周
     * @return 每日好评/中评/差评数量
     */
    List<DailyRatingStatsVo> getDailyRatingStats(String range);
}
