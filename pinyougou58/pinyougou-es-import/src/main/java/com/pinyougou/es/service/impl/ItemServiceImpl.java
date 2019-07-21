package com.pinyougou.es.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.es.dao.ItemDao;
import com.pinyougou.es.service.ItemService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private ItemDao itemDao;

    @Override
    public void ImportDataToEs() {
        //数据库查出item表中数据
        TbItem tbItem = new TbItem();
        tbItem.setStatus("1");
        List<TbItem> itemList = itemMapper.select(tbItem);
        //存入es服务器中
        for (TbItem item : itemList) {
            if (!StringUtils.isEmpty(item.getSpec())) {
                Map<String,String> map = JSON.parseObject(item.getSpec(), Map.class);
                item.setSpecMap(map);
            }
        }
        itemDao.saveAll(itemList);
    }
}
