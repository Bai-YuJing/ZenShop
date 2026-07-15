package cn.zenshop.server.service;

import cn.zenshop.common.result.Result;
import cn.zenshop.model.dto.UserInfoDto;
import cn.zenshop.model.dto.UserLoginDto;
import cn.zenshop.model.dto.UserPageDto;
import cn.zenshop.model.dto.UserRegisterDto;
import cn.zenshop.model.dto.UserUpdatePwdDto;
import cn.zenshop.model.entity.User;
import cn.zenshop.model.vo.UserPageVo;
import cn.zenshop.model.vo.UserInfoVo;
import cn.zenshop.model.vo.UserRatingVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService extends IService<User> {

    /**
     * 获取验证码
     * @param phone
     */
    void getCaptcha(String phone);

    /**
     * 用户注册
     * @param dto
     * @return token
     */
    String register(UserRegisterDto dto);

    /**
     * 用户登录
     * @param dto
     * @return token
     */
    String login(UserLoginDto dto);

    /**
     * 更新用户信息
     * @param userInfoDto
     */
    void updateUserInfo(UserInfoDto userInfoDto);

    /**
     * 修改密码
     * @param userUpdatePwdDto
     */
    void updatePassword(UserUpdatePwdDto userUpdatePwdDto);

    /**
     * 上传头像
     * @param file
     * @return
     */
    void avatar(MultipartFile file);

    // ===== 管理员端用户管理 =====

    /**
     * 管理员分页查询用户列表
     * @param dto 筛选条件
     * @return 用户分页数据
     */
    IPage<UserPageVo> adminGetUserPage(UserPageDto dto);

    /**
     * 管理员根据ID获取用户详情
     * @param id 用户ID
     * @return 用户详情
     */
    UserInfoVo adminGetUserDetail(Long id);

    /**
     * 管理员启用/禁用用户
     * @param id 用户ID
     * @param status 状态: 0=禁用, 1=启用
     */
    void adminUpdateUserStatus(Long id, Integer status);

    /**
     * 管理员删除用户（逻辑删除）
     * @param id 用户ID
     */
    void adminDeleteUser(Long id);

    /**
     * 管理员查看用户评价列表
     * @param userId 用户ID
     * @return 评价列表（订单号、评分、评价内容）
     */
    List<UserRatingVo> adminGetUserRatings(Long userId);
}
