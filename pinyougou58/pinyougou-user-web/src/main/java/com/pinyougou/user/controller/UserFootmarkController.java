package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.user.service.UserFootmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
  Created by IntelliJ IDEA.
  User: EvanLI
  Date: 2019/7/26 0026
  Time: 14:43
*/
@RestController
@RequestMapping("/footmark")
public class UserFootmarkController {

    @Reference
    private UserFootmarkService userFootmarkService;

    @RequestMapping("/add")
    @CrossOrigin(origins = "http://localhost:9105",allowCredentials = "true")
    public void add(Long itemId){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!"anonymousUser".equals(userId)) {
            //只添加已登录用户的，未登录则不添加
            userFootmarkService.addFootmark(itemId,userId);
        }
    }

    @RequestMapping("/findAll")
    public List<TbItem> findAll(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(userId);
        if (!"anonymousUser".equals(userId)) {
            System.out.println(userFootmarkService.findAll(userId));
            return userFootmarkService.findAll(userId);
        }else {
            System.out.println("null");
            return null;
        }
    }
}
