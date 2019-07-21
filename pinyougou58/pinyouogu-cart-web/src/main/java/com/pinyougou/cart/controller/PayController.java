package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private OrderService orderService;

    /***
     * 生成二维码
     * @return
     */
    @RequestMapping("/createNative")
    public Map<String, String> createNative() {
        //redis中获取订单号 与 金额
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLog = orderService.searchPayLogFromRedis(userId);

        //传参获取二维码
        if (payLog != null) {
            return weixinPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee() + "");
        }
        return null;
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
                //支付成功
                if ("SUCCESS".equals(map.get("trade_state"))) {
                    //更新订单状态
                    orderService.updateOrderStatus(out_trade_no,map.get("transaction_id"));
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
