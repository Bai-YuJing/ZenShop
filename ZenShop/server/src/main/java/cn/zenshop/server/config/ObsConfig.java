package cn.zenshop.server.config;


import cn.zenshop.common.properties.OBSProperties;
import com.obs.services.ObsClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * OBS 客户端配置
 */
@Configuration
@Component
public class ObsConfig {
    @Bean
    public ObsClient obsClient(OBSProperties obsProperties) {
//        System.out.println("===== OBS 配置: " + obsProperties.getAccessKey() + " / " + obsProperties.getEndpoint());
        return new ObsClient(
                obsProperties.getAccessKey(),
                obsProperties.getSecretKey(),
                obsProperties.getEndpoint()
        );
}
}