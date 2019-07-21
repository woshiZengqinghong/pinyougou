package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    TbGoodsMapper goodsMapper;

    @Autowired
    TbGoodsDescMapper goodsDescMapper;

    @Autowired
    FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    TbItemMapper itemMapper;

    @Override
    public void genItemHtml(Long goodsId) {
        //获取goodsId 对应的goods goodsDesc
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        //调用genHTML 生成html
        genHTML("item.ftl", tbGoods, tbGoodsDesc);
    }

    /***
     * 删除静态页面
     * @param ids
     */
    @Override
    public void deleteByIds(Long[] ids) throws IOException {
        for (Long goodsId : ids) {
            FileUtils.forceDelete(new File("E:\\html\\"+goodsId+".html"));
        }
    }

    private void genHTML(String templateName, TbGoods tbGoods, TbGoodsDesc tbGoodsDesc) {
        FileWriter fileWriter = null;
        try {
            //创建配置对象
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            //设置编码  加载模板
            Template template = configuration.getTemplate(templateName);
            //加载数据
            Map map = new HashMap<String,Object>();
            map.put("tbGoods",tbGoods);
            map.put("tbGoodsDesc",tbGoodsDesc);

            //js存入sku数据
            Example example = new Example(TbItem.class);
            //查询条件： goodsId status is_default
            example.createCriteria().andEqualTo("goodsId",tbGoods.getId()).andEqualTo("status","1");
            example.setOrderByClause("is_default desc");
            List<TbItem> skuList = itemMapper.selectByExample(example);
            map.put("skuList",skuList);

            //创建输出流
            /*ResourceBundle bundle = ResourceBundle.getBundle("/resources/properties/config");
            String pageDir = bundle.getString("pageDir");*/

            fileWriter = new FileWriter(new File("E:\\html\\"+tbGoods.getId()+".html"));
            //freeMarker创建静态页面
            template.process(map,fileWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {//关闭流
            if (fileWriter!=null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
