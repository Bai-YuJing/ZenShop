package cn.zenshop.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "obs")
@Data
public class OBSProperties {

    /** 终端节点，例如 obs.cn-north-4.myhuaweicloud.com */
    private String endpoint;

    /** 访问密钥ID */
    private String accessKey;

    /** 秘密访问密钥 */
    private String secretKey;

    /** 桶名称 */
    private String bucketName;



}
