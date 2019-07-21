package com.pinyougou.search.dao;

import com.pinyougou.pojo.TbItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ItemSearchDao extends ElasticsearchRepository<TbItem,Long> {
}
