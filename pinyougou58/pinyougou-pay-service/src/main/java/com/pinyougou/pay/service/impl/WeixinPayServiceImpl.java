package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.util.HttpClient;
import com.pinyougou.pay.service.WeixinPayService;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    /***
     * 生成二维码
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    @Override
    public Map<String, String> createNative(String out_trade_no, String total_fee) {
        //微信支付api的参数
        Map<String,String> param = new HashMap<String,String>();
        param.put("appid","wx8397f8696b538317");                                        //公众号
        param.put("mch_id","1473426802");                                               //商户号
        param.put("nonce_str", WXPayUtil.generateNonceStr());                           //随机字符串
        param.put("body","商品名称");                                                   //商品描述
        param.put("out_trade_no",out_trade_no);                                         //商户订单号
        param.put("total_fee",total_fee);                                               //总金额
        param.put("spbill_create_ip","127.0.0.1");                                      //终端IP
        param.put("notify_url","http://a31ef7db.ngrok.io/WeChatPay/WeChatPayNotify");   //回调地址  随便写
        param.put("trade_type","NATIVE");                                               //交易类型

        try {
            //参数格式 xml
            String xmlParam = WXPayUtil.generateSignedXml(param, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");
            //httClient 发送参数
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();
            //微信返回结果
            String resultStr = httpClient.getContent();
            System.out.println("httpClient.getContent():"+resultStr);
            //结果str转xml
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultStr);
            //自定义返回前端结果
            Map<String, String> map = new HashMap<>();
            map.put("out_trade_no",out_trade_no);
            map.put("total_fee",total_fee);
            map.put("code_url",resultMap.get("code_url"));
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /***
     * 查询支付状态
     * @param out_trade_no
     * @return
     */
    @Override
    public Map<String, String> queryPayStatus(String out_trade_no) {
        //封装请求参数
        Map<String,String> param = new HashMap<String,String>();
        param.put("appid","wx8397f8696b538317");                                        //公众号
        param.put("mch_id","1473426802");                                               //商户号
        param.put("out_trade_no",out_trade_no);                                         //商户订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());                           //随机字符串
        try {
            //数字签名
            String xml = WXPayUtil.generateSignedXml(param, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");
            //向支付接口发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setXmlParam(xml);
            httpClient.setHttps(true);
            httpClient.post();
            //获取响应结果
            String strXml = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(strXml);
            System.out.println("httpClient.getContent()"+map);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /***
     * 关闭订单
     * @param out_trade_no
     * @return
     */
    @Override
    public Map closePay(String out_trade_no) {
        //微信支付api的参数
        Map<String,String> param = new HashMap<String,String>();
        param.put("appid","wx8397f8696b538317");                                        //公众号
        param.put("mch_id","1473426802");                                               //商户号
        param.put("nonce_str", WXPayUtil.generateNonceStr());                           //随机字符串
        param.put("out_trade_no",out_trade_no);
        try {
            //参数格式 xml
            String xmlParam = WXPayUtil.generateSignedXml(param, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");
            //httClient 发送参数
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();
            //微信返回结果
            String resultStr = httpClient.getContent();
            System.out.println("????????????????????????????????????????????"+resultStr);
            //结果str转xml
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultStr);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
