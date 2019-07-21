package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    public void deleteByIds(Long[] ids);

    public void updateIndex(List<TbItem> items);

    public Map<String,Object> search(Map<String,Object> searchMap);
}
