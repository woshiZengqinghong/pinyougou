package com.pinyougou;

import com.pinyougou.es.service.ItemService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App {
    //创建一个类，执行方法 导入数据到ES中
    public static void main( String[] args ) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");

        ItemService itemService = context.getBean(ItemService.class);

        itemService.importDataToEs();


    }
}
