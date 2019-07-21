package com.pinyougou.search.dao;

import com.pinyougou.pojo.TbItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

public interface ItemSearchDao extends ElasticsearchCrudRepository<TbItem,Long>{
}
