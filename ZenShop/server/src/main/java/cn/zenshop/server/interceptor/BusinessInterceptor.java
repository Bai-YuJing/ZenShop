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
public class BusinessInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            // 当前拦截到的不是动态方法，直接放行
            return true;
        }

        // 从请求头获取token
        String token = request.getHeader("authentication");
        if (StrUtil.isBlank(token)) {
            response.setStatus(401);
            return false;
        }

        // 根据token查询Redis中的商家ID
        String userIdStr = stringRedisTemplate.opsForValue().get(RedisConstant.BUSINESS_TOKEN + token);
        if (StrUtil.isBlank(userIdStr)) {
            // token过期或无效
            response.setStatus(401);
            return false;
        }

        // 存入ThreadLocal，供后续使用
        ThreadLocalContext.set(Long.valueOf(userIdStr));
        // 刷新token过期时间
        stringRedisTemplate.expire(RedisConstant.BUSINESS_TOKEN + token, RedisConstant.BUSINESS_TOKEN_TTL, TimeUnit.SECONDS);

        return true;
    }
}
