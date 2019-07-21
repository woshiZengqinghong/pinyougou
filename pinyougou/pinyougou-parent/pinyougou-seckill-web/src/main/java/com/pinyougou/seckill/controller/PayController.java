package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.utils.IdWorker;
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
    private SeckillOrderService orderService;

    @RequestMapping("/createNative")
    public Map createNative(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder order = orderService.getUserOrderStatus(userId);
        if (order != null) {
            double v = order.getMoney().doubleValue() * 100;
            long x = (long) v;
            return weixinPayService.createNative(order.getId()+"",x+"");
        }

        return null;
    }


    @RequestMapping("/queryStatus")
    public Result queryStatus(String out_trade_no){
        try {
            Result result = new Result(false,"支付失败");
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            int count=0;
            //1.调用支付的服务 不停的查询 状态
            while (true) {
                Map<String,String> resultMap = weixinPayService.queryPayStatus(out_trade_no);

                count ++;

                if (count>=100){
                    result = new Result(false,"超时");
                    //关闭微信订单
                    Map map = weixinPayService.closePay(out_trade_no);
                    if ("ORDERPAID".equals(map.get("err_code"))) {
                        //已经支付则更新入库
                        orderService.updateOrderStatus(resultMap.get("transaction_id"),userId);
                    }else if("SUCCESS".equals(resultMap.get("result_code")) || "ORDERCLOSED".equals(resultMap.get("err_code"))){
                        //删除预订单
                        orderService.deleteOrder(userId);
                    }else{
                        System.out.println("由于微信端错误");
                    }
                    break;
                }

                Thread.sleep(3000);

                //如果超时5分钟就直接退出
                if ("SUCCESS".equals(resultMap.get("trade_state"))) {
                    result=new Result(true,"支付成功");
                    orderService.updateOrderStatus(resultMap.get("transaction_id"),userId);
                    break;
                }
            }
            //返回结果
            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new Result(false,"支付失败");
        }
    }

}
