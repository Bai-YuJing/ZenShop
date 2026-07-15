package cn.zenshop.server.controller.Business;

import cn.zenshop.common.constant.CommonConstant;
import cn.zenshop.common.context.ThreadLocalContext;
import cn.zenshop.common.result.Result;
import cn.zenshop.model.dto.BusinessPageDto;
import cn.zenshop.model.dto.BusinessReviewDto;
import cn.zenshop.model.dto.OrderPageDto;
import cn.zenshop.model.dto.OrderStatusDto;
import cn.zenshop.model.entity.Business;
import cn.zenshop.model.vo.BusinessOrderVo;
import cn.zenshop.model.vo.BusinessPageVo;
import cn.zenshop.model.vo.BusinessVo;
import cn.zenshop.model.vo.DailyOrderStatsVo;
import cn.zenshop.model.vo.DailyRatingStatsVo;
import cn.zenshop.model.vo.OrderCancelVo;
import cn.zenshop.server.service.BusinessOrderService;

import java.util.List;
import cn.zenshop.server.service.BusinessReviewReasonService;
import cn.zenshop.server.service.BusinessService;
import cn.zenshop.server.service.CommonService;
import cn.zenshop.server.service.OrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/business/shop")
@Slf4j
@Tag(name = "店铺相关接口")
public class BusinessShopController {
    @Autowired
    private BusinessService businessService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private BusinessReviewReasonService businessReviewReasonService;

    @Autowired
    private BusinessOrderService businessOrderService;

    @Autowired
    private OrderService orderService;

    /**
     * 获取当前登录店铺状态
     * @return
     */
    @GetMapping("/getStatus")
    @Operation(summary = "获取店铺状态")
    public Result<Integer> getStatus(){
        Long l = ThreadLocalContext.get();
        BusinessVo business = businessService.getBusinessById(String.valueOf(l));
        return Result.success(business.getStatus().getValue());
    }

    /**
     * 获取当前登录店铺信息
     * @return
     */
    @GetMapping("/getShop")
    @Operation(summary = "获取当前店铺信息")
    public Result<BusinessVo> getShop(){
        Long l = ThreadLocalContext.get();
        return Result.success(businessService.getBusinessById(String.valueOf(l)));
    }

    /**
     * 上传logo
     * @param file
     * @return
     */
    @PostMapping("/logo")
    @Operation(summary = "上传logo")
    public Result<String> logo(MultipartFile file){
        String filePath= CommonConstant.BUSINESS_LOGO;
        return commonService.uploadAvatar(file,filePath);
    }

    /**
     * 提交审核
     * @param reviewDto
     * @return
     */
    @PutMapping("/review")
    @Operation(summary = "提交审核")
    public Result<Void> review(@RequestBody BusinessReviewDto reviewDto){
        businessService.review(reviewDto);
        return Result.success();
    }

    /**
     * 获取冻结/驳回原因
     * @param type
     * @param id
     * @return
     */
    @GetMapping("/getReason/{id}")
    @Operation(summary = "获取冻结/驳回原因")
    public Result<Integer> getReason(@RequestParam Integer type,@PathVariable String id){
        return Result.success(businessReviewReasonService.getReason(type,id));
    }
    /**
     * 下线
     * @param token
     * @return
     */
    @GetMapping("/logout")
    @Operation(summary = "下线")
    public Result<Void> logout(@RequestHeader("authentication") String token){
        businessService.logout(token);
        return Result.success();
    }

    /**
     * 查询已完成订单
     * @param pageDto 筛选条件
     * @return
     */
    @GetMapping("/orders/completed")
    @Operation(summary = "查询已完成订单")
    public Result<IPage<BusinessOrderVo>> getCompletedOrders(OrderPageDto pageDto) {
        return Result.success(businessOrderService.getCompletedOrders(pageDto));
    }

    /**
     * 查询未完成订单
     * @param pageDto 筛选条件
     * @return
     */
    @GetMapping("/orders/unfinished")
    @Operation(summary = "查询未完成订单")
    public Result<IPage<BusinessOrderVo>> getUnfinishedOrders(OrderPageDto pageDto) {
        return Result.success(businessOrderService.getUnfinishedOrders(pageDto));
    }

    /**
     * 商家发货
     * @param orderId 订单ID
     * @return
     */
    @PutMapping("/order/deliver/{orderId}")
    @Operation(summary = "商家发货")
    public Result<Void> deliver(@PathVariable Long orderId) {
        orderService.deliver(orderId);
        return Result.success();
    }
    /**
     * 更新状态
     * @param status
     * @param id
     * @return
     */
    @PutMapping("/deliver/{id}")
    @Operation(summary = "商家发货")
    public Result<Void> updateStatus(@PathVariable String id) {
            businessService.updateStatus(id, 2);
        return Result.success();
    }

    /**
     * 修改审核
     * @param reviewDto
     * @return
     */
    @PutMapping("/updateBusiness")
    @Operation(summary = "提交审核")
    public Result<Void> updateBusiness(@RequestBody BusinessReviewDto reviewDto){
        businessService.updateBusiness(reviewDto);
        return Result.success();
    }

    @GetMapping("/statistics/order")
    @Operation(summary = "获取每日订单统计")
    public Result<List<DailyOrderStatsVo>> getDailyOrderStats(@RequestParam(defaultValue = "7d") String range) {
        return Result.success(businessService.getDailyOrderStats(range));
    }

    @GetMapping("/statistics/rating")
    @Operation(summary = "获取每日评价统计")
    public Result<List<DailyRatingStatsVo>> getDailyRatingStats(@RequestParam(defaultValue = "7d") String range) {
        return Result.success(businessService.getDailyRatingStats(range));
    }

    @PutMapping("/order/reviewCancel/{orderId}")
    @Operation(summary = "审核取消订单")
    public Result<Void> reviewCancel(@PathVariable Long orderId, @RequestParam boolean approved,
                                          @RequestParam(required = false) String reason) {
        orderService.reviewCancel(orderId, approved, reason);
        return Result.success();
    }

    @GetMapping("/order/cancelList")
    @Operation(summary = "查看取消订单列表")
    public Result<IPage<OrderCancelVo>> getCancelOrders(OrderPageDto pageDto) {
        return Result.success(orderService.getCancelOrders(pageDto));
    }

    /**
     * 关店
     */
    @PutMapping("/close")
    @Operation(summary = "关店")
    public Result<Void> close() {
        String id = String.valueOf(ThreadLocalContext.get());
        businessService.updateStatus(id, 4);
        return Result.success();
    }

    /**
     * 开店
     */
    @PutMapping("/open")
    @Operation(summary = "开店")
    public Result<Void> open() {
        String id = String.valueOf(ThreadLocalContext.get());
        businessService.updateStatus(id, 2);
        return Result.success();
    }
}
