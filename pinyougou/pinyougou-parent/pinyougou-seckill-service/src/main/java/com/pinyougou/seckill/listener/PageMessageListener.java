package com.pinyougou.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.MessageInfo;
import com.pinyougou.pojo.TbSeckillGoods;
import freemarker.template.Configuration;
import freemarker.template.Template;
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

    @Value("${pageDir}")
    private String pageDir;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        try {
            for (MessageExt msg : list) {
                byte[] body = msg.getBody();
                MessageInfo messageInfo = JSON.parseObject(body, MessageInfo.class);
                Long[] ids = JSON.parseObject(messageInfo.getContext().toString(), Long[].class);
                if(messageInfo.getMethod()==MessageInfo.METHOD_ADD){
                    for (Long id : ids) {
                        genHTML("item.ftl",id);
                    }
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }

    private void genHTML(String templateName, Long id) {
        FileWriter writer = null;
        try {
            //创建一个configuration对象
            //2.设置字符编码 和模板加载的目录
            Configuration configuration = configurer.getConfiguration();
            //3.获取模板对象
            Template template = configuration.getTemplate(templateName);
            //4.获取数据集
            Map model =new HashMap<>();

            TbSeckillGoods seckillGoods = seckillGoodsMapper.selectByPrimaryKey(id);
            model.put("seckillGoods",seckillGoods);
            //5.创建一个写流
            writer = new FileWriter(new File(pageDir+seckillGoods.getId()+".html"));
            //6.调用模板对象的process 方法输出到指定的文件中
            template.process(model,writer);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
