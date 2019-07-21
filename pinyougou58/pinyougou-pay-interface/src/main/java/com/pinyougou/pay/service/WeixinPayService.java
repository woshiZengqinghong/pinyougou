package com.pinyougou.pay.service;

import java.util.Map;

public interface WeixinPayService {

    /***
     * 生成支付二维码
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    Map<String,String> createNative(String out_trade_no,String total_fee);

    /***
     * 查询支付状态
     * @param out_trade_no
     * @return
     */
    Map<String,String> queryPayStatus(String out_trade_no);

    /***
     * 关闭订单
     * @param out_trade_no
     * @return
     */
    Map closePay(String out_trade_no);
}
