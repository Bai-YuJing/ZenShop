package cn.zenshop.server.service.impl;

import cn.zenshop.common.result.Result;
import cn.zenshop.common.utils.OBSUtils;
import cn.zenshop.server.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class CommonServiceImpl implements CommonService {
    @Autowired
    private OBSUtils obsUtils;
    @Override
    public Result<String> uploadAvatar(MultipartFile file, String filePath) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileType = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String path =filePath+ UUID.randomUUID() + fileType;
            log.info("文件路径：{}",filePath);

            String upload = obsUtils.upload(path, file.getInputStream());
            return Result.success(upload);
        } catch (IOException e) {
            log.error("上传失败");
            throw new RuntimeException(e);
        }
    }
}
