package cn.zenshop.server.controller.user;

import cn.zenshop.common.result.Result;
import cn.zenshop.model.vo.CouponVo;
import cn.zenshop.server.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/coupon")
@Slf4j
@Tag(name = "用户端-优惠券接口")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping("/list")
    @Operation(summary = "获取当前用户的优惠券")
    public Result<List<CouponVo>> getUserCoupons() {
        return Result.success(couponService.getUserCoupons());
    }

    @GetMapping("/all")
    @Operation(summary = "查询所有上架的优惠券")
    public Result<List<CouponVo>> getAllCoupons() {
        return Result.success(couponService.getAllCoupons());
    }

    @PostMapping("/claim/{id}")
    @Operation(summary = "抢券（每人限领一张）")
    public Result<Void> claimCoupon(@PathVariable Long id) {
        couponService.claimCoupon(id);
        return Result.success();
    }
}
