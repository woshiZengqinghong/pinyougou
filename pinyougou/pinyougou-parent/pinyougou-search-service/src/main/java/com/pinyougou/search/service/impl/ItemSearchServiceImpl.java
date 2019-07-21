package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.dao.ItemSearchDao;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ItemSearchDao itemSearchdao;

    private Map searchBrandAndSpecList(String category){
        Map map = new HashMap<>();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);//获取模板id

        if (typeId != null) {
            //根据模板ID查询品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList",brandList);

            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList",specList);
        }

        return map;
    }

    @Override
    public void deleteByIds(Long[] ids) {
        DeleteQuery query = new DeleteQuery();
        //删除多个goodsId
        query.setQuery(QueryBuilders.termsQuery("goodsId",ids));
        //根据删除条件 索引名 和类型
        elasticsearchTemplate.delete(query,TbItem.class);

    }

    @Override
    public void updateIndex(List<TbItem> itemList) {
        //先设置map 再一次性插入
        for (TbItem tbItem : itemList) {
            String spec = tbItem.getSpec();
            Map map = JSON.parseObject(spec, Map.class);
            tbItem.setSpecMap(map);
        }

        itemSearchdao.saveAll(itemList);
    }

    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {

        Map<String,Object> resultMap = new HashMap<>();
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();

        //1.获取关键字
        String keywords = (String) searchMap.get("keywords");

        if (StringUtils.isNotBlank(keywords)) {

            //设置一个聚合查询的条件 ：1.设置聚合查询的名称（别名）2.设置分组的字段
            builder.addAggregation(AggregationBuilders.terms("category_group").field("category").size(50));

            builder.withQuery(QueryBuilders.multiMatchQuery(keywords,"title","brand","category","selller"));
        }else{
            //设置一个聚合查询的条件 ：1.设置聚合查询的名称（别名）2.设置分组的字段
            builder.addAggregation(AggregationBuilders.terms("category_group").field("category").size(50));
            //匹配所有
            builder.withQuery(QueryBuilders.matchAllQuery());
        }

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //1.2 过滤查询  ----商品分类的过滤查询
        String category = (String) searchMap.get("category");
        if (StringUtils.isNotBlank(category)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("category",category));
        }
        //1.3 过滤查询  ----商品品牌的过滤查询
        String brand = (String) searchMap.get("brand");
        if (StringUtils.isNotBlank(brand)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("brand",brand));
        }

        //1.4 过滤查询  ----规格的过滤查询
        Map<String,String> spec = (Map<String, String>) searchMap.get("spec");
        if (spec!=null) {
            for (String key : spec.keySet()) {
                TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("specMap." + key + ".keyword", spec.get(key));
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }

        //1.4=5 过滤查询  ----规格的过滤查询
        String price = (String) searchMap.get("price");
        if (StringUtils.isNotBlank(price)) {
            String[] split = price.split("-");
            if ("*".equals(split[1])) {
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("pirce").gte(split[0]));
            }else{
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").from(split[0],true).to(split[1],true));
            }
        }

        builder.withFilter(boolQueryBuilder);
        //2.设置高亮
        builder
                .withHighlightFields(new HighlightBuilder.Field("title"))
                .withHighlightBuilder(new  HighlightBuilder().preTags("<em style=\"color:red\">").postTags("</em>"));

        //构建查询对象
        NativeSearchQuery query = builder.build();

        //设置分页条件
        Integer pageNo = (Integer) searchMap.get("pageNo");
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageNo==0) {
            pageNo=1;
        }
        if (pageSize==0) {
            pageSize=40;
        }

        query.setPageable(PageRequest.of(pageNo-1,pageSize));

        //设置排序条件  价格排序
        String sortField = (String) searchMap.get("sortField");
        String sortType = (String) searchMap.get("sortType");

        if (StringUtils.isNotBlank(sortField) && StringUtils.isNotBlank(sortType)) {
            if (sortType.equals("ASC")) {
                Sort sort = new Sort(Sort.Direction.ASC, sortField);
                query.addSort(sort);
            }else if(sortType.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC, sortField);
                query.addSort(sort);
            }else{
                System.out.println("不排序");
            }
        }
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(query, TbItem.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                SearchHits hits = searchResponse.getHits();
                List<T> content = new ArrayList<>();
                //如果没有搜索到记录
                if (hits == null || hits.getHits().length <= 0) {
                    return new AggregatedPageImpl(content);
                }

                for (SearchHit hit : hits) {
                    String sourceAsString = hit.getSourceAsString();
                    TbItem tbItem = JSON.parseObject(sourceAsString, TbItem.class);

                    //获取高亮
                    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                    //获取高亮的域为title的高亮对象
                    HighlightField highlightField = highlightFields.get("title");

                    if (highlightField != null) {

                        //获取高亮的碎片
                        Text[] fragments = highlightField.getFragments();
                        StringBuffer sb = new StringBuffer();//高亮的数据
                        if (fragments != null) {
                            for (Text fragment : fragments) {
                                sb.append(fragment.string());//获取到的高亮碎片的值<em styple="colore:red">
                            }
                        }

                        //不为空的时候存储值
                        if (StringUtils.isNotBlank(sb.toString())) {
                            tbItem.setTitle(sb.toString());
                        }
                    }

                    content.add((T) tbItem);
                }

                AggregatedPageImpl<T> aggregatedPage = new AggregatedPageImpl<>(content, pageable, hits.getTotalHits(), searchResponse.getAggregations(), searchResponse.getScrollId());
                return aggregatedPage;
            }
        });

        //获取分组结果
        Aggregation category_group = tbItems.getAggregation("category_group");

        StringTerms terms = (StringTerms) category_group;
        //商品分类分组 结果
        ArrayList<String> categoryList = new ArrayList<>();

        if (terms!=null) {
            for (StringTerms.Bucket bucket : terms.getBuckets()) {
                categoryList.add(bucket.getKeyAsString());
            }
        }
        Map map = null;
        //获取第一个分类下的所有品牌和规格的列表
        if (StringUtils.isNotBlank(category)) {
            map = searchBrandAndSpecList(category);
        }else{
            map = searchBrandAndSpecList(categoryList.get(0));
        }
        resultMap.putAll(map);
        resultMap.put("total",tbItems.getTotalElements());
        resultMap.put("totalPages",tbItems.getTotalPages());
        resultMap.put("rows",tbItems.getContent());
        resultMap.put("categoryList",categoryList);

        return resultMap;
    }
}
