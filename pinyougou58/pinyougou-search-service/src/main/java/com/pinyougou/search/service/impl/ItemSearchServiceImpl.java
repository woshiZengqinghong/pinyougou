package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.dao.ItemSearchDao;
import com.pinyougou.search.service.ItemSearchService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.*;
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
import org.springframework.util.StringUtils;

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
    private ItemSearchDao itemSearchDao;

    @Override
    public void deleteByIds(Long[] ids) {
        DeleteQuery deleteQuery = new DeleteQuery();
        deleteQuery.setQuery(QueryBuilders.termsQuery("goodsId", ids));
        elasticsearchTemplate.delete(deleteQuery, TbItem.class);
    }

    @Override
    public void updateIndex(List<TbItem> tbItemList) {
        //遍历 设置specMap
        for (TbItem tbItem : tbItemList) {
            Map<String, String> specMap = JSON.parseObject(tbItem.getSpec(), Map.class);
            tbItem.setSpecMap(specMap);
        }
        itemSearchDao.saveAll(tbItemList);
    }

    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        //根据关键字搜索
        String keywords = (String) searchMap.get("keywords");
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        if (!StringUtils.isEmpty(keywords)) {
            searchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(keywords, "category", "brand", "seller", "title"));

            //聚合函数 设置category
            searchQueryBuilder.addAggregation(AggregationBuilders.terms("category_group").field("category").size(50));
        } else {
            //匹配所有
            searchQueryBuilder.withQuery(QueryBuilders.matchAllQuery());
        }

        //设置高亮
        searchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("title"))
                .withHighlightBuilder(new HighlightBuilder()
                        .preTags("<em style=\"color:red\">")
                        .postTags("</em>"));

        //过滤查询  商品分类过滤
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //分类过滤
        String category = (String) searchMap.get("category");
        if (!StringUtils.isEmpty(category)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("category", category));  //filter 就相当于must
        }

        //品牌过滤
        String brand = (String) searchMap.get("brand");
        if (!StringUtils.isEmpty(brand)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("brand", brand));
        }

        //规格过滤
        Map<String, String> spec = (Map<String, String>) searchMap.get("spec");
        if (spec != null) {
            for (String key : spec.keySet()) {
                String value = spec.get(key);
                boolQueryBuilder.filter(QueryBuilders.termQuery("specMap." + key + ".keyword", value));
            }
        }

        //价格过滤
        String price = (String) searchMap.get("price");
        if (!StringUtils.isEmpty(price)) {
            String[] split = price.split("-");
            //无最大值
            if ("*".equals(split[1])) {
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(split[0]));
            } else {
                //有最大值
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").from(split[0], true).to(split[1], true));
            }
        }

        //构建查询对象
        searchQueryBuilder.withFilter(boolQueryBuilder);
        NativeSearchQuery searchQuery = searchQueryBuilder.build();

        //分页
        Integer pageNo = (Integer) searchMap.get("pageNo");
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageNo == null) {
            pageNo = 1;
        }
        if (pageSize == null) {
            pageSize = 40;
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        searchQuery.setPageable(pageable);

        //价格排序
        String sortField = (String) searchMap.get("sortField");
        String sortType = (String) searchMap.get("sortType");
        if (!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortType)) {
            if ("ASC".equals(sortType)) {
                Sort sort = new Sort(Sort.Direction.ASC, sortField);
                searchQuery.addSort(sort);
            }

            if ("DESC".equals(sortType)) {
                Sort sort = new Sort(Sort.Direction.DESC, sortField);
                searchQuery.addSort(sort);
            }
        }

        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                List<T> content = new ArrayList<>();
                SearchHits hits = searchResponse.getHits();

                if (hits == null || hits.getHits().length <= 0) {
                    return new AggregatedPageImpl(content);
                }

                for (SearchHit hit : hits) {
                    TbItem tbItem = JSON.parseObject(hit.getSourceAsString(), TbItem.class);
                    if (hit.getHighlightFields() != null && hit.getHighlightFields().get("title") != null && hit.getHighlightFields().get("title").getFragments() != null) {

                        //高亮域
                        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                        HighlightField highlightField = highlightFields.get("title");

                        //高亮碎片
                        Text[] fragments = highlightField.getFragments();
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Text fragment : fragments) {
                            stringBuilder.append(fragment);
                        }
                        tbItem.setTitle(stringBuilder.toString());
                        content.add((T) tbItem);
                    } else {
                        content.add((T) tbItem);
                    }
                }

                long total = hits.getTotalHits();
                Aggregations aggregations = searchResponse.getAggregations();
                String scrollId = searchResponse.getScrollId();
                AggregatedPageImpl<T> aggregatedPage = new AggregatedPageImpl<T>(content, pageable, total, aggregations, scrollId);
                return aggregatedPage;
            }
        });

        //封装数据
        //categoryList brandList specList
        List<String> categoryList = new ArrayList<>();
        Aggregation category_group = tbItems.getAggregation("category_group");

        if (category_group != null) {
            StringTerms terms = (StringTerms) category_group;
            List<StringTerms.Bucket> buckets = terms.getBuckets();
            for (StringTerms.Bucket bucket : buckets) {
                categoryList.add(bucket.getKeyAsString());
            }
        }

        //分页数据集合 总条数 总页数
        List<TbItem> content = tbItems.getContent();
        int totalPages = tbItems.getTotalPages();
        long totalElements = tbItems.getTotalElements();

        //存入集合
        Map<String, Object> resultMap = new HashMap<>();

        if (categoryList != null && categoryList.size() > 0) {
            //有商品分类
            if (!StringUtils.isEmpty(category)) {
                Map map = searchBrandAndSpecList(category);
                resultMap.putAll(map);
            } else {
                Map map = searchBrandAndSpecList(categoryList.get(0));
                resultMap.putAll(map);
            }
        } else {
            resultMap.putAll(new HashMap<>());
        }

        resultMap.put("rows", content);
        resultMap.put("total", totalElements);
        resultMap.put("totalPages", totalPages);
        resultMap.put("categoryList", categoryList);

        return resultMap;
    }

    public Map searchBrandAndSpecList(String category) {
        Long typeTempId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        Map<Object, Object> map = new HashMap<>();
        if (typeTempId != null) {
            //品牌列表
            List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeTempId);
            //规格列表
            List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeTempId);
            map.put("brandList", brandList);
            map.put("specList", specList);
        }
        return map;
    }
}
