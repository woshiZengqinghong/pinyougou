package com.pinyougou.order.service.impl;

import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;

	@Autowired
	private TbOrderItemMapper orderItemMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private TbPayLogMapper payLogMapper;

	/**
	 * 添加
	 * @param order
	 */
	@Override
	public void add(TbOrder order) {
		//获取购物车数据
		List<Cart> redisCarList = (List<Cart>) redisTemplate.boundHashOps("redisCarList").get(order.getUserId());
		//支付日志参数
		double total_fee = 0;
		List<String> orderList = new ArrayList<>();
		//循环 cart--orderItemList
		for (Cart cart : redisCarList) {
			//设置订单id
			long orderId = new IdWorker(0, 1).nextId();
			orderList.add(orderId+"");

			//补全order的信息
			TbOrder newOrder = new TbOrder();
			newOrder.setOrderId(orderId);
			newOrder.setUserId(order.getUserId());
			newOrder.setPaymentType(order.getPaymentType());
			newOrder.setStatus("1"); //未支付
			newOrder.setSourceType("2"); //订单来源pc
			newOrder.setCreateTime(new Date());
			newOrder.setUpdateTime(newOrder.getCreateTime());
			newOrder.setReceiverAreaName(order.getReceiverAreaName());
			newOrder.setReceiverMobile(order.getReceiverMobile());
			newOrder.setReceiver(order.getReceiver());
			newOrder.setSellerId(cart.getSellerId());

			//支付金额
			double payment = 0;
			List<TbOrderItem> orderItemList = cart.getOrderItemList();
			for (TbOrderItem orderItem : orderItemList) {
				//TbOrderItem插入数据库
				long id = new IdWorker(0, 1).nextId();
				orderItem.setId(id);
				orderItem.setOrderId(orderId);
				orderItemMapper.insert(orderItem);

				//payment += 每张订单的总金额
				payment+=orderItem.getTotalFee().doubleValue();
			}
			newOrder.setPayment(BigDecimal.valueOf(payment));
			//购物车的总金额
			total_fee+=payment;

			//order 表中添加数据
			orderMapper.insert(newOrder);
		}

		//创建支付日志
		TbPayLog payLog = new TbPayLog();
		long outTradeNo = new IdWorker(0, 1).nextId();
		payLog.setOutTradeNo(outTradeNo+"");
		payLog.setCreateTime(new Date());

		payLog.setTotalFee((long) (total_fee*100));
		payLog.setUserId(order.getUserId());
		payLog.setTradeState("0");
		payLog.setOrderList(orderList.toString().replace("[","").replace("]",""));
		payLog.setPayType("1");
		//插入支付日志
		payLogMapper.insert(payLog);
		//存入redis
		System.out.println("TbPayLog.class.getSimpleName():"+TbPayLog.class.getSimpleName());
		redisTemplate.boundHashOps(TbPayLog.class.getSimpleName()).put(order.getUserId(),payLog);

		//清空redis中的购物车
		redisTemplate.boundHashOps("redisCarList").delete(order.getUserId());
	}

	/***
	 * redis中取出支付日志
	 * @param userId
	 * @return
	 */
	@Override
	public TbPayLog searchPayLogFromRedis(String userId) {
		TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps(TbPayLog.class.getSimpleName()).get(userId);
		return payLog;
	}

	/***
	 * 更新订单状态
	 * @param out_trade_no
	 * @param transaction_id
	 */
	@Override
	public void updateOrderStatus(String out_trade_no, String transaction_id) {
		//获取支付日志中的商家订单号列表
		TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
		//更新数据库支付日志的支付状态 支付时间 微信订单号
		payLog.setTradeState("1");
		payLog.setPayTime(new Date());
		payLog.setTransactionId(transaction_id);
		payLogMapper.updateByPrimaryKey(payLog);

		//更新商家订单的支付状态
		String orderIdListStr = payLog.getOrderList();
		String[] orderIdList = orderIdListStr.split(",");
		for (String orderId : orderIdList) {
			TbOrder order = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
			order.setPaymentTime(payLog.getPayTime());
			order.setStatus("2");
			orderMapper.updateByPrimaryKey(order);
		}
		//清空redis中的支付日志
		redisTemplate.boundHashOps(TbPayLog.class.getSimpleName()).delete(payLog.getUserId());
	}
}
