package com.pinyougou.user.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbUser;
import entity.UserOrderList;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface UserOrderService extends CoreService<TbUser> {


	/**
	 *查询所有订单
	 * @return
	 */
	List<UserOrderList> findOrderList(TbUser User);

	/**
	 *查询所有待支付订单
	 * @return
	 */
	List<UserOrderList> findUnpayOrder(String username);

	void updateOrderStatus(String out_trade_no, String userId, String transaction_id);
}
