package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.user.service.UserOrderService;
import com.pinyougou.user.service.UserService;
import entity.Cart;
import entity.UserOrderList;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class UserOrderServiceImpl extends CoreServiceImpl<TbUser> implements UserOrderService {


    private TbUserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Autowired
    private TbSellerMapper sellerMapper;
    @Autowired
    private TbPayLogMapper payLogMapper;



    @Autowired
    public UserOrderServiceImpl(TbUserMapper userMapper) {
        super(userMapper, TbUser.class);
        this.userMapper = userMapper;
    }


    /*
    * 查询我的订单列表
    * */
    public List<UserOrderList> findOrderList(TbUser user) {

        List<UserOrderList> all = new ArrayList<>();
        //查询该买家的所有订单
        TbOrder tbOrder = new TbOrder();
        if (user != null) {
            if (StringUtils.isNotBlank(user.getUsername())) {
                tbOrder.setUserId(user.getUsername());
            }else {
                System.out.println("user==null?????");
                return null;
            }
        }
        List<TbOrder> tbOrderList = orderMapper.select(tbOrder);
        if (tbOrderList != null && tbOrderList.size()>0) {
            //查询每个订单号对应的所有产品
            for (TbOrder order : tbOrderList) {
                UserOrderList userOrderList = new UserOrderList();
                userOrderList.setOrder(order);

                TbOrderItem tbOrderItem = new TbOrderItem();
                tbOrderItem.setOrderId(order.getOrderId());
                List<TbOrderItem> orderItemList = orderItemMapper.select(tbOrderItem);
                userOrderList.setOrderItemList(orderItemList);
                //查询商家名
                TbSeller seller = sellerMapper.selectByPrimaryKey(order.getSellerId());
                userOrderList.setSellerName(seller.getNickName());
                all.add(userOrderList);
            }
        }
        System.out.println(all);
        return all;
    }

    @Override
    public List<UserOrderList> findUnpayOrder(String username) {
        List<UserOrderList> all = new ArrayList<>();
        //查询该买家的待支付订单
        TbOrder tbOrder = new TbOrder();

        if (StringUtils.isNotBlank(username) && !"anonymousUser".equals(username)) {
            tbOrder.setUserId(username);
            /*查询未付款即状态为1的所有订单*/
            tbOrder.setStatus("1");
        }else {
            System.out.println("user==null?????");
            return null;
        }

        List<TbOrder> tbOrderList = orderMapper.select(tbOrder);
        if (tbOrderList != null && tbOrderList.size()>0) {
            //查询每个订单号对应的所有产品
            for (TbOrder order : tbOrderList) {
                UserOrderList userOrderList = new UserOrderList();
                userOrderList.setOrder(order);

                TbOrderItem tbOrderItem = new TbOrderItem();
                tbOrderItem.setOrderId(order.getOrderId());
                List<TbOrderItem> orderItemList = orderItemMapper.select(tbOrderItem);
                userOrderList.setOrderItemList(orderItemList);
                //查询商家名
                TbSeller seller = sellerMapper.selectByPrimaryKey(order.getSellerId());
                userOrderList.setSellerName(seller.getNickName());
                all.add(userOrderList);
            }
        }
        System.out.println(all);
        return all;
    }



    @Override
    public void updateOrderStatus(String orderId, String userId, String transaction_id) {
        //更新订单状态
        TbOrder order = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
        order.setPaymentTime(new Date());
        order.setStatus("2");
        orderMapper.updateByPrimaryKey(order);

        //查询所有未支付订单
        TbOrder tbOrder = new TbOrder();
        tbOrder.setStatus("1");
        List<TbOrder> select = orderMapper.select(tbOrder);
        if (select != null) {

        }else {
            //清空redis中的支付日志
            redisTemplate.boundHashOps(TbPayLog.class.getSimpleName()).delete(userId);
        }

        /*无法更新支付日志，业务复杂，支付日志里创建的都是多个订单号在一行数据
        现在只支付其中某个订单号的金额，无法局部更新
        建议是购物车下单时，添加支付日志是每个订单号新建一条日志
        TbPayLog tbPayLog = new TbPayLog();
        tbPayLog.setUserId(userId);
        List<TbPayLog> payLogList = payLogMapper.select(tbPayLog);
        for (TbPayLog payLog : payLogList) {
            String orderIdListStr = payLog.getOrderList();
            String[] orderIds = orderIdListStr.split(",");
            List<String> orderIdList = Arrays.asList(orderIds);
            if (orderIdList.contains(orderId)) {
                for (String s : orderIdList) {
                    TbOrder order = orderMapper.selectByPrimaryKey(Long.parseLong(s));
                    order.setPaymentTime(payLog.getPayTime());
                    order.setStatus("2");
                    orderMapper.updateByPrimaryKey(order);
                }
            }
            //获取支付日志中的商家订单号列表
            TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
            //更新数据库支付日志的支付状态 支付时间 微信订单号
            payLog.setTradeState("1");
            payLog.setPayTime(new Date());
            payLog.setTransactionId(transaction_id);
            payLogMapper.updateByPrimaryKey(payLog);
        }*/

    }


}
