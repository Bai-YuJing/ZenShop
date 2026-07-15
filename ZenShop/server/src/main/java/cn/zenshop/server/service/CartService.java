package cn.zenshop.server.service;

import cn.zenshop.model.vo.CartGroupVo;

import java.util.List;

public interface CartService {

    /**
     * 添加商品到购物车
     * @param productId 商品ID
     * @param quantity  数量
     */
    void addCart(Long productId, Integer quantity);

    /**
     * 获取购物车列表（按商家分组）
     */
    List<CartGroupVo> getCartList();

    /**
     * 修改购物车商品数量
     * @param productId 商品ID
     * @param quantity  新数量
     */
    void updateQuantity(Long productId, Integer quantity);

    /**
     * 删除购物车中的商品
     * @param productId 商品ID
     */
    void removeCart(Long productId);

    /**
     * 清空购物车
     */
    void clearCart();
}
