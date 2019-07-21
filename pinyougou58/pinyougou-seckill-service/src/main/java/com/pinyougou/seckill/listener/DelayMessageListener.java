package com.pinyougou.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DelayMessageListener implements MessageListenerConcurrently {

    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        try {
            if (list != null) {
                //获取消息体中的订单
                for (MessageExt messageExt : list) {
                    byte[] body = messageExt.getBody();
                    String s = new String(body);
                    MessageInfo messageInfo = JSON.parseObject(s, MessageInfo.class);
                    if (MessageInfo.METHOD_UPDATE == messageInfo.getMethod()) {
                        TbSeckillOrder orderFromMsg = JSON.parseObject(messageInfo.getContext().toString(), TbSeckillOrder.class);
                        TbSeckillOrder orderFromSql = seckillOrderMapper.selectByPrimaryKey(orderFromMsg.getId());
                        //用户未支付
                        if (orderFromSql == null) {
                            //关闭订单
                            seckillOrderService.deleteOrder(orderFromMsg.getUserId());
                        }
                        //数据库有订单 说明已支付
                    }
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
}
