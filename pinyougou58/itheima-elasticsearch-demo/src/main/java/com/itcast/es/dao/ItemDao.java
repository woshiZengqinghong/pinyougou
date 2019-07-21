package com.itcast.es.dao;

import com.itcast.es.model.TbItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

public interface ItemDao extends ElasticsearchCrudRepository<TbItem,Long> {
}
