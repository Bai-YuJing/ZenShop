package cn.zenshop.server.service;

import cn.zenshop.model.entity.BusinessReviewReason;
import com.baomidou.mybatisplus.extension.service.IService;

public interface BusinessReviewReasonService extends IService<BusinessReviewReason> {
    Integer getReason(Integer type, String id);
}
