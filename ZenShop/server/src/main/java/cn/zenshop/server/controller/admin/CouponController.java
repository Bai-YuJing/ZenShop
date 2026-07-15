package cn.zenshop.server.controller.admin;

import cn.zenshop.common.result.Result;
import cn.zenshop.model.dto.CouponDto;
import cn.zenshop.model.vo.CouponVo;
import cn.zenshop.server.service.CouponService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "管理端-优惠券接口")
@RestController("adminCouponController")
@RequestMapping("/admin/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping("/page")
    @Operation(summary = "分页查询优惠券列表")
    public Result<IPage<CouponVo>> getCouponPage(CouponDto dto) {
        return Result.success(couponService.adminGetCouponPage(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取优惠券详情")
    public Result<CouponVo> getCouponById(@PathVariable Long id) {
        return Result.success(couponService.adminGetCouponById(id));
    }

    @PostMapping("/create")
    @Operation(summary = "创建优惠券")
    public Result<Void> createCoupon(@RequestBody CouponDto dto) {
        couponService.adminCreateCoupon(dto);
        return Result.success();
    }

    @PutMapping("/update")
    @Operation(summary = "更新优惠券")
    public Result<Void> updateCoupon(@RequestBody CouponDto dto) {
        couponService.adminUpdateCoupon(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "下架/删除优惠券")
    public Result<Void> deleteCoupon(@PathVariable Long id) {
        couponService.adminDeleteCoupon(id);
        return Result.success();
    }
}
