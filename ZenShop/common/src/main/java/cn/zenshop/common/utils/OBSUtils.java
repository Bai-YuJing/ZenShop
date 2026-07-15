package cn.zenshop.common.utils;



import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.DeleteObjectRequest;
import com.obs.services.model.PutObjectRequest;
import cn.zenshop.common.properties.OBSProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Slf4j
@Component
public class OBSUtils {

    private final ObsClient obsClient;
    private final OBSProperties obsProperties;

    public OBSUtils(ObsClient obsClient, OBSProperties obsProperties) {
        this.obsClient = obsClient;
        this.obsProperties = obsProperties;
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件的访问 URL
     */
    public void deleteFile(String fileUrl) {
        try {
            // 从 URL 中提取 objectKey，格式：https://bucket.endpoint/ZenShop/xxx
            String prefix = "ZenShop/";
            int index = fileUrl.indexOf(prefix);
            if (index == -1) {
                log.warn("无法从URL中提取objectKey: {}", fileUrl);
                return;
            }
            String objectKey = fileUrl.substring(index);

            DeleteObjectRequest request = new DeleteObjectRequest();
            request.setBucketName(obsProperties.getBucketName());
            request.setObjectKey(objectKey);

            obsClient.deleteObject(request);
            log.info("deleteObject successfully, objectKey: {}", objectKey);

        } catch (ObsException e) {
            log.error("deleteObject failed, HTTP Code: {}, Error Code: {}, Message: {}",
                    e.getResponseCode(), e.getErrorCode(), e.getErrorMessage());
        }
    }

    /**
     * 上传文件
     *
     * @param fileName   文件名
     * @param stream     文件输入流
     * @return 文件的访问 URL
     */
    public String upload(String fileName, InputStream stream) {
        try {
            PutObjectRequest request = new PutObjectRequest();
            request.setBucketName(obsProperties.getBucketName());
            // 指定上传到桶中的文件名称（带固定前缀）
            request.setObjectKey("ZenShop/" + fileName);
            request.setInput(stream);

            obsClient.putObject(request);

            log.info("putObject successfully, objectKey: ZenShop{}", fileName);

        } catch (ObsException e) {
            log.error("putObject failed, HTTP Code: {}, Error Code: {}, Message: {}",
                    e.getResponseCode(), e.getErrorCode(), e.getErrorMessage());
            throw new RuntimeException("OBS 文件上传失败", e);
        }

        // 返回访问路径
        return "https://" +
                obsProperties.getBucketName() +
                "." +
                obsProperties.getEndpoint() +
                "/" +
                "ZenShop/" +
                fileName;
    }
}
