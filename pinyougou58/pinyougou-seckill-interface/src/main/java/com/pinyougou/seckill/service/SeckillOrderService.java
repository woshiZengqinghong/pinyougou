package com.pinyougou.seckill.service;
import java.util.List;
import com.pinyougou.pojo.TbSeckillOrder;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillOrderService extends CoreService<TbSeckillOrder> {
	
	
	
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

	/***
	 * 添加订单到redis
	 * @param id
	 * @param userId
	 */
    void submitOrder(Long id, String userId);

	/**
	 * 查询订单状态
	 * @param userId
	 * @return
	 */
	Object getUserOrderStatus(String userId);

	/***
	 * 订单已支付  数据库插入订单 删除redis中的订单
	 * @param userId
	 * @param transaction_id
	 */
    void updateOrderStatus(String transaction_id,String userId);

	/**
	 * 关闭订单
	 * @param userId
	 */
	void deleteOrder(String userId);
}
