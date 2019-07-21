package com.pinyougou.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageMessageListener implements MessageListenerConcurrently {

    @Autowired
    private FreeMarkerConfigurer configurer;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Value("${PageDir}")
    private String PageDir;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        try {
            //取出消息
            for (MessageExt messageExt : list) {
                byte[] body = messageExt.getBody();
                String s = new String(body);
                MessageInfo messageInfo = JSON.parseObject(s, MessageInfo.class);
                if (messageInfo!=null&&messageInfo.getMethod()==MessageInfo.METHOD_ADD) {
                    Long[] longs = JSON.parseObject(messageInfo.getContext().toString(), Long[].class);
                    for (Long aLong : longs) {
                        //生成静态页面
                        genHTML("item.ftl",aLong);
                    }
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }

    /***
     * 生成静态页面
     * @param templateName
     * @param id
     */
    private void genHTML(String templateName, Long id) {
        FileWriter writer = null;
        //模板
        try {
            Configuration configuration = configurer.getConfiguration();
            Template template = configuration.getTemplate(templateName);
            //数据源
            TbSeckillGoods seckillGoods = seckillGoodsMapper.selectByPrimaryKey(id);
            Map map = new HashMap<>();
            map.put("seckillGoods",seckillGoods);
            //创建静态页面的流
            writer = new FileWriter(new File(PageDir+id+".html"));
            //生成静态页面
            template.process(map,writer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
