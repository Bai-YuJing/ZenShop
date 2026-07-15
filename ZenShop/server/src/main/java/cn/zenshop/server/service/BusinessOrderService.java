package cn.zenshop.server.service;

import cn.zenshop.model.dto.OrderPageDto;
import cn.zenshop.model.vo.BusinessOrderVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface BusinessOrderService {

    /**
     * 分页查询已完成订单
     * @param pageDto 筛选条件
     * @return 分页结果
     */
    IPage<BusinessOrderVo> getCompletedOrders(OrderPageDto pageDto);

    /**
     * 分页查询未完成订单
     * @param pageDto 筛选条件
     * @return 分页结果
     */
    IPage<BusinessOrderVo> getUnfinishedOrders(OrderPageDto pageDto);
}
