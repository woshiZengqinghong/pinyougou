package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /***
     * 从redis中获取购物车
     * @param username
     * @return
     */
    @Override
    public List<Cart> getCartListFromRedis(String username) {
        List<Cart> redisCarList = (List<Cart>) redisTemplate.boundHashOps("redisCarList").get(username);
        return redisCarList;
    }

    /***
     * 向redis中保存购物车
     * @param username
     * @param newRedisCartList
     */
    @Override
    public void saveToRedis(String username, List<Cart> newRedisCartList) {
        redisTemplate.boundHashOps("redisCarList").put(username,newRedisCartList);
    }

    /***
     * 合并cookie redis中的购物车
     * @param cookieCartList
     * @param redisCartList
     * @return
     */
    @Override
    public List<Cart> merge(List<Cart> cookieCartList, List<Cart> redisCartList) {
        for (Cart cart : cookieCartList) {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem tbOrderItem : orderItemList) {
                redisCartList = addGoodsToCartList(redisCartList, tbOrderItem.getItemId(), tbOrderItem.getNum());
            }
        }
        return redisCartList;
    }

    /***
     * 向购物车添加商品
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //获取item
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        //获取sellerId
        String sellerId = item.getSellerId();
        //获取item sellerId 对应的Cart
        Cart cart = findCartBySellerId(cartList, sellerId);

        //购物车没有该商家 直接添加
        if (cart==null) {
            List<TbOrderItem> orderItemList = new ArrayList<>();
            //添加orderItem
            TbOrderItem tbOrderItem = createTbOrderItem(item, num);
            orderItemList.add(tbOrderItem);
            //添加Cart
            cart = createCart(orderItemList, item);
            cartList.add(cart);
            return cartList;
        }

        //购物车有该商家
        List<TbOrderItem> orderItemList = cart.getOrderItemList();
        //增加商品
        TbOrderItem orderItem = findOrderItemByItemId(orderItemList,itemId);
        if (orderItem==null) {
            TbOrderItem tbOrderItem = createTbOrderItem(item, num);
            orderItemList.add(tbOrderItem);
            return cartList;
        }

        //判断购买数量是否为0
        //更新该商品数量 总价
        Integer newNum = orderItem.getNum() + num;
        if (newNum==0) {
            //删除该商品
            orderItemList.remove(orderItem);
            if (orderItemList.size()<=0) {
                cartList.remove(cart);
            }
        }else {
            orderItem.setNum(newNum);
            orderItem.setTotalFee(BigDecimal.valueOf(orderItem.getPrice().doubleValue()*newNum));
        }
        return cartList;

    }

    /***
     * 判断商品列表中是否有该商品
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem findOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem tbOrderItem : orderItemList) {
            if (itemId.equals(tbOrderItem.getItemId())) {
                return tbOrderItem;
            }
        }
        return null;
    }

    /***
     * 判断该购物车中是否有该商家
     * @param oldCartList
     * @param sellerId
     * @return
     */
    private Cart findCartBySellerId(List<Cart> oldCartList, String sellerId) {
        for (Cart cart : oldCartList) {
            if (sellerId.equals(cart.getSellerId())) {
                return cart;
            }
        }
        return null;
    }

    /***
     * 创建TbOrderItem
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createTbOrderItem(TbItem item, Integer num) {
        //添加orderItem
        TbOrderItem tbOrderItem = new TbOrderItem();
        tbOrderItem.setPicPath(item.getImage());
        tbOrderItem.setTitle(item.getTitle());
        tbOrderItem.setSellerId(item.getSellerId());
        tbOrderItem.setItemId(item.getId());
        tbOrderItem.setGoodsId(item.getGoodsId());
        tbOrderItem.setNum(num);
        BigDecimal price = item.getPrice();
        tbOrderItem.setPrice(price);
        Double totalPrice = num * price.doubleValue();
        tbOrderItem.setTotalFee(BigDecimal.valueOf(totalPrice));
        return tbOrderItem;
    }

    /***
     * 创建商家
     * @param orderItemList
     * @param item
     * @return
     */
    private Cart createCart(List orderItemList, TbItem item) {
        //添加Cart
        Cart cart = new Cart();
        cart.setOrderItemList(orderItemList);
        cart.setSellerId(item.getSellerId());
        cart.setSellerName(item.getSeller());
        return cart;
    }
}
