package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.pojo.SeckillStatus;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.seckill.thread.CreateOrderThread;
import com.pinyougou.utils.IdWorker;
import config.SysConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillOrderServiceImpl extends CoreServiceImpl<TbSeckillOrder>  implements SeckillOrderService {

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;

	@Autowired
	private IdWorker idWorker;

	@Autowired
	private CreateOrderThread createOrderThread;

	private TbSeckillOrderMapper seckillOrderMapper;

	@Autowired
	public SeckillOrderServiceImpl(TbSeckillOrderMapper seckillOrderMapper) {
		super(seckillOrderMapper, TbSeckillOrder.class);
		this.seckillOrderMapper=seckillOrderMapper;
	}


	@Override
	public void deleteOrder(String userId) {
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId);

		if (seckillOrder == null) {
			System.out.println("没有该订单");
			return;
		}

		Long seckillId = seckillOrder.getSeckillId();

		//获取商品对象
		TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).get(seckillId);


		if (seckillGoods==null) {//说明redis中已经没有了
			//从数据库中查询
			seckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillId);
			seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
			//重新存储到redis中
			redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(seckillId,seckillGoods);


		}else{
			//恢复库存
			seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
			redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(seckillId,seckillGoods);
		}

		//商品队列中恢复元素
		redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX + seckillId).leftPush(seckillId);

		//删除该预订单
		redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).delete(userId);
	}

	@Override
	public void updateOrderStatus(String transaction_id, String userId) {
		TbSeckillOrder  seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId);


		if (seckillOrder != null) {

			seckillOrder.setPayTime(new Date());
			seckillOrder.setStatus("1");
			seckillOrder.setTransactionId(transaction_id);
			//存储到数据库中
			seckillOrderMapper.insert(seckillOrder);

			//删除订单即可
			redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).delete(userId);

		}
	}

	@Override
	public TbSeckillOrder getUserOrderStatus(String userId) {
		return (TbSeckillOrder) redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId);
	}


	@Override
    public void submitOrder(Long seckillId, String userId) {
		//判断是否已经在排队中了
		Object flagObject = redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).get(userId);
		if (flagObject != null) {
			throw new RuntimeException("你已经在排队中了别抢了，请稍等");
		}

		//判断是否有未支付的订单
		Object hasOrderObject = redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId);
		if (hasOrderObject != null) {
			throw new RuntimeException("你有未支付订单，请先支付");
		}

		//一个商品就是一个队列  队列的长度就是商品的库存大小
		Long goodsId = (Long) redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX + seckillId).rightPop();

		if (goodsId==null) {
			//说明商品已经没有库存了
			throw new RuntimeException("商品已被抢光");
		}

		//进入排队队列中
		redisTemplate.boundListOps(SysConstants.SEC_KILL_USER_ORDER_LIST).leftPush(
				new SeckillStatus(userId,seckillId,SeckillStatus.SECKILL_queuing));
		//设置排队标识
		redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).put(userId,seckillId);
		//多线程处理下单
		createOrderThread.handleOrder();


	}

    @Override
    public PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbSeckillOrder> all = seckillOrderMapper.selectAll();
        PageInfo<TbSeckillOrder> info = new PageInfo<TbSeckillOrder>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeckillOrder> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize, TbSeckillOrder seckillOrder) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbSeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();

        if(seckillOrder!=null){			
						if(StringUtils.isNotBlank(seckillOrder.getUserId())){
				criteria.andLike("userId","%"+seckillOrder.getUserId()+"%");
				//criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getSellerId())){
				criteria.andLike("sellerId","%"+seckillOrder.getSellerId()+"%");
				//criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getStatus())){
				criteria.andLike("status","%"+seckillOrder.getStatus()+"%");
				//criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getReceiverAddress())){
				criteria.andLike("receiverAddress","%"+seckillOrder.getReceiverAddress()+"%");
				//criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getReceiverMobile())){
				criteria.andLike("receiverMobile","%"+seckillOrder.getReceiverMobile()+"%");
				//criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getReceiver())){
				criteria.andLike("receiver","%"+seckillOrder.getReceiver()+"%");
				//criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getTransactionId())){
				criteria.andLike("transactionId","%"+seckillOrder.getTransactionId()+"%");
				//criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
			}
	
		}
        List<TbSeckillOrder> all = seckillOrderMapper.selectByExample(example);
        PageInfo<TbSeckillOrder> info = new PageInfo<TbSeckillOrder>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeckillOrder> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }
	
}
