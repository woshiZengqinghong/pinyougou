package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.SysConstants;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.pojo.SeckillStatus;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.seckill.thread.CreateOrderThread;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SeckillOrderServiceImpl extends CoreServiceImpl<TbSeckillOrder> implements SeckillOrderService {

    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;

    @Autowired
    private TbSeckillGoodsMapper secKillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CreateOrderThread createOrderThread;

    @Autowired
    public SeckillOrderServiceImpl(TbSeckillOrderMapper seckillOrderMapper) {
        super(seckillOrderMapper, TbSeckillOrder.class);
        this.seckillOrderMapper = seckillOrderMapper;
    }


    @Override
    public PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbSeckillOrder> all = seckillOrderMapper.selectAll();
        PageInfo<TbSeckillOrder> info = new PageInfo<TbSeckillOrder>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeckillOrder> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize, TbSeckillOrder seckillOrder) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbSeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();

        if (seckillOrder != null) {
            if (StringUtils.isNotBlank(seckillOrder.getUserId())) {
                criteria.andLike("userId", "%" + seckillOrder.getUserId() + "%");
                //criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getSellerId())) {
                criteria.andLike("sellerId", "%" + seckillOrder.getSellerId() + "%");
                //criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getStatus())) {
                criteria.andLike("status", "%" + seckillOrder.getStatus() + "%");
                //criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getReceiverAddress())) {
                criteria.andLike("receiverAddress", "%" + seckillOrder.getReceiverAddress() + "%");
                //criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getReceiverMobile())) {
                criteria.andLike("receiverMobile", "%" + seckillOrder.getReceiverMobile() + "%");
                //criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getReceiver())) {
                criteria.andLike("receiver", "%" + seckillOrder.getReceiver() + "%");
                //criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getTransactionId())) {
                criteria.andLike("transactionId", "%" + seckillOrder.getTransactionId() + "%");
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


    /***
     * 添加订单至redis
     * @param secKillGoodId
     * @param userId
     */
    @Override
    public void submitOrder(Long secKillGoodId, String userId) {
        //判断用户是否已排队
        Long goodsId = (Long) redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).get(userId);
        if (goodsId != null) {
            throw new RuntimeException("正在排队");
        }

        //判断用户有未支付的订单
        TbSeckillOrder secKillOrder = (TbSeckillOrder) redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId);
        if (secKillOrder != null) {
            throw new RuntimeException("有未支付的订单");
        }

        //判断商品的库存
        Long id = (Long) redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX + secKillGoodId).rightPop();
        //商品售罄
        if (id == null) {
            throw new RuntimeException("商品售罄");
        }

        //将用户加入下单队列
        redisTemplate.boundListOps(SysConstants.SEC_KILL_USER_ORDER_LIST).leftPush(new SeckillStatus(userId,secKillGoodId, SeckillStatus.SECKILL_queuing));
        //标记用户的排队状态
        redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).put(userId, secKillGoodId);

        //多线程操作订单
        createOrderThread.handleOrder();
    }

    /***
     * 查询用户订单状态
     * @param userId
     * @return
     */
    @Override
    public Object getUserOrderStatus(String userId) {
        return redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId);
    }

    /***
     * 订单已支付  数据库插入订单 删除redis中的订单
     * @param userId
     * @param transaction_id
     */
    @Override
    public void updateOrderStatus(String transaction_id,String userId) {
        TbSeckillOrder secKillOrder = (TbSeckillOrder) redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId);
        if (secKillOrder != null) {
            secKillOrder.setTransactionId(transaction_id);
            secKillOrder.setPayTime(new Date());
            secKillOrder.setStatus("1");

            seckillOrderMapper.insertSelective(secKillOrder);
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).delete(userId);
        }
    }

    /***
     * 关闭订单
     * @param userId
     */
    @Override
    public void deleteOrder(String userId) {
        //恢复库存
        TbSeckillOrder secKillOrder = (TbSeckillOrder) redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId);
        Long secKillGoodId = secKillOrder.getSeckillId();
        TbSeckillGoods SecKillGood = (TbSeckillGoods) redisTemplate.boundHashOps("TbSeckillGoods").get(secKillGoodId);
        //商品售罄 数据库中查询
        if (SecKillGood == null) {
            SecKillGood = secKillGoodsMapper.selectByPrimaryKey(secKillGoodId);
            SecKillGood.setStockCount(1);
        }else {
            //商品未售罄
            SecKillGood.setStockCount(SecKillGood.getStockCount()+1);
        }
        //存入redis中
        redisTemplate.boundHashOps("TbSeckillGoods").put(secKillGoodId,SecKillGood);
        //恢复队列的元素
        redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX+secKillGoodId).leftPush(secKillGoodId);
        //删除订单
        redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).delete(userId);
    }

}
