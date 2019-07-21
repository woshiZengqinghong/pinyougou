package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.utils.CookieUtil;
import entity.Cart;
import entity.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
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


    /**
     * 获取购物车的列表
     * @param request
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response){
        //获取用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //如果是匿名用户，操作cookie
        if ("anonymousUser".equals(name)) {

            String cartListString = CookieUtil.getCookieValue(request, "carList", "UTF-8");
            if (StringUtils.isEmpty(cartListString)) {
                cartListString="[]";
            }
            List<Cart> cookieCartList = JSON.parseArray(cartListString, Cart.class);
            return cookieCartList;
        }else{

            //操作redis
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(name);
            if (cartListFromRedis==null) {
                cartListFromRedis = new ArrayList<>();
            }

            String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
            if (StringUtils.isEmpty(cartListString)) {
                cartListString = "[]";
            }
            //cookie中购物车
            List<Cart> cookieCartList = JSON.parseArray(cartListString, Cart.class);

            if (cookieCartList.size()>0) {
                //redis中购物车
                //合并
                List<Cart> carts = cartService.mergeCartList(cookieCartList, cartListFromRedis);

                cartService.saveCartListToRedis(name,carts);
                //移除
                CookieUtil.deleteCookie(request,response,"cartList");

                return carts;
            }
            return cartListFromRedis;
        }

    }

    /**
     * 添加商品到已有的购物车的列表中
     * @param itemId
     * @param num
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9105",allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response){
//        response.setHeader("Access-Control-Allow-Origin","http://localhost:9105");//统一指定的域访问我的服务器资源
//        response.setHeader("Access-Control-Allow-Credentials","true");//统一客户端携带cookie
        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            //如果是匿名用户登陆 添加到cookie
            if ("anonymousUser".equals(name)) {

                List<Cart> cartList = findCartList(request,response);//获取购物车列表
                cartList = cartService.addGoodsToCartList(cartList, itemId, num);
                CookieUtil.setCookie(request,response,"cartList", JSON.toJSONString(cartList),3600*24,"UTF-8");
                return  new Result(true,"添加成功");
            }else{
                //获取购物车数据
                List<Cart> cartListFromRedis = cartService.findCartListFromRedis(name);
                //向已有的购物车添加商品
                List<Cart> cartListnew  = cartService.addGoodsToCartList(cartListFromRedis, itemId, num);
                //保存最新列表到redis中
                cartService.saveCartListToRedis(name,cartListnew);
                return new Result(true, "保存成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }

    }
}
