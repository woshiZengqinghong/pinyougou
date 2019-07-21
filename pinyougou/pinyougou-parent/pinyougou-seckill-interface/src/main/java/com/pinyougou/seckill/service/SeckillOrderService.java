package com.pinyougou.seckill.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbSeckillOrder;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillOrderService extends CoreService<TbSeckillOrder> {

	/**
	 * 支付超时的时候执行。用于删除订单
	 * @param userId
	 */
	public void deleteOrder(String userId);

	/**
	 * 更新订单的状态 支付成功的时候执行
	 * @param transaction_id
	 * @param userId
	 */
	public void updateOrderStatus(String transaction_id,String userId);

	/**
	 * 查询某一个登陆用户的订单对象。
	 * @param userId
	 * @return
	 */
	public TbSeckillOrder getUserOrderStatus(String userId);


	/**
	 * 秒杀下单
	 * @param seckillId 秒杀的商品ID
	 * @param userId 下单的用户ID
	 */
	public void submitOrder(Long seckillId,String userId);
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize);



	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize, TbSeckillOrder SeckillOrder);
	
}
