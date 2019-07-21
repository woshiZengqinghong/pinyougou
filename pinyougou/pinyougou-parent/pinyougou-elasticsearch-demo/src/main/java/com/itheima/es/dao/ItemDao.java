package com.itheima.es.dao;

import com.itheima.model.TbItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface ItemDao extends ElasticsearchRepository<TbItem,Long> {

}
