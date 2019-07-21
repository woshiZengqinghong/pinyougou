package com.pinyougou.order.service;
import java.util.List;
import com.pinyougou.pojo.TbOrder;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbPayLog;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface OrderService {

	/**
	 * 添加
	 * @param order
	 */
	void add(TbOrder order);

	/***
	 * redis中获取支付日志
	 * @param userId
	 * @return
	 */
    TbPayLog searchPayLogFromRedis(String userId);

	/**
	 * 更新订单状态
	 * @param out_trade_no
	 * @param transaction_id
	 */
	void updateOrderStatus(String out_trade_no, String transaction_id);
}
