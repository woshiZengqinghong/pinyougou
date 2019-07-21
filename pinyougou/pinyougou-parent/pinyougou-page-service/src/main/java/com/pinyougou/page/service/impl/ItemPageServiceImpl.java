package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private TbGoodsMapper tbGoodsMapper;

    @Value("${pageDir}")
    private String pageDir;

    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Override
    public void genItemHtml(Long goodsId) {
        //查询数据库带的商品的数据 生成静态页面
        //1.根据SPU的ID查询商品的信息(goods goodsDesc)
        TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(goodsId);
        TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodsId);

        //2.使用freemarker 创建模板  使用的数据集 生成静态页面
        genHTML("item.ftl", tbGoods, tbGoodsDesc);
    }

    @Override
    public void deleteById(Long[] goodsId) {
        try {
            for (Long aLong : goodsId) {
                FileUtils.forceDelete(new File(pageDir + aLong + ".html"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void genHTML(String templateName, TbGoods tbGoods, TbGoodsDesc tbGoodsDesc) {
        FileWriter writer = null;

        try {
            //1.创建一个configuration对象
            //2.设置字符编码 和 模板加载的目录
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            configuration.setDefaultEncoding("UTF-8");
            //3.获取模板对象
            Template template = configuration.getTemplate(templateName);
            //4.获取数据集
            Map model = new HashMap<>();
            model.put("tbGoods", tbGoods);
            model.put("tbGoodsDesc", tbGoodsDesc);

            TbItemCat tbItemCat1 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id());
            TbItemCat tbItemCat2 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id());
            TbItemCat tbItemCat3 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
            model.put("tbItemCat1", tbItemCat1);
            model.put("tbItemCat2", tbItemCat2);
            model.put("tbItemCat3", tbItemCat3);

            Example example = new Example(TbItem.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("goodsId", tbGoods.getId())
                    .andEqualTo("status", "1");

            example.setOrderByClause("is_default desc");

            List<TbItem> tbItems = tbItemMapper.selectByExample(example);
            model.put("skuList", tbItems);
            //5.创建一个写流
            writer = new FileWriter(new File(pageDir + tbGoods.getId() + ".html"));
            //6.调用模板对象的process方法输出到指定的文件中
            template.process(model, writer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关流
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
