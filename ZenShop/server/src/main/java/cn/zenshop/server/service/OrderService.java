package cn.zenshop.server.service;

import cn.zenshop.model.dto.OrderDto;
import cn.zenshop.model.dto.OrderPageDto;
import cn.zenshop.model.dto.PayDto;
import cn.zenshop.model.dto.RatingDto;
import cn.zenshop.model.vo.BusinessOrderVo;
import cn.zenshop.model.vo.OrderCancelVo;
import cn.zenshop.model.vo.UnreviewedOrderVo;
import cn.zenshop.model.vo.OrderCreateVo;
import cn.zenshop.model.vo.OrderPreviewVo;
import cn.zenshop.model.vo.OrderRatingVo;
import cn.zenshop.model.vo.OrderStatusCountVo;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface OrderService {

    /**
     * 订单预览
     * @param dto 商品ID列表与数量
     * @return 按商家分组的预览信息
     */
    List<OrderPreviewVo> preview(OrderDto dto);

    /**
     * 创建订单
     * @param dto 商品ID列表与数量
     * @return 订单ID和实付金额
     */
    OrderCreateVo createOrder(OrderDto dto);

    /**
     * 支付订单（模拟）
     * @param dto 订单ID和支付方式
     */
    void pay(PayDto dto);

    /**
     * 分页查询用户订单
     * @param pageDto 筛选条件
     * @return
     */
    IPage<BusinessOrderVo> getUserOrders(OrderPageDto pageDto);

    /**
     * 获取用户订单各状态数量
     * @return
     */
    OrderStatusCountVo getOrderStatusCounts();

    /**
     * 根据订单ID获取订单详情
     * @param orderId 订单ID
     * @return 订单详情（含商品列表）
     */
    BusinessOrderVo getOrderDetail(Long orderId);

    /**
     * 商家发货
     * @param orderId 订单ID
     */
    void deliver(Long orderId);

    /**
     * 用户确认收货
     * @param orderId 订单ID
     */
    void complete(Long orderId);

    /**
     * 获取未评价的订单列表
     * @param pageDto 分页参数
     * @return 未评价订单列表
     */
    IPage<UnreviewedOrderVo> getUnreviewedOrders(OrderPageDto pageDto);

    /**
     * 评价订单商品
     * @param dto 评价信息
     */
    void rate(RatingDto dto);

    /**
     * 获取订单的评价列表
     * @param orderId 订单ID
     * @param productId 商品ID（可选，传了则只返回该商品的评价）
     * @return 评价列表
     */
    List<OrderRatingVo> getOrderRatings(Long orderId, Long productId);

    /**
     * 用户取消订单
     * @param orderId 订单ID
     * @param reasonType 取消原因类型
     * @return 提示信息
     */
    String cancelOrder(Long orderId, Integer reasonType);

    /**
     * 商家审核取消订单
     * @param orderId 订单ID
     * @param approved true=同意取消, false=拒绝取消
     * @param reason 拒绝原因
     */
    void reviewCancel(Long orderId, boolean approved, String reason);

    /**
     * 分页查询商家的取消订单列表
     * @param pageDto 分页参数
     * @return 取消订单分页列表
     */
    IPage<OrderCancelVo> getCancelOrders(OrderPageDto pageDto);
}