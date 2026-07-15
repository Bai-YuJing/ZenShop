package cn.zenshop.server.config;

import cn.zenshop.server.interceptor.AdminInterceptor;
import cn.zenshop.server.interceptor.BusinessInterceptor;
import cn.zenshop.server.interceptor.UserInterceptor;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Autowired
    @Lazy
    private AdminInterceptor adminInterceptor;

    @Autowired
    @Lazy
    private BusinessInterceptor businessInterceptor;

    @Autowired
    @Lazy
    private UserInterceptor userInterceptor;


    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ZenShop 接口文档")
                        .version("1.0")
                        .description("ZenShop 电商项目 API 接口文档"));
    }

    @Bean
    public GroupedOpenApi businessApi() {
        return GroupedOpenApi.builder()
                .group("商家端")
                .pathsToMatch("/business/**")
                .build();
    }
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("用户端")
                .pathsToMatch("/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("管理端")
                .pathsToMatch("/admin/**")
                .build();
    }

    // ==================== 拦截器 ====================
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login/**");

        registry.addInterceptor(businessInterceptor)
                .addPathPatterns("/business/**")
                .excludePathPatterns("/business/business/**");

        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns("/user/user/**");

    }


}
