package cn.zenshop.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"cn.zenshop.server", "cn.zenshop.common"})
@EnableScheduling
public class ZenShopApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ZenShopApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ZenShopApplication.class, args);
    }

}
