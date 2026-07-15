package cn.zenshop.server.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.zenshop.common.constant.RedisConstant;
import cn.zenshop.common.context.ThreadLocalContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            response.setStatus(401);
            return false;
        }

        String userIdStr = stringRedisTemplate.opsForValue().get(RedisConstant.USER_TOKEN + token);
        if (StrUtil.isBlank(userIdStr)) {
            response.setStatus(401);
            return false;
        }

        ThreadLocalContext.set(Long.valueOf(userIdStr));
        stringRedisTemplate.expire(RedisConstant.USER_TOKEN + token, RedisConstant.USER_TOKEN_TTL, TimeUnit.SECONDS);

        return true;
    }
}
