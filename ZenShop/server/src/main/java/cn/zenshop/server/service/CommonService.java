package cn.zenshop.server.service;

import cn.zenshop.common.result.Result;
import org.springframework.web.multipart.MultipartFile;

public interface CommonService {
    Result<String> uploadAvatar(MultipartFile file, String filePath);
}
