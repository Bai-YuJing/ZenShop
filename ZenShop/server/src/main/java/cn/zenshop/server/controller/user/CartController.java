package cn.zenshop.server.controller.user;

import cn.zenshop.common.result.Result;
import cn.zenshop.model.vo.CartGroupVo;
import cn.zenshop.server.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/cart")
@Slf4j
@Tag(name = "用户端-购物车接口")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 添加商品到购物车
     * @param productId 商品ID
     * @param quantity  数量
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "添加商品到购物车")
    public Result<Void> addCart(@RequestParam Long productId, @RequestParam Integer quantity) {
        cartService.addCart(productId, quantity);
        return Result.success();
    }

    /**
     * 获取购物车列表（按商家分组）
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "获取购物车列表")
    public Result<List<CartGroupVo>> getCartList() {
        return Result.success(cartService.getCartList());
    }

    /**
     * 修改商品数量
     * @param productId 商品ID
     * @param quantity  新数量
     * @return
     */
    @PutMapping("/update")
    @Operation(summary = "修改商品数量")
    public Result<Void> updateQuantity(@RequestParam Long productId, @RequestParam Integer quantity) {
        cartService.updateQuantity(productId, quantity);
        return Result.success();
    }

    /**
     * 删除购物车商品
     * @param productId 商品ID
     * @return
     */
    @DeleteMapping("/remove/{productId}")
    @Operation(summary = "删除购物车商品")
    public Result<Void> removeCart(@PathVariable Long productId) {
        cartService.removeCart(productId);
        return Result.success();
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clear")
    @Operation(summary = "清空购物车")
    public Result<Void> clearCart() {
        cartService.clearCart();
        return Result.success();
    }
}
