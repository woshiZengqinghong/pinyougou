package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbProvincesMapper;
import com.pinyougou.pojo.TbProvinces;
import com.pinyougou.content.service.ProvincesService;
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
public class ProvincesServiceImpl extends CoreServiceImpl<TbProvinces>  implements ProvincesService {

	
	private TbProvincesMapper provincesMapper;

	@Autowired
	public ProvincesServiceImpl(TbProvincesMapper provincesMapper) {
		super(provincesMapper, TbProvinces.class);
		this.provincesMapper=provincesMapper;
	}

	
	

	
	@Override
    public PageInfo<TbProvinces> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbProvinces> all = provincesMapper.selectAll();
        PageInfo<TbProvinces> info = new PageInfo<TbProvinces>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbProvinces> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbProvinces> findPage(Integer pageNo, Integer pageSize, TbProvinces provinces) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbProvinces.class);
        Example.Criteria criteria = example.createCriteria();

        if(provinces!=null){			
						if(StringUtils.isNotBlank(provinces.getProvinceid())){
				criteria.andLike("provinceid","%"+provinces.getProvinceid()+"%");
				//criteria.andProvinceidLike("%"+provinces.getProvinceid()+"%");
			}
			if(StringUtils.isNotBlank(provinces.getProvince())){
				criteria.andLike("province","%"+provinces.getProvince()+"%");
				//criteria.andProvinceLike("%"+provinces.getProvince()+"%");
			}
	
		}
        List<TbProvinces> all = provincesMapper.selectByExample(example);
        PageInfo<TbProvinces> info = new PageInfo<TbProvinces>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbProvinces> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }
	
}
