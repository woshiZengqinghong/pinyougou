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
    private TbItemMapper tbItemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> mergeCartList(List<Cart> cookieList, List<Cart> redisList) {
        for (Cart cart : cookieList) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                redisList = addGoodsToCartList(redisList, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return redisList;
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    @Override
    public List<Cart> findCartListFromRedis(String username) {
        return (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
    }

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据商品的ID 查询商品的数据
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
        //2.获取商品数据中的商家ID sellerID
        String sellerId = tbItem.getSellerId();//获取该商品的商家的ID

        Cart cart = findCartBySellerId(sellerId, cartList);

        if(cart == null){
            //3.判断要添加的商品的商家Id，是否在已有的购物车列表中存在 如果没有存在 直接添加商品
            cart = new Cart();

            cart.setSellerId(sellerId);
            cart.setSellerName(tbItem.getSeller());//店铺名

            List<TbOrderItem> orderItemList = new ArrayList<>();

            TbOrderItem orderItemNew = new TbOrderItem();
            //补充属性
            //设置其他属性
            orderItemNew.setItemId(itemId);
            orderItemNew.setGoodsId(tbItem.getGoodsId());
            orderItemNew.setTitle(tbItem.getTitle());
            orderItemNew.setPrice(tbItem.getPrice());
            orderItemNew.setNum(num);//传递过来的购买的数量
            double v = num * tbItem.getPrice().doubleValue();
            orderItemNew.setTotalFee(new BigDecimal(v));//金额
            orderItemNew.setPicPath(tbItem.getImage());//商品图片

            orderItemList.add(orderItemNew);
            //购物车
            cart.setOrderItemList(orderItemList);

            cartList.add(cart);//添加到现有购物车中

        }else{
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            TbOrderItem orderItem = findOrderItemByItemId(itemId, orderItemList);

            //3.如果要添加的商品的商家的Id，在已有的购物车中列表中存在 此时表示有商家了
            if (orderItem != null) {
                orderItem.setNum(orderItem.getNum()+num);//数量相加
                //金额重新计算 数量*单价
                double v= orderItem.getNum()*orderItem.getPrice().doubleValue();
                orderItem.setTotalFee(BigDecimal.valueOf(v));//重新设置

                //判断如果商品的购买数量为0 表不买了，就要删除商品
                if (orderItem.getNum() == 0) {
                    orderItemList.remove(orderItem);
                }
                //如果是长度为空说明 用户没购买该商家的商品就直接删除对象
                if (orderItemList.size() == 0) {
                    cartList.remove(cart);//商家也删除了
                }

            }else{
                //4.2 要添加的商品 如果没有存在的商家的明细列表
                TbOrderItem orderItemNew = new TbOrderItem();
                //设置他的属性
                orderItemNew.setItemId(itemId);
                orderItemNew.setGoodsId(tbItem.getGoodsId());
                orderItemNew.setTitle(tbItem.getTitle());
                orderItemNew.setPrice(tbItem.getPrice());
                orderItemNew.setNum(num);
                double v= num * tbItem.getPrice().doubleValue();
                orderItemNew.setTotalFee(BigDecimal.valueOf(v));
                orderItemNew.setPicPath(tbItem.getImage());
                orderItemList.add(orderItemNew);
            }


        }
        return cartList;
    }

    private TbOrderItem findOrderItemByItemId(Long itemId, List<TbOrderItem> orderItemList) {
        for (TbOrderItem tbOrderItem : orderItemList) {
            if (tbOrderItem.getItemId().equals(itemId)) {
                return tbOrderItem;
            }
        }
        return null;
    }

    private Cart findCartBySellerId(String sellerId, List<Cart> cartList) {
        for (Cart cart : cartList) {
            if (sellerId.equals(cart.getSellerId())) {
                return cart;
            }
        }
        return null;
    }
}
