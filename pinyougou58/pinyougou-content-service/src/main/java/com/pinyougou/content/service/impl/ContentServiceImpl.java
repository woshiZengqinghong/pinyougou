package com.pinyougou.content.service.impl;
import java.util.Arrays;
import java.util.List;

import com.pinyougou.content.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;  



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl extends CoreServiceImpl<TbContent>  implements ContentService {

	private TbContentMapper contentMapper;

	@Autowired
	public ContentServiceImpl(TbContentMapper contentMapper) {
		super(contentMapper, TbContent.class);
		this.contentMapper=contentMapper;
	}

	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public List<TbContent> findByCategoryId(Long categoryId) {
		//从redis中查询数据
		List<TbContent> contentList = (List<TbContent>) redisTemplate.boundHashOps("CONTENT_KEY").get(categoryId);
		if (contentList!=null && contentList.size()>0) {
			System.out.println("读取redis");
			return contentList;
		}

		System.out.println("读取数据库");
		//redis中没有数据 数据库中查询数据
		TbContent tbContent = new TbContent();
		tbContent.setCategoryId(categoryId);
		tbContent.setStatus("1");
		List<TbContent> contents = contentMapper.select(tbContent);

		//将数据插入到缓存中
		redisTemplate.boundHashOps("CONTENT_KEY").put(categoryId,contents);
		return contents;
	}

	@Override
    public PageInfo<TbContent> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbContent> all = contentMapper.selectAll();
        PageInfo<TbContent> info = new PageInfo<TbContent>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbContent> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbContent> findPage(Integer pageNo, Integer pageSize, TbContent content) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();

        if(content!=null){			
						if(StringUtils.isNotBlank(content.getTitle())){
				criteria.andLike("title","%"+content.getTitle()+"%");
				//criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(StringUtils.isNotBlank(content.getUrl())){
				criteria.andLike("url","%"+content.getUrl()+"%");
				//criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(StringUtils.isNotBlank(content.getPic())){
				criteria.andLike("pic","%"+content.getPic()+"%");
				//criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(StringUtils.isNotBlank(content.getStatus())){
				criteria.andLike("status","%"+content.getStatus()+"%");
				//criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
        List<TbContent> all = contentMapper.selectByExample(example);
        PageInfo<TbContent> info = new PageInfo<TbContent>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbContent> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

}
