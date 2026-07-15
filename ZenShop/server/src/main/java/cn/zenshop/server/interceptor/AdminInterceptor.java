package cn.zenshop.server.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.zenshop.common.constant.RedisConstant;
import cn.zenshop.common.context.ThreadLocalContext;
import cn.zenshop.model.entity.Admin;
import cn.zenshop.server.mapper.AdminMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class AdminInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 从请求头获取token
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            response.setStatus(401);
            return false;
        }
        // 根据token查询Redis中的用户ID
        String userIdStr = stringRedisTemplate.opsForValue().get(RedisConstant.ADMIN_TOKEN + token);
        if (StrUtil.isBlank(userIdStr)) {
            response.setStatus(401);
            return false;
        }
        // 存入ThreadLocal，供后续使用
        ThreadLocalContext.set(Long.valueOf(userIdStr));
        // 刷新token过期时间
        stringRedisTemplate.expire(RedisConstant.ADMIN_TOKEN + token, RedisConstant.ADMIN_TOKEN_TTL, TimeUnit.SECONDS);

        // 检查是否需要超级管理员权限
        if (handlerMethod.hasMethodAnnotation(SuperAdmin.class)) {
            Long currentId = ThreadLocalContext.get();
            Integer role = adminMapper.selectById(currentId).getRole();
            if (role == null || role != 2) {
                log.warn("非超级管理员尝试访问受限接口，userId: {}", currentId);
                response.setStatus(403);
                ThreadLocalContext.remove();
                return false;
            }
        }

        return true;
    }
}
