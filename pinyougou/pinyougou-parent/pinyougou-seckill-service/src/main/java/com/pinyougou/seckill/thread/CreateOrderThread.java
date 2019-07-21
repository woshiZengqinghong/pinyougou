package com.pinyougou.seckill.thread;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.MessageInfo;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.pojo.SeckillStatus;
import com.pinyougou.utils.IdWorker;
import config.SysConstants;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;


import java.util.Date;

public class CreateOrderThread {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    @Autowired
    private IdWorker idWorker;

    //多线程执行下单操作 异步方法
    @Async
    public void handleOrder() {
        try {
            System.out.println("模拟处理订单开始-------" + Thread.currentThread().getName());
            Thread.sleep(10000);
            System.out.println("模拟处理订单结束 总共耗时十秒钟======" + Thread.currentThread().getName());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //从redis中获取商品
        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps(SysConstants.SEC_KILL_USER_ORDER_LIST).rightPop();
        if (seckillStatus != null) {
            //从redis中获取商品
            TbSeckillGoods killgoods = (TbSeckillGoods) redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).get(seckillStatus.getGoodsId());
            //将这个商品的库存减少
            killgoods.setStockCount(killgoods.getStockCount() - 1);//减少

            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(seckillStatus.getGoodsId(), killgoods);


            if (killgoods.getStockCount() <= 0) {//如果已经被秒光
                seckillGoodsMapper.updateByPrimaryKeySelective(killgoods);//同步到数据库
                redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).delete(seckillStatus.getGoodsId());
            }

            //创建订单
            long orderId = idWorker.nextId();

            TbSeckillOrder seckillOrder = new TbSeckillOrder();

            seckillOrder.setId(orderId);
            seckillOrder.setCreateTime(new Date());
            seckillOrder.setMoney(killgoods.getCostPrice());
            seckillOrder.setSeckillId(seckillStatus.getGoodsId());
            seckillOrder.setSellerId(killgoods.getSellerId());
            seckillOrder.setUserId(seckillStatus.getUserId());
            seckillOrder.setStatus("0");
            //将构建的订单保存到redis中
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).put(seckillStatus.getUserId(), seckillOrder);

            //移除排队标识 标识下单成功
            redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).delete(seckillStatus.getUserId());
            //立即发送延时消息
            sendMessage(seckillOrder);
        }
    }

    private void sendMessage(TbSeckillOrder seckillOrder) {
        try {
            MessageInfo messageInfo = new MessageInfo("TOPIC_SECKILL_DELAY", "TAG_SECKILL_DELAY", "handleOrder_DELAY", seckillOrder, MessageInfo.METHOD_UPDATE);

            System.out.println("多线程下单======");
            Message message = new Message(messageInfo.getTopic(), messageInfo.getTags(), messageInfo.getKeys(), JSON.toJSONString(messageInfo).getBytes());
            //设置消息演示等级 16=30m
            message.setDelayTimeLevel(5);

            defaultMQProducer.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
