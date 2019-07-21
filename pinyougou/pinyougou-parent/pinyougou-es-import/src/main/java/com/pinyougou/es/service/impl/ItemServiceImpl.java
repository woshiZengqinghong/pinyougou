package com.pinyougou.es.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.es.dao.ItemDao;
import com.pinyougou.es.service.ItemService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private TbItemMapper tbItemMapper;

    @Override
    public void importDataToEs() {

        //1.从数据库查询出符合条件的tbItem的数据

        TbItem tbItem = new TbItem();
        tbItem.setStatus("1");//审核过的
        List<TbItem> itemList = tbItemMapper.select(tbItem);

        for (TbItem item : itemList) {
            String spec = item.getSpec();
            if (StringUtils.isNotBlank(spec)) {
                Map<String,String> map = JSON.parseObject(spec, Map.class);
                item.setSpecMap(map);
            }
        }

        //2保存
        itemDao.saveAll(itemList);
    }
}
