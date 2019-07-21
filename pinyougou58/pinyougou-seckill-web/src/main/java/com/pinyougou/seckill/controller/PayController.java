package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private SeckillOrderService orderService;

    /***
     * 生成二维码
     * @return
     */
    @RequestMapping("/createNative")
    public Map<String, String> createNative() {
        //redis中获取订单号 与 金额
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder secKillOrder = (TbSeckillOrder) orderService.getUserOrderStatus(userId);


        //传参获取二维码
        if (secKillOrder != null) {
            double dMoney = secKillOrder.getMoney().doubleValue();
            long money = (long) (dMoney*100);
            return weixinPayService.createNative(secKillOrder.getId()+"",money+ "");
        }
        return null;
    }

    /***
     * 查询支付状态
     * @param out_trade_no
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Result result = new Result(false, "支付失败");
        try {
            //发送5分钟 100次请求
            int count = 0;
            while (true) {
                count++;
                Map<String, String> resultMap = weixinPayService.queryPayStatus(out_trade_no);
                //支付超时
                if (count > 10) {
                    //关闭订单
                    Map closeMap = weixinPayService.closePay(out_trade_no);
                    if (closeMap == null) {
                        //关闭无返回
                    }else if ("ORDERPAID".equals(closeMap.get("err_code"))){
                        //关闭同时用户已支付
                        orderService.updateOrderStatus(resultMap.get("transaction_id"),userId);
                    }else if ("SUCCESS".equals(closeMap.get("return_code"))||"ORDERCLOSED".equals(closeMap.get("err_code"))){
                        //关闭成功 用户未支付
                        orderService.deleteOrder(userId);
                    }else {
                        //关闭异常
                        System.out.println("由于微信端错误");
                    }
                    result = new Result(false,"支付超时");
                    break;
                }

                //支付成功
                if ("SUCCESS".equals(resultMap.get("trade_state"))) {
                    //更新订单状态
                    orderService.updateOrderStatus(resultMap.get("transaction_id"),userId);
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
