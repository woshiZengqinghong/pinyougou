package com.pinyougou.user.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;

public interface UserFootmarkService {
    void addFootmark(Long itemId, String userId);

    List<TbItem> findAll(String userId);
}
