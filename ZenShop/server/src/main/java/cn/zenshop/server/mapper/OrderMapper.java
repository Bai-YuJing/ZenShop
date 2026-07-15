package cn.zenshop.server.mapper;

import cn.zenshop.model.dto.OrderPageDto;
import cn.zenshop.model.entity.Order;
import cn.zenshop.model.vo.BusinessOrderVo;
import cn.zenshop.model.vo.OrderCancelVo;
import cn.zenshop.model.vo.UnreviewedOrderVo;

import java.util.List;
import cn.zenshop.model.vo.OrderStatusCountVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    IPage<BusinessOrderVo> selectBusinessOrders(Page<BusinessOrderVo> page, @Param("dto") OrderPageDto dto, @Param("businessId") Long businessId, @Param("statusList") java.util.List<Integer> statusList);

    IPage<BusinessOrderVo> selectUserOrders(Page<BusinessOrderVo> page, @Param("dto") OrderPageDto dto, @Param("userId") Long userId, @Param("statusList") java.util.List<Integer> statusList);

    List<Long> selectUserOrderIds(@Param("dto") OrderPageDto dto, @Param("userId") Long userId, @Param("statusList") java.util.List<Integer> statusList, @Param("offset") long offset, @Param("limit") long limit);

    List<Long> selectBusinessOrderIds(@Param("dto") OrderPageDto dto, @Param("businessId") Long businessId, @Param("statusList") java.util.List<Integer> statusList, @Param("offset") long offset, @Param("limit") long limit);

    Long countUserOrders(@Param("dto") OrderPageDto dto, @Param("userId") Long userId, @Param("statusList") java.util.List<Integer> statusList);

    OrderStatusCountVo selectOrderStatusCounts(@Param("userId") Long userId);

    java.util.List<BusinessOrderVo> selectOrderDetailById(@Param("orderId") Long orderId, @Param("userId") Long userId);

    IPage<UnreviewedOrderVo> selectUnreviewedOrders(Page<UnreviewedOrderVo> page, @Param("userId") Long userId);

    IPage<OrderCancelVo> selectCancelOrders(Page<OrderCancelVo> page, @Param("businessId") Long businessId, @Param("status") Integer status);
}
