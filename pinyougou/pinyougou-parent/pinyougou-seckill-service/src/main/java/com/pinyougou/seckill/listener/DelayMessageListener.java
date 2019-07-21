package com.pinyougou.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.MessageInfo;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

public class DelayMessageListener implements MessageListenerConcurrently {

    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;

    @Autowired
    private TbSeckillGoodsMapper tbSeckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillOrderService seckillOrderService;
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        try {
            if (list != null) {
                for (MessageExt msg : list) {
                    System.out.println("延时消息接收");
                    byte[] body = msg.getBody();
                    String s = new String(body);
                    MessageInfo messageInfo = JSON.parseObject(s, MessageInfo.class);

                    if (messageInfo.getMethod()== MessageInfo.METHOD_UPDATE) {
                        //获取Redis中的未支付订单的信息
                        TbSeckillOrder tbSeckillOrder  = JSON.parseObject(messageInfo.getContext().toString(), TbSeckillOrder.class);

                        //获取数据库中的订单的信息
                        TbSeckillOrder seckillOrder = seckillOrderMapper.selectByPrimaryKey(tbSeckillOrder.getId());
                        if (seckillOrder == null) {
                            //关闭微信订单  如果 关闭微信订单的时候出现该订单已经关闭 则说明不需要 再恢复库存

                            seckillOrderService.deleteOrder(tbSeckillOrder.getUserId());
                        }
                        //有订单 无需关心
                    }
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }

    }
}
