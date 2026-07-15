package cn.zenshop.server.service;

import cn.zenshop.model.dto.AdminDto;
import cn.zenshop.model.dto.AdminLoginDto;
import cn.zenshop.model.dto.AdminPageDto;
import cn.zenshop.model.vo.AdminPageVo;
import cn.zenshop.model.vo.AdminVo;
import cn.zenshop.model.vo.DailyUserStatsVo;
import cn.zenshop.model.vo.BusinessStatsVo;
import cn.zenshop.model.vo.userAvatarVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.zenshop.model.entity.Admin;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

public interface AdminService extends IService<Admin> {

    /**
     * 管理员登录
     * @param adminLoginDto
     */
    String adminLogin(AdminLoginDto adminLoginDto);

    /**
     * 获取验证码
     * @return
     */
    Map<String, String> getCaptcha();


    /**
     * 添加管理员
     * @param adminDto
     */
    void addAdminUser(@Valid AdminDto adminDto);

    /**
     * 更新管理员信息
     * @param adminDto
     */
    void adminUpdate(AdminDto adminDto);

    /**
     * 根据id获取管理员信息
     * @param id
     * @return
     */

    AdminVo getAdminById(Integer id);

    /**
     *  修改管理员状态
     * @param status
     * @param id
     */
    void adminUpdate(Integer status, Integer id);

    /**
     * 根据id删除
     * @param id
     */
    void deleteAdminById(Integer id);

    /**
     * 分页查询员工列表
     * @param adminPageDto
     * @return
     */
    IPage<AdminPageVo> adminList(AdminPageDto adminPageDto);

    /**
     * 退出登录
     * @param token
     */
    void logout(String token);

    /**
     * 获取待审核头像列表
     * @return
     */
    List<userAvatarVo> getPendingAvatarList();
    /**
     * 审核头像
     * @param id
     * @param status
     */
    void avatar(Long id, Integer status);

    /**
     * 获取每日用户统计
     * @param range 时间范围：7d=近7天, 30d=近30天, week=本周, month=本月
     * @return 每日用户统计
     */
    List<DailyUserStatsVo> getDailyUserStats(String range);

    /**
     * 获取每日商家统计
     * @param range 时间范围：7d=近7天, 30d=近30天, week=本周, month=本月
     * @return 每日商家统计（含本日快照）
     */
    BusinessStatsVo getDailyBusinessStats(String range);
}
