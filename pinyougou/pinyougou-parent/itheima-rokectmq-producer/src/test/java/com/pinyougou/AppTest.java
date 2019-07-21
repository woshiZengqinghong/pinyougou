package com.pinyougou;

import static org.junit.Assert.assertTrue;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@ContextConfiguration("classpath:spring-producer.xml")
@RunWith(SpringRunner.class)
public class AppTest {

    @Autowired
    private DefaultMQProducer producer;

    @Test
    public void sendMessage() throws Exception {
        byte[] bytes = new String("nihao").getBytes();
        Message message = new Message("springTopic", "TagA", bytes);
        SendResult result = producer.send(message);
        System.out.println(result.getMsgId()+">>>"+result.getSendStatus()+";>>>");
        Thread.sleep(10000);
    }
}
