package com.pinyougou.page.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbItem;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;

public class PageMessageListener implements MessageListenerConcurrently{

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        try {
            //判断 遍历 message
            if (list!=null&&list.size()>0) {
                for (MessageExt messageExt : list) {
                    //message body就是messageInfo
                    byte[] body = messageExt.getBody();
                    String s = new String(body);
                    MessageInfo messageInfo = JSON.parseObject(s, MessageInfo.class);

                    //判断method 执行新增 更新 删除
                    switch (messageInfo.getMethod()){
                        case 1:{
                            updatePageHtml(messageInfo);
                            break;
                        }
                        case 2:{
                            updatePageHtml(messageInfo);
                            break;
                        }
                        case 3:{
                            //取出ids
                            Long[] ids = JSON.parseObject(messageInfo.getContext().toString(), Long[].class);
                            if (ids!=null&&ids.length>0) {
                                itemPageService.deleteByIds(ids);
                            }
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

    private void updatePageHtml(MessageInfo messageInfo) {
        //messageInfo context 就是List<item>
        List<TbItem> tbItems = JSON.parseArray(messageInfo.getContext().toString(), TbItem.class);

        //判断集合是否为空
        if (tbItems!=null&&tbItems.size()>0) {
            //创建set集合 获取goodsId 并去重
            HashSet<Long> set = new HashSet<>();
            for (TbItem tbItem : tbItems) {
                set.add(tbItem.getGoodsId());
            }
            for (Long goodsId : set) {
                itemPageService.genItemHtml(goodsId);
            }
        }
    }
}
