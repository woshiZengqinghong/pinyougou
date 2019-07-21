package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.util.CookieUtil;
import com.pinyougou.pojo.TbOrderItem;
import entity.Cart;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;


    /***
     * 查看购物车
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request, HttpServletResponse response){
        //用户是否登录
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)) {
            List<Cart> cookieCartList = getCookieCartList(request);
            return cookieCartList;
        } else {
            //redis中购物车
            List<Cart> redisCartList = cartService.getCartListFromRedis(username);
            if (redisCartList==null) {
                redisCartList=new ArrayList<>();
            }
            //cookie中的购物车
            List<Cart> cookieCartList = getCookieCartList(request);
            //合并两个购物车
            List<Cart> cartListNewMost = cartService.merge(cookieCartList,redisCartList);
            //清空cookie中的购物车
            CookieUtil.deleteCookie(request,response,"cartList");
            return cartListNewMost;
        }

    }

    /***
     * 添加购物车
     * @param itemId
     * @param num
     * @param request
     * @param response
     * @return
     */
    @CrossOrigin(origins = {"http://localhost:18095"},allowCredentials = "true")
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response){
        try {
            //判断用户是否登录
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if ("anonymousUser".equals(username)) {
                //未登录  cookie 中取出 List<Cart>
                List<Cart> oldCookieCartList = getCookieCartList(request);
                //将数据存入List<Cart> 集合
                List<Cart> newCookieCartList = cartService.addGoodsToCartList(oldCookieCartList,itemId,num);
                //重新存入cookie
                if (newCookieCartList==null) {
                    newCookieCartList = new ArrayList<>();
                }
                String newCookieCartStr = JSON.toJSONString(newCookieCartList);
                CookieUtil.setCookie(request,response,"cartList",newCookieCartStr,-1,true);  // 7*24*3600
            }else {
                //登录 redis 中取出数据 List<Cart>
                List<Cart> oldRedisCartList = cartService.getCartListFromRedis(username);
                if (oldRedisCartList==null) {
                    oldRedisCartList = new ArrayList<>();
                }
                //将数据存入List<Cart> 集合
                List<Cart> newRedisCartList = cartService.addGoodsToCartList(oldRedisCartList,itemId,num);
                //重新存入redis
                if (newRedisCartList==null) {
                    newRedisCartList = new ArrayList<>();
                }
                cartService.saveToRedis(username,newRedisCartList);
            }
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    private List<Cart> getCookieCartList(HttpServletRequest request){
        //cookie中查询
        String cartListStr = CookieUtil.getCookieValue(request, "cartList", true);
        if (StringUtils.isEmpty(cartListStr)) {
            cartListStr="[]";
        }
        List<Cart> cookieCartList = JSON.parseArray(cartListStr, Cart.class);
        return cookieCartList;
    }
}
