package com.pinyougou.search.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.dao.ItemSearchDao;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class GoodsMessageListener implements MessageListenerConcurrently {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

        try {
            //List<MessageExt> list  就是传过来的message
            if (list != null && list.size() > 0) {
                for (MessageExt messageExt : list) {

                    byte[] body = messageExt.getBody();
                    //MessageInfo messageInfo  = JSON.parseObject(body, MessageInfo.class);
                    MessageInfo messageInfo = JSON.parseObject(body.toString(), MessageInfo.class);

                    int method = messageInfo.getMethod();
                    switch (method) {
                        case 1: { //新增
                            List<TbItem> tbItems = JSON.parseArray(messageInfo.getContext().toString(), TbItem.class);
                            itemSearchService.updateIndex(tbItems);
                            break;
                        }
                        case 2: { //更新
                            List<TbItem> tbItems = JSON.parseArray(messageInfo.getContext().toString(), TbItem.class);
                            itemSearchService.updateIndex(tbItems);
                            break;
                        }
                        case 3: { //删除
                            Long[] ids = JSON.parseObject(messageInfo.getContext().toString(), Long[].class);
                            itemSearchService.deleteByIds(ids);
                            break;
                        }
                        default:{ //未定义
                            throw new RuntimeException("方法不存在");
                        }
                    }
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
