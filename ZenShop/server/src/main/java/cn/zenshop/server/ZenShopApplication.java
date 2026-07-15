package cn.zenshop.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"cn.zenshop.server", "cn.zenshop.common"})
@EnableScheduling
public class ZenShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZenShopApplication.class, args);
    }

}
