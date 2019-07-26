package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbUser;
import entity.UserOrderList;
import com.pinyougou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
  Created by IntelliJ IDEA.
  User: EvanLI
  Date: 2019/7/24 0024
  Time: 10:31
*/
@RestController
@RequestMapping("/myOrderList")
public class UserOrderController {

    @Reference
    private UserService userService;


    @RequestMapping("/search")
    public List<UserOrderList> findOrderList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TbUser user = new TbUser();
        user.setUsername(username);
        List<UserOrderList> page = userService.findOrderList(user);
        return page;

    }
}
