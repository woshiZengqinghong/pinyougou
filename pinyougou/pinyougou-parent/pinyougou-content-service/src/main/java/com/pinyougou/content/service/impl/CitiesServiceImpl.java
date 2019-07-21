package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbCitiesMapper;
import com.pinyougou.pojo.TbCities;
import com.pinyougou.content.service.CitiesService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class CitiesServiceImpl extends CoreServiceImpl<TbCities>  implements CitiesService {

	
	private TbCitiesMapper citiesMapper;

	@Autowired
	public CitiesServiceImpl(TbCitiesMapper citiesMapper) {
		super(citiesMapper, TbCities.class);
		this.citiesMapper=citiesMapper;
	}

	
	

	
	@Override
    public PageInfo<TbCities> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbCities> all = citiesMapper.selectAll();
        PageInfo<TbCities> info = new PageInfo<TbCities>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbCities> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbCities> findPage(Integer pageNo, Integer pageSize, TbCities cities) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbCities.class);
        Example.Criteria criteria = example.createCriteria();

        if(cities!=null){			
						if(StringUtils.isNotBlank(cities.getCityid())){
				criteria.andLike("cityid","%"+cities.getCityid()+"%");
				//criteria.andCityidLike("%"+cities.getCityid()+"%");
			}
			if(StringUtils.isNotBlank(cities.getCity())){
				criteria.andLike("city","%"+cities.getCity()+"%");
				//criteria.andCityLike("%"+cities.getCity()+"%");
			}
			if(StringUtils.isNotBlank(cities.getProvinceid())){
				criteria.andLike("provinceid","%"+cities.getProvinceid()+"%");
				//criteria.andProvinceidLike("%"+cities.getProvinceid()+"%");
			}
	
		}
        List<TbCities> all = citiesMapper.selectByExample(example);
        PageInfo<TbCities> info = new PageInfo<TbCities>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbCities> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }
	
}
