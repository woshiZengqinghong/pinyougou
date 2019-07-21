package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired; 
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbAreasMapper;
import com.pinyougou.pojo.TbAreas;  

import com.pinyougou.sellergoods.service.AreasService;



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class AreasServiceImpl extends CoreServiceImpl<TbAreas>  implements AreasService {

	
	private TbAreasMapper areasMapper;

	@Autowired
	public AreasServiceImpl(TbAreasMapper areasMapper) {
		super(areasMapper, TbAreas.class);
		this.areasMapper=areasMapper;
	}

	
	

	
	@Override
    public PageInfo<TbAreas> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbAreas> all = areasMapper.selectAll();
        PageInfo<TbAreas> info = new PageInfo<TbAreas>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbAreas> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbAreas> findPage(Integer pageNo, Integer pageSize, TbAreas areas) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbAreas.class);
        Example.Criteria criteria = example.createCriteria();

        if(areas!=null){			
						if(StringUtils.isNotBlank(areas.getAreaid())){
				criteria.andLike("areaid","%"+areas.getAreaid()+"%");
				//criteria.andAreaidLike("%"+areas.getAreaid()+"%");
			}
			if(StringUtils.isNotBlank(areas.getArea())){
				criteria.andLike("area","%"+areas.getArea()+"%");
				//criteria.andAreaLike("%"+areas.getArea()+"%");
			}
			if(StringUtils.isNotBlank(areas.getCityid())){
				criteria.andLike("cityid","%"+areas.getCityid()+"%");
				//criteria.andCityidLike("%"+areas.getCityid()+"%");
			}
	
		}
        List<TbAreas> all = areasMapper.selectByExample(example);
        PageInfo<TbAreas> info = new PageInfo<TbAreas>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbAreas> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }
	
}
