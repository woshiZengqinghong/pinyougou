package com.itheima;

import com.itheima.es.dao.ItemDao;
import com.itheima.model.TbItem;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:spring-es.xml")
public class TestElasticSearch {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ItemDao itemDao;

    @Test
    public void queryByFilter(){
        //1.创建查询对象的构建对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //2.查询条件
        queryBuilder.withIndices("pinyougou");//设置从哪一个索引查询
        queryBuilder.withTypes("item");//设置从哪一个类型中查询

        queryBuilder.withQuery(QueryBuilders.matchQuery("title", "商品"));////从title 中查询内容为商品的数据

        //3.创建过滤查询(规格的过滤查询 多个过滤使用bool查询)
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.filter(QueryBuilders.termQuery("specMap.网络制式.keyword","移动4G"));

        boolQueryBuilder.filter(QueryBuilders.termQuery("specMap.机身内存.keyword","16G"));

        queryBuilder.withFilter(boolQueryBuilder);

        //4.构建 查询条件
        NativeSearchQuery searchQuery = queryBuilder.build();
        //5.执行查询
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class);

        long totalElements = tbItems.getTotalElements();
        System.out.println("总记录数为："+totalElements);

        List<TbItem> content = tbItems.getContent();

        for (TbItem tbItem : content) {
            System.out.println(tbItem.getTitle()+"=="+tbItem.getSpecMap());
        }

    }
    //索引的时候分词了 查询的时候先分词再进行查询匹配 并通过OR进行连接 并集显示 所以有数据
    @Test
    public void queryByMathQuery(){
        NativeSearchQuery query = new NativeSearchQuery(QueryBuilders.matchQuery("specMap.网络制式.keyword", "移动4G"));

        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(query, TbItem.class);
        long totalElements = tbItems.getTotalElements();
        System.out.println("总记录数："+totalElements);
        List<TbItem> content = tbItems.getContent();

        for (TbItem tbItem : content) {
            System.out.println(tbItem.getTitle()+":"+tbItem.getSpecMap());
        }
    }

    @Test
    public void queryByWildcardQuery(){
        NativeSearchQuery query = new NativeSearchQuery(QueryBuilders.wildcardQuery("keyword","商?"));
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(query, TbItem.class);

        long totalElements = tbItems.getTotalElements();
        System.out.println("总记录数："+totalElements);

        List<TbItem> contents = tbItems.getContent();

        for (TbItem tbItem : contents) {
            System.out.println(tbItem.getTitle());
        }
    }
    @Test
    public void queryByPageable(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<TbItem> all = itemDao.findAll(pageable);
        for (TbItem tbItem : all) {
            System.out.println(tbItem.getTitle());

        }
    }
    @Test
    public void queryById(){
        System.out.println(itemDao.findById(20000L));

    }
    //查询    查询所有
    @Test
    public void QueryById(){
        Iterable<TbItem> list = itemDao.findAll();
        for (TbItem tbItem : list) {
            System.out.println(tbItem.getId()+":"+tbItem.getBrand()+":"+tbItem.getCategory());
        }
    }

    @Test
    public void update(){
        TbItem tbItem = new TbItem();
        tbItem.setId(30000L);
        tbItem.setTitle("测试商品333");
        tbItem.setCategory("商品分类333");
        tbItem.setBrand("三星");
        tbItem.setSeller("三星旗舰店");

        itemDao.save(tbItem);
    }
    /**
     * 删除文档
     */
    @Test
    public void delteById(){
        itemDao.deleteById(20000L);
    }

    /**
     * 插入
     */
    @Test
    public void saveData(){
        TbItem tbItem = new TbItem();
        tbItem.setId(30000L);
        tbItem.setTitle("测试商品");
        tbItem.setCategory("商品分类1");
        tbItem.setBrand("三星");
        tbItem.setSeller("三星旗舰店");

        HashMap<String, String> map = new HashMap<>();
        map.put("网络制式","移动4G");
        map.put("机身内存","16G");
        tbItem.setSpecMap(map);



        itemDao.save(tbItem);
    }
    /**
     * 创建索引和映射
     */
    @Test
    public void testCreateIndexAndMapping(){
        //创建索引
        elasticsearchTemplate.createIndex(TbItem.class);
        //创建映射
        elasticsearchTemplate.putMapping(TbItem.class);
    }
}
