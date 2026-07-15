package cn.zenshop.server.controller.user;

import cn.zenshop.common.result.Result;
import cn.zenshop.model.dto.OrderDto;
import cn.zenshop.model.dto.OrderPageDto;
import cn.zenshop.model.dto.OrderStatusDto;
import cn.zenshop.model.dto.PayDto;
import cn.zenshop.model.dto.RatingDto;
import cn.zenshop.model.vo.BusinessOrderVo;
import cn.zenshop.model.vo.UnreviewedOrderVo;
import cn.zenshop.model.vo.OrderCreateVo;
import cn.zenshop.model.vo.OrderPreviewVo;
import cn.zenshop.model.vo.OrderRatingVo;
import cn.zenshop.model.vo.OrderStatusCountVo;
import cn.zenshop.server.service.OrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/user/order")
@Slf4j
@Tag(name = "用户端-订单接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 订单预览
     * @param dto 商品ID列表与数量
     * @return 预览信息
     */
    @PostMapping("/preview")
    @Operation(summary = "订单预览")
    public Result<List<OrderPreviewVo>> preview(@RequestBody OrderDto dto) {
        return Result.success(orderService.preview(dto));
    }

    /**
     * 创建订单
     * @param dto 商品ID列表与数量
     * @return 订单ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建订单")
    public Result<OrderCreateVo> createOrder(@RequestBody OrderDto dto) {
        return Result.success(orderService.createOrder(dto));
    }

    /**
     * 支付订单（模拟）
     * @param dto 订单ID和支付方式
     * @return
     */
    @PostMapping("/pay")
    @Operation(summary = "支付订单（模拟）")
    public Result<Void> pay(@RequestBody PayDto dto) {
        orderService.pay(dto);
        return Result.success();
    }

    /**
     * 查询用户所有订单
     * @param pageDto 分页查询参数
     * @return 订单分页列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询用户所有订单")
    public Result<IPage<BusinessOrderVo>> getUserOrders(OrderPageDto pageDto) {
        return Result.success(orderService.getUserOrders(pageDto));
    }

    /**
     * 获取订单各状态数量
     * @return 各状态订单数量
     */
    @GetMapping("/statusCounts")
    @Operation(summary = "获取订单各状态数量")
    public Result<OrderStatusCountVo> getOrderStatusCounts() {
        return Result.success(orderService.getOrderStatusCounts());
    }

    /**
     * 获取订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    @GetMapping("/detail/{orderId}")
    @Operation(summary = "获取订单详情")
    public Result<BusinessOrderVo> getOrderDetail(@PathVariable Long orderId) {
        return Result.success(orderService.getOrderDetail(orderId));
    }

    @PutMapping("/complete/{id}")
    @Operation(summary = "确认收货")
    public Result<Void> complete(@PathVariable Long id) {
        orderService.complete(id);
        return Result.success();
    }

    /**
     * 获取未评价的订单列表
     * @param pageDto 分页查询参数
     * @return 未评价订单分页列表
     */
    @GetMapping("/unreviewed")
    @Operation(summary = "获取未评价的订单列表")
    public Result<IPage<UnreviewedOrderVo>> getUnreviewedOrders( OrderPageDto pageDto) {
        return Result.success(orderService.getUnreviewedOrders(pageDto));
    }

    @PostMapping("/rate")
    @Operation(summary = "评价订单商品")
    public Result<Void> rate(@RequestBody RatingDto dto) {
        orderService.rate(dto);
        return Result.success();
    }

    /**
     * 获取订单的评价列表
     * @param orderId 订单ID
     * @param productId 商品ID（可选）
     * @return 评价列表
     */
    @GetMapping("/ratings/{orderId}")
    @Operation(summary = "获取订单的评价列表")
    public Result<List<OrderRatingVo>> getOrderRatings(@PathVariable Long orderId,
                                                       @RequestParam(required = false) Long productId) {
        return Result.success(orderService.getOrderRatings(orderId, productId));
    }

    /**
     * 取消订单
     * @param orderId 订单ID
     * @param reasonType 取消原因类型
     * @return 取消结果提示信息
     */
    @PutMapping("/cancel/{orderId}")
    @Operation(summary = "取消订单")
    public Result<String> cancelOrder(@PathVariable Long orderId,
                                      @RequestParam Integer reasonType) {
        return Result.success(orderService.cancelOrder(orderId, reasonType));
    }
}
