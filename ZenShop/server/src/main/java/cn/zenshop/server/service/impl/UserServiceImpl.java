package cn.zenshop.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.zenshop.common.constant.CommonConstant;
import cn.zenshop.common.constant.MessageConstant;
import cn.zenshop.common.constant.RedisConstant;
import cn.zenshop.common.context.ThreadLocalContext;
import cn.zenshop.common.exception.BaseException;
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
import cn.zenshop.common.utils.OBSUtils;
import cn.zenshop.server.mapper.UserMapper;
import cn.zenshop.server.mapper.BusinessRatingMapper;
import cn.zenshop.server.service.CommonService;
import cn.zenshop.server.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CommonService commonService;
    @Autowired
    private BusinessRatingMapper businessRatingMapper;
    @Autowired
    private OBSUtils obsUtils;


    @Override
    public void getCaptcha(String phone) {
        String code = RandomUtil.randomString(6);
        log.info("用户验证码：{}", code);
        String key = RedisConstant.USER_CAPTCHA_KEY + phone;
        stringRedisTemplate.opsForValue().set(key, code, RedisConstant.USER_CAPTCHA_KEY_TTL, TimeUnit.SECONDS);
    }

    @Override
    @Transactional
    public String register(UserRegisterDto dto) {
        log.info("用户注册：{}", dto.getPhone());
        // 校验验证码
        String key = RedisConstant.USER_CAPTCHA_KEY + dto.getPhone();
        String code = stringRedisTemplate.opsForValue().get(key);
        stringRedisTemplate.delete(key);
        if (StrUtil.isBlank(code)) {
            throw new BaseException(MessageConstant.CAPTCHA_NOT_FOUND);
        }
        if (!code.equals(dto.getCaptcha())) {
            throw new BaseException(MessageConstant.CAPTCHA_ERROR);
        }
        // 判断手机号是否已注册
        User existUser = lambdaQuery().eq(User::getPhone, dto.getPhone()).one();
        if (BeanUtil.isNotEmpty(existUser)) {
            throw new BaseException(MessageConstant.PHONE_SAME);
        }
        // 保存用户
        User user = BeanUtil.copyProperties(dto, User.class);
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        user.setStatus(CommonConstant.ON_SALE);
        save(user);
        // 自动登录
        String token = UUID.randomUUID().toString();
        String tokenKey = RedisConstant.USER_TOKEN + token;
        stringRedisTemplate.opsForValue().set(tokenKey, String.valueOf(user.getId()), RedisConstant.USER_TOKEN_TTL, TimeUnit.SECONDS);
        // 更新登录时间
        lambdaUpdate().set(User::getLastLoginTime, LocalDateTime.now()).eq(User::getId, user.getId()).update();
        return token;
    }

    @Override
    public String login(UserLoginDto dto) {
        // 查用户
        User user = lambdaQuery().eq(User::getPhone, dto.getPhone()).one();
        if (BeanUtil.isEmpty(user)) {
            throw new BaseException(MessageConstant.PHONE_NOT_REGISTER);
        }
        if (user.getStatus().equals(CommonConstant.IS_STOP)){
            throw new BaseException(MessageConstant.ACCOUNT_IS_STOP);
        }
        if (StrUtil.isNotBlank(dto.getPassword())) {
            // 密码登录
            if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
                throw new BaseException(MessageConstant.PASSWORD_ERROR);
            }
        } else if (StrUtil.isNotBlank(dto.getCaptcha())) {
            // 验证码登录
            String key = RedisConstant.USER_CAPTCHA_KEY + dto.getPhone();
            String code = stringRedisTemplate.opsForValue().get(key);
            stringRedisTemplate.delete(key);
            if (StrUtil.isEmpty(code) || !dto.getCaptcha().equals(code)) {
                throw new BaseException(MessageConstant.CAPTCHA_ERROR);
            }
        } else {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }

        // 生成 token
        String token = UUID.randomUUID().toString();
        String tokenKey = RedisConstant.USER_TOKEN + token;
        stringRedisTemplate.opsForValue().set(tokenKey, String.valueOf(user.getId()), RedisConstant.USER_TOKEN_TTL, TimeUnit.SECONDS);
        // 更新登录时间
        lambdaUpdate().set(User::getLastLoginTime, LocalDateTime.now()).eq(User::getId, user.getId()).update();
        return token;
    }

    /**
     * 更新用户信息
     * @param dto
     */
    @Override
    public void updateUserInfo(UserInfoDto dto) {
        User user = BeanUtil.copyProperties(dto, User.class);
        updateById(user);
    }

    /**
     * 修改密码
     * @param userUpdatePwdDto
     */
    @Override
    @Transactional
    public void updatePassword(UserUpdatePwdDto userUpdatePwdDto) {
        log.info("用户修改密码");
        // 获取当前登录用户
        Long userId = ThreadLocalContext.get();
        User user = getById(userId);
        if (BeanUtil.isEmpty(user)) {
            throw new BaseException(MessageConstant.USER_NOT_FOUND);
        }
        // 校验旧密码
        if (!BCrypt.checkpw(userUpdatePwdDto.getOldPassword(), user.getPassword())) {
            throw new BaseException(MessageConstant.PASSWORD_ERROR);
        }
        // 更新新密码
        lambdaUpdate()
                .set(User::getPassword, BCrypt.hashpw(userUpdatePwdDto.getNewPassword()))
                .set(User::getUpdatedTime, LocalDateTime.now())
                .eq(User::getId, userId)
                .update();
    }

    /**
     * 上传头像
     * @param file
     * @return
     */
    @Override
    public void avatar(MultipartFile file) {
        Long id = ThreadLocalContext.get();
        // 删除旧头像
        String oldAvatarUrl = (String) stringRedisTemplate.opsForHash().get(RedisConstant.USER_AVATAR, String.valueOf(id));
        if (StrUtil.isNotBlank(oldAvatarUrl)) {
            obsUtils.deleteFile(oldAvatarUrl);
        }
        // 上传新头像
        String filePath = CommonConstant.USER_AVATAR + LocalDate.now() + "/";
        Result<String> res = commonService.uploadAvatar(file, filePath);
        // 存入 Redis Hash，供后台审核
        stringRedisTemplate.opsForHash().put(RedisConstant.USER_AVATAR, String.valueOf(id), res.getData());
    }

    // ===== 管理员端用户管理 =====

    @Override
    public IPage<UserPageVo> adminGetUserPage(UserPageDto dto) {
        Page<User> page = page(
                new Page<>(Optional.ofNullable(dto.getPage()).orElse(0),
                            Optional.ofNullable(dto.getSize()).orElse(10)),
                new LambdaQueryWrapper<User>()
                        .like(StrUtil.isNotBlank(dto.getUsername()), User::getUsername, dto.getUsername())
                        .eq(StrUtil.isNotBlank(dto.getPhone()), User::getPhone, dto.getPhone())
                        .eq(dto.getStatus() != null, User::getStatus, dto.getStatus())
        );
        return page.convert(user -> BeanUtil.copyProperties(user, UserPageVo.class));
    }

    @Override
    public UserInfoVo adminGetUserDetail(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new BaseException(MessageConstant.USER_NOT_FOUND);
        }
        return BeanUtil.copyProperties(user, UserInfoVo.class);
    }

    @Override
    public void adminUpdateUserStatus(Long id, Integer status) {
        if (id == null || status == null) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }
        lambdaUpdate()
                .set(User::getStatus, status)
                .set(User::getUpdatedTime, LocalDateTime.now())
                .eq(User::getId, id)
                .update();
    }

    @Override
    public void adminDeleteUser(Long id) {
        if (id == null) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }
        removeById(id);
    }

    @Override
    public List<UserRatingVo> adminGetUserRatings(Long userId) {
        if (userId == null) {
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }
        return businessRatingMapper.selectUserRatings(userId);
    }
}
