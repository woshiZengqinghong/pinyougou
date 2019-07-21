import com.itcast.es.dao.ItemDao;
import com.itcast.es.model.TbItem;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-es.xml")
public class ElasticSearchTest {

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    ItemDao itemDao;

    @Test
    public void createIndexAndMapping() {
        //创建索引
        elasticsearchTemplate.createIndex(TbItem.class);
        //创建映射
        elasticsearchTemplate.putMapping(TbItem.class);
    }

    /***
     * 过滤查询
     */
    @Test
    public void queryByFilter(){
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withIndices("pinyougou");
        queryBuilder.withTypes("item");
        queryBuilder.withQuery(QueryBuilders.matchQuery("title","商品"));

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.termQuery("specMap.网络制式.keyword","移动4G"));
        boolQueryBuilder.filter(QueryBuilders.termQuery("specMap.机身内存.keyword","128G"));

        queryBuilder.withFilter(boolQueryBuilder);
        NativeSearchQuery searchQuery = queryBuilder.build();

        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class);
        for (TbItem tbItem : tbItems) {
            System.out.println(tbItem);
        }
    }

    /***
     * 对象查询
     */
    @Test
    public void queryByObject(){
        SearchQuery query = new NativeSearchQuery(QueryBuilders.matchQuery("specMap.网络制式.keyword", "移动4G"));
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(query, TbItem.class);
        for (TbItem tbItem : tbItems) {
            System.out.println(tbItem);
        }
    }

    @Test
    public void save(){
        TbItem tbitem = new TbItem();
        tbitem.setId(30000L);
        Map<String, String> map = new HashMap<>();
        map.put("网络制式","移动4G");
        map.put("机身内存","128G");
        tbitem.setSpecMap(map);//规格的数据 规格的名称(key) 和规格的选项值(value)
        tbitem.setTitle("测试商品111");
        tbitem.setCategory("商品分类111");
        tbitem.setGoodsId(10000L);
        tbitem.setBrand("三星");
        tbitem.setSeller("三星旗舰店");
        itemDao.save(tbitem);
    }


    @Test
    public void copyTo(){
        //查询对象 条件
        SearchQuery searchQuery = new NativeSearchQuery(QueryBuilders.matchQuery("keyword","华为"));
        //查询
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class);
        //获取结果
        System.out.println("总数量" + tbItems.getTotalElements());
        System.out.println("总页数：" + tbItems.getTotalPages());
        List<TbItem> content = tbItems.getContent();
        for (TbItem tbItem : content) {
            System.out.println("count:"+tbItem.getTitle());
        }
    }

    @Test
    public void wildcardQuery(){
        //创建查询对象 查询条件
        SearchQuery searchQuery = new NativeSearchQuery(QueryBuilders.wildcardQuery("title","商?"));
        //查询
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class);
        //获取数据
        System.out.println("总数据："+tbItems.getContent());
        System.out.println("totalPage:"+tbItems.getTotalPages());
        System.out.println("总条数" + tbItems.getTotalElements());
    }

    @Test
    public void match(){
        //创建查询对象 查询条件
        SearchQuery searchQuery = new NativeSearchQuery(QueryBuilders.matchQuery("title", "商品111"));
        //查询
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class);
        //获取数据
        System.out.println("总数据："+tbItems.getContent());
        System.out.println("totalPage:"+tbItems.getTotalPages());
        System.out.println("总条数" + tbItems.getTotalElements());
    }

    @Test
    public void findById(){
        Optional<TbItem> item = itemDao.findById(30000L);
        System.out.println("item:"+item+"    "+"item.get().getBrand():"+item.get().getBrand());
    }

    @Test
    public void update(){
        for (int i = 0; i < 100; i++) {
            TbItem tbitem = new TbItem();
            tbitem.setId((30000L+i));
            tbitem.setTitle("测试商品华为"+i);
            tbitem.setCategory("商品分类111"+i);
            tbitem.setGoodsId(10000L);
            tbitem.setBrand("华为"+i);
            tbitem.setSeller("华为旗舰店"+i);
            itemDao.save(tbitem);
        }
    }

    @Test
    public void findAll(){
        Iterable<TbItem> tbItems = itemDao.findAll();
        for (TbItem tbItem : tbItems) {
            System.out.println(tbItem.getTitle());
        }
    }

    @Test
    public void showPage(){
        Pageable pageable= PageRequest.of(0, 10);
        Page<TbItem> all = itemDao.findAll(pageable);
        System.out.println(all.getTotalElements());
        System.out.println(all.getTotalPages());
    }

    @Test
    public void delete(){
        itemDao.deleteAll();
    }

}
