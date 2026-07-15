package cn.zenshop.server.service.impl;

import cn.zenshop.model.entity.BusinessReviewReason;
import cn.zenshop.server.mapper.BusinessReviewReasonMapper;
import cn.zenshop.server.service.BusinessReviewReasonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BusinessReviewReasonServiceImpl extends ServiceImpl<BusinessReviewReasonMapper, BusinessReviewReason> implements BusinessReviewReasonService {
    @Override
    public Integer getReason(Integer type, String id) {
        BusinessReviewReason reason = lambdaQuery()
                .eq(BusinessReviewReason::getType, type)
                .eq(BusinessReviewReason::getBusinessId, id)
                .orderByDesc(BusinessReviewReason::getId)
                .last("limit 1")
                .one();
        return reason != null ? reason.getReason() : null;
    }
}
