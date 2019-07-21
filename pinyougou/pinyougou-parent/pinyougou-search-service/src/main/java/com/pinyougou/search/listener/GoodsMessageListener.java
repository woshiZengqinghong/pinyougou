package com.pinyougou.search.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.MessageInfo;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 监听器作用
 * 获取消息
 * 获取消息的内容 转换数据
 * 更新索引库
 */
public class GoodsMessageListener implements MessageListenerConcurrently {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        System.out.println(">>>>>>>>>>>接收数据");
        try {
            if (list != null) {
                for (MessageExt msg : list) {
                    byte[] body = msg.getBody();
                    String s = new String(body);
                    MessageInfo messageInfo = JSON.parseObject(s, MessageInfo.class);
                    switch (messageInfo.getMethod()) {
                        case 1://新增
                            {
                                String context = messageInfo.getContext().toString();
                                List<TbItem> tbItems = JSON.parseArray(context, TbItem.class);
                                itemSearchService.updateIndex(tbItems);
                                break;
                            }

                        case 2://更新
                            {
                            String context = messageInfo.getContext().toString();
                            List<TbItem> tbItems = JSON.parseArray(context, TbItem.class);
                            itemSearchService.updateIndex(tbItems);
                            break;
                            }
                        case 3://删除
                            {
                                String context = messageInfo.getContext().toString();
                                Long[] longs = JSON.parseObject(context, Long[].class);
                                itemSearchService.deleteByIds(longs);
                                break;
                            }
                        default:
                            break;
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
