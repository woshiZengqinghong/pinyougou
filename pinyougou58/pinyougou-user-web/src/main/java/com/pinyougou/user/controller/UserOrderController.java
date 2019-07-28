package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.AddressService;
import com.pinyougou.user.service.UserOrderService;
import entity.Result;
import entity.UserOrderList;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

/*
  Created by IntelliJ IDEA.
  User: EvanLI
  Date: 2019/7/24 0024
  Time: 10:31
*/
@RestController
@RequestMapping("/myOrderList")
public class UserOrderController {

    @Reference
    private UserOrderService userOrderService;
    @Reference
    private AddressService addressService;
    @Reference
    private WeixinPayService weixinPayService;

    @RequestMapping("/search")
    public List<UserOrderList> findOrderList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TbUser user = new TbUser();
        user.setUsername(username);
        List<UserOrderList> page = userOrderService.findOrderList(user);
        return page;
    }

    @RequestMapping("/getUnpayOrder")
    public List<UserOrderList> getUnpayOrder(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<UserOrderList> list = userOrderService.findUnpayOrder(username);
        System.out.println(list);
        return list;
    }


    /**
     * 生成支付二维码
     * @param orderId
     * @return
     */
    @RequestMapping("/createNative")
    public Map<String, String> createNative(String orderId,String payment) {
        String outTradeNo = orderId+"";
        String totalFee = payment+"";
        System.out.println("111111111111111111"+orderId);
        System.out.println("222222222222222222"+payment);
        return weixinPayService.createNative(outTradeNo, totalFee);
    }

    /***
     * 查询支付状态
     * @param out_trade_no
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        Result result = new Result(false, "支付失败");
        try {
            //发送5分钟 100次请求
            int count = 0;
            while (true) {
                //支付超时
                if (count > 100) {
                    result = new Result(false, "支付超时");
                    break;
                }
                count++;
                Map<String, String> map = weixinPayService.queryPayStatus(out_trade_no);
                System.out.println(map);
                //支付成功
                if ("SUCCESS".equals(map.get("trade_state"))) {
                    //更新订单状态
                    String userId = SecurityContextHolder.getContext().getAuthentication().getName();
                    userOrderService.updateOrderStatus(out_trade_no,userId,map.get("transaction_id"));
                    result = new Result(true, "支付成功");
                    break;
                }
                Thread.sleep(3000);
            }
            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return result;
        }
    }
}
