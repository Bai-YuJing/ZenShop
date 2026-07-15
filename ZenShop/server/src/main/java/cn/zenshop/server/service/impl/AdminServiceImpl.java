package cn.zenshop.server.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.zenshop.common.constant.CommonConstant;
import cn.zenshop.common.constant.MessageConstant;
import cn.zenshop.common.constant.RedisConstant;
import cn.zenshop.common.context.ThreadLocalContext;
import cn.zenshop.common.exception.BaseException;
import cn.zenshop.common.utils.OBSUtils;
import cn.zenshop.model.dto.AdminDto;
import cn.zenshop.model.dto.AdminLoginDto;
import cn.zenshop.model.dto.AdminPageDto;
import cn.zenshop.model.entity.Admin;
import cn.zenshop.model.entity.Business;
import cn.zenshop.model.entity.User;
import cn.zenshop.model.vo.AdminPageVo;
import cn.zenshop.model.vo.AdminVo;
import cn.zenshop.model.vo.BusinessStatsVo;
import cn.zenshop.model.vo.DailyBusinessStatsVo;
import cn.zenshop.model.vo.DailyUserStatsVo;
import cn.zenshop.model.vo.userAvatarVo;
import cn.zenshop.server.mapper.AdminMapper;
import cn.zenshop.server.mapper.BusinessMapper;
import cn.zenshop.server.mapper.UserMapper;
import cn.zenshop.server.service.AdminService;
import cn.zenshop.server.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AdminServiceImpl extends ServiceImpl<AdminMapper,Admin> implements AdminService   {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private OBSUtils obsUtils;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BusinessMapper businessMapper;
    /**
     * 管理员登录
     * @param adminLoginDto
     */
    @Override
    public String adminLogin(AdminLoginDto adminLoginDto) {
        //验证验证码
        String key = RedisConstant.ADMIN_CAPTCHA_KEY + adminLoginDto.getCaptchaKey();
        String captcha = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isEmpty(captcha)){
            throw new  BaseException(MessageConstant.CAPTCHA_NOT_FOUND);
        }
        if (!captcha.equals(adminLoginDto.getCaptcha())){
            throw new  BaseException(MessageConstant.CAPTCHA_ERROR);
        }
        // 删除redis中的验证码 确保验证码只使用一次
        stringRedisTemplate.delete(key);

        Admin admin = lambdaQuery().eq(Admin::getUsername, adminLoginDto.getUsername()).one();
        //验证用户名
        if (BeanUtil.isEmpty(admin)){
            throw new  BaseException(MessageConstant.USER_NAME_NOT_FOUND);
        }
        if(admin.getStatus()==0){
            throw new  BaseException(MessageConstant.STATUS_IS_STOP);
        }
        //验证密码
        String pwd = DigestUtil.md5Hex(adminLoginDto.getPassword());
        if (!admin.getPassword().equals(pwd)){
            throw new  BaseException(MessageConstant.PASSWORD_ERROR);
        }
        //更新登陆时间
        lambdaUpdate()
                .set(Admin::getLastLoginTime, LocalDateTime.now())
                .eq(Admin::getId,admin.getId())
                .update();
        // 生成token 写入redis
        String token = UUID.randomUUID().toString();
        String tokeKey = RedisConstant.ADMIN_TOKEN + token;
        stringRedisTemplate.opsForValue().set(tokeKey, String.valueOf(admin.getId()), RedisConstant.ADMIN_TOKEN_TTL, TimeUnit.MINUTES);

        log.info("token:{}",token);
        return token;
    }

    /**
     * 获取验证码
     * @return
     */
    @Override
    public Map<String, String> getCaptcha() {
        // 生成图形验证码：宽200，高100，4位字符，10个干扰线
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(150, 100, 4, 10);
        String code = captcha.getCode();          // 验证码文本
        String base64Image = captcha.getImageBase64Data(); // base64图片

        // 存入 Redis，120秒过期
        String uuid = UUID.randomUUID().toString();
        String key = RedisConstant.ADMIN_CAPTCHA_KEY + uuid;
        stringRedisTemplate.opsForValue().set(key, code, RedisConstant.ADMIN_CAPTCHA_KEY_TTL, TimeUnit.SECONDS);
        log.info("验证码：{}",code);

        return Map.of("captchaKey", uuid, "captchaImage", base64Image);
    }

    @Override
    public void addAdminUser(AdminDto adminDto) {
        // 判断用户名是否存在
        Admin admin = lambdaQuery().eq(Admin::getUsername, adminDto.getUsername()).one();
        if (BeanUtil.isNotEmpty(admin)){
            throw new BaseException(MessageConstant.USER_NAME_SAME);
        }

        log.info("添加管理员：{}",adminDto);
        admin = BeanUtil.copyProperties(adminDto, Admin.class);
        if (StrUtil.isBlank(admin.getNickname())){
            admin.setNickname("用户"+UUID.randomUUID());
        }

        admin.setAvatar(MessageConstant.ADMIN_AVATAR);
        admin.setPassword(DigestUtil.md5Hex(CommonConstant.DEFAULT_PASSWORD));
        save(admin);
    }

    /**
     *  更新管理员信息
     * @param adminDto
     */
    @Override
    public void adminUpdate(AdminDto adminDto) {
        Admin admin = BeanUtil.copyProperties(adminDto, Admin.class);
        admin.setUsername(null);
        updateById(admin);
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Override
    public AdminVo getAdminById(Integer id) {
        Admin admin = getById(id);
        log.info("查询到的用户：{}",admin);
        return BeanUtil.copyProperties(admin, AdminVo.class);
    }

    /**
     * 根据id修改状态
     * @param status
     * @param id
     */
    @Override
    public void adminUpdate(Integer status, Integer id) {
       log.info("将id：{}设置为状态：{}",id,status);
       lambdaUpdate().eq(Admin::getId,id)
               .set(Admin::getStatus,status)
               .set(Admin::getUpdatedTime,LocalDateTime.now())
               .update();
    }

    /**
     * 根据id删除
     * @param id
     */
    @Override
    public void deleteAdminById(Integer id) {
        removeById(id);
    }

    /**
     * 管理员分页查询
     * @param adminPageDto
     * @return
     */
    @Override
    public IPage<AdminPageVo> adminList(AdminPageDto adminPageDto) {
        log.info("{}",adminPageDto);
        IPage<Admin> page = page(
                new Page<>(Optional.ofNullable(adminPageDto.getPage()).orElse(0),
                            Optional.ofNullable(adminPageDto.getSize()).orElse(10)),
                new LambdaQueryWrapper<Admin>()
                        .eq(adminPageDto.getStatus() != null, Admin::getStatus, adminPageDto.getStatus())
                        .like(StrUtil.isNotBlank(adminPageDto.getUsername()), Admin::getUsername, adminPageDto.getUsername())
                        .like(StrUtil.isNotBlank(adminPageDto.getNickname()), Admin::getNickname, adminPageDto.getNickname())
        );
        return page.convert(admin -> BeanUtil.copyProperties(admin, AdminPageVo.class));
    }

    /**
     * 登出
     * @param token
     */
    @Override
    public void logout(String token) {
        String tokenKey = RedisConstant.ADMIN_TOKEN + token;
        stringRedisTemplate.delete(tokenKey);
        ThreadLocalContext.remove();
    }

    /**
     * 获取待审核头像列表（每次最多取15条）
     * 从 Redis Hash 中读取用户上传的待审核头像 URL
     * @return 待审核头像列表
     */
    @Override
    public List<userAvatarVo> getPendingAvatarList() {
        // 从 Redis Hash 中获取所有待审核头像 entry
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(RedisConstant.USER_AVATAR);
        List<userAvatarVo> list = new ArrayList<>();
        int count = 0;
        // 遍历 entries，取前 15 条
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            if (count >= 15) break;
            userAvatarVo vo = new userAvatarVo();
            // key=用户ID, value=头像URL
            vo.setId(Long.valueOf(entry.getKey().toString()));
            vo.setAvatar(entry.getValue().toString());
            list.add(vo);
            count++;
        }
        return list;
    }

    /**
     * 审核头像
     * @param id
     * @param status
     */
    @Override
    public void avatar(Long id, Integer status) {
        // 删除旧头像
        String url = (String) stringRedisTemplate.opsForHash().get(RedisConstant.USER_AVATAR, String.valueOf(id));
        if (StrUtil.isEmpty(url)){
            throw new BaseException(MessageConstant.UN_KNOW_ERROR);
        }
        //审核失败 删除redis中的数据
        if (status==0){
            obsUtils.deleteFile(url);
            stringRedisTemplate.opsForHash().delete(RedisConstant.USER_AVATAR, String.valueOf(id));
        }
        //审核成功 写入数据库
        if (status==1){
            //更新数据库
            userService
                    .lambdaUpdate()
                    .set(User::getAvatar,url)
                    .set(User::getUpdatedTime,LocalDateTime.now())
                    .eq(User::getId,id)
                    .update();
            //删除redis的信息
            stringRedisTemplate.opsForHash().delete(RedisConstant.USER_AVATAR, String.valueOf(id));
        }
    }

    @Override
    public List<DailyUserStatsVo> getDailyUserStats(String range) {
        List<DailyUserStatsVo> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        int days = parseRange(range);

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            Long totalUsers = userMapper.selectCount(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getIsDelete, 1)
                            .le(User::getCreatedTime, endOfDay));
            Long newUsers = userMapper.selectCount(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getIsDelete, 1)
                            .between(User::getCreatedTime, date.atStartOfDay(), endOfDay));
            Long activeUsers = userMapper.selectCount(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getIsDelete, 1)
                            .between(User::getLastLoginTime, date.atStartOfDay(), endOfDay));

            DailyUserStatsVo vo = new DailyUserStatsVo();
            vo.setDate(date.toString());
            vo.setTotalUsers(totalUsers);
            vo.setNewUsers(newUsers);
            vo.setActiveUsers(activeUsers);
            result.add(vo);
        }
        return result;
    }

    @Override
    public BusinessStatsVo getDailyBusinessStats(String range) {
        List<DailyBusinessStatsVo> dailyStats = new ArrayList<>();
        LocalDate today = LocalDate.now();
        int days = parseRange(range);

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            Long totalBusinesses = businessMapper.selectCount(
                    new LambdaQueryWrapper<Business>()
                            .eq(Business::getIsDelete, 1)
                            .le(Business::getCreatedTime, endOfDay));
            Long newBusinesses = businessMapper.selectCount(
                    new LambdaQueryWrapper<Business>()
                            .eq(Business::getIsDelete, 1)
                            .between(Business::getCreatedTime, date.atStartOfDay(), endOfDay));

            DailyBusinessStatsVo vo = new DailyBusinessStatsVo();
            vo.setDate(date.toString());
            vo.setTotalBusinesses(totalBusinesses);
            vo.setNewBusinesses(newBusinesses);
            dailyStats.add(vo);
        }

        // 本日快照：当前各状态数量
        Long currentTotal = businessMapper.selectCount(
                new LambdaQueryWrapper<Business>().eq(Business::getIsDelete, 1));
        Long pendingCount = businessMapper.selectCount(
                new LambdaQueryWrapper<Business>()
                        .eq(Business::getIsDelete, 1)
                        .eq(Business::getStatus, 1));
        Long activeCount = businessMapper.selectCount(
                new LambdaQueryWrapper<Business>()
                        .eq(Business::getIsDelete, 1)
                        .eq(Business::getStatus, 2));
        Long frozenCount = businessMapper.selectCount(
                new LambdaQueryWrapper<Business>()
                        .eq(Business::getIsDelete, 1)
                        .eq(Business::getStatus, 3));

        BusinessStatsVo result = new BusinessStatsVo();
        result.setDailyStats(dailyStats);
        result.setCurrentTotal(currentTotal);
        result.setPendingCount(pendingCount);
        result.setActiveCount(activeCount);
        result.setFrozenCount(frozenCount);
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
