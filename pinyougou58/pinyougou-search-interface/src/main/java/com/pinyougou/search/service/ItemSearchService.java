package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    Map<String, Object> search(Map<String, Object> searchMap);

    /***
     * 更新索引
     * @param tbItemList
     */
    void updateIndex(List<TbItem> tbItemList);

    /***
     * 删除索引
     * @param ids
     */
    void deleteByIds(Long[] ids);
}
