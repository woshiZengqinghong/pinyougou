package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.user.service.UserFootmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/*
  Created by IntelliJ IDEA.
  User: EvanLI
  Date: 2019/7/26 0026
  Time: 14:49
*/
@Service
public class UserFootmarkServiceImpl implements UserFootmarkService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public void addFootmark(Long itemId, String userId) {
        List<Long> list = redisTemplate.boundListOps("footmark_key:" + userId).range(0l,20l);
        if (list != null && list.size()>0) {
            if (!list.contains(itemId)) {
                redisTemplate.boundListOps("footmark_key:" + userId).leftPush(itemId);
            }
        }
    }

    @Override
    public List<TbItem> findAll(String userId) {
        ArrayList<TbItem> itemList = new ArrayList<>();
        if (userId != null) {
            List<Long> list = redisTemplate.boundListOps("footmark_key:" + userId).range(0l,20l);
            for (Long aLong : list) {
                TbItem tbItem = itemMapper.selectByPrimaryKey(aLong);
                itemList.add(tbItem);
            }
        }
        System.out.println(itemList);
        return itemList;
    }


}
