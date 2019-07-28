package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.pinyougou.common.util.AlipayConfig;
import com.pinyougou.pay.service.AliPayService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
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
    private AliPayService aliPayService;

    @Reference
    private SeckillOrderService orderService;

    @RequestMapping(value = "/goAlipay", produces = "text/html; charset=UTF-8")
    public String goAlipay() throws Exception {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder secKillOrder = (TbSeckillOrder) orderService.getUserOrderStatus(userId);


        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);

        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = secKillOrder.getId()+"";
        //付款金额，必填
        String total_amount = secKillOrder.getMoney()+"";
        //订单名称，必填
        String subject = secKillOrder.getUserId();
        //商品描述，可空
        String body = "用户"+secKillOrder.getUserId()+"订购商品" ;

        // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
        String timeout_express = "1c";

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\""+ timeout_express +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //请求
        String result = alipayClient.pageExecute(alipayRequest).getBody();
        System.out.println(result);

        return result;
    }
//    /***
//     * 生成二维码
//     * @return
//     */
//    @RequestMapping("/goAliPay")
//    public Map<String, String> goAliPay() {
//        //redis中获取订单号 与 金额
//        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
//        TbSeckillOrder secKillOrder = (TbSeckillOrder) orderService.getUserOrderStatus(userId);
//        //传参获取二维码
//        if (secKillOrder != null) {
//            System.out.println("我要进行支付");
//
//
//            return aliPayService.goAliPay(secKillOrder);
//        }
//        return null;
//    }

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
