package com.pinyougou.seckill.thread;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.common.pojo.SysConstants;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.pojo.SeckillStatus;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
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
    private DefaultMQProducer producer;

    @Async      //多线程注解
    public void handleOrder() {

        try {
            System.out.println("模拟处理订单开始========"+Thread.currentThread().getName());
            Thread.sleep(10000);
            System.out.println("模拟处理订单结束 总共耗费10秒钟======="+Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //用户队列中取出用户秒杀状态
        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps(SysConstants.SEC_KILL_USER_ORDER_LIST).rightPop();

        if (seckillStatus != null) {
            //取出秒杀的商品
            TbSeckillGoods secKillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("TbSeckillGoods").get(seckillStatus.getGoodsId());
            //库存少一
            secKillGoods.setStockCount(secKillGoods.getStockCount() - 1);
            //判断库存
            if (secKillGoods.getStockCount() <= 0) {
                //清空该商品
                redisTemplate.boundHashOps("TbSeckillGoods").delete(seckillStatus.getGoodsId());
                //更新数据库
                seckillGoodsMapper.updateByPrimaryKeySelective(secKillGoods);
            }
            //更新redis中的商品库存
            redisTemplate.boundHashOps("TbSeckillGoods").put(seckillStatus.getGoodsId(), secKillGoods);

            //生成秒杀订单存入redis
            TbSeckillOrder secKillOrder = new TbSeckillOrder();
            secKillOrder.setId(new IdWorker(0, 2).nextId());
            secKillOrder.setMoney(secKillGoods.getCostPrice());
            secKillOrder.setSeckillId(seckillStatus.getGoodsId());
            secKillOrder.setUserId(seckillStatus.getUserId());
            secKillOrder.setCreateTime(new Date());
            secKillOrder.setStatus("0");
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).put(seckillStatus.getUserId(), secKillOrder);
            //移除用户的排队标识
            redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).delete(seckillStatus.getUserId());

            //发送30分钟延迟消息
            sendMessage(secKillOrder);
        }
    }

    public void sendMessage(TbSeckillOrder seckillOrder){
        try {
            MessageInfo messageInfo = new MessageInfo(seckillOrder,"TOPIC_SECKILL_DELAY","TAG_SECKILL_DELAY","handleOrder_DELAY", MessageInfo.METHOD_UPDATE);
            Message message = new Message(messageInfo.getTopic(),messageInfo.getTags(),messageInfo.getKeys(), JSON.toJSONString(messageInfo).getBytes());
            //1分钟后发送消息
            message.setDelayTimeLevel(5);
            //message.setDelayTimeLevel(16); //延迟30分钟后发送消息
            producer.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
