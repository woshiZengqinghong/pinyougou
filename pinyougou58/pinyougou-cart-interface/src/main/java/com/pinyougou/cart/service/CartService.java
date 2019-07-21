package com.pinyougou.cart.service;

import entity.Cart;

import java.util.List;

public interface CartService {
    /***
     * 像购物车列表添加商品
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);

    /***
     * 从redis中取出购物车
     * @param username
     * @return
     */
    List<Cart> getCartListFromRedis(String username);


    /***
     * 向redis中保存购物车
     * @param username
     * @param newRedisCartList
     */
    void saveToRedis(String username, List<Cart> newRedisCartList);

    /***
     * 合并cookie redis中的购物车
     * @param cookieCartList
     * @param redisCartList
     * @return
     */
    List<Cart> merge(List<Cart> cookieCartList, List<Cart> redisCartList);
}
