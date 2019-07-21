package com.pinyougou.es.dao;

import com.pinyougou.pojo.TbItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ItemDao extends ElasticsearchRepository<TbItem,Long> {
}
