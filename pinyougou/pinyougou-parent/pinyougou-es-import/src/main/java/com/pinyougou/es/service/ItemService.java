package com.pinyougou.es.service;

public interface ItemService {

    /**
     * 从数据库中获取数据 导入到ES的索引库
     *
     */
    public void importDataToEs();
}
