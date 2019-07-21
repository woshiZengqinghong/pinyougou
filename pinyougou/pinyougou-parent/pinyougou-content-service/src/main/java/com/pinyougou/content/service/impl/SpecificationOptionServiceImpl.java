package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.content.service.SpecificationOptionService;
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
public class SpecificationOptionServiceImpl extends CoreServiceImpl<TbSpecificationOption>  implements SpecificationOptionService {

	
	private TbSpecificationOptionMapper specificationOptionMapper;

	@Autowired
	public SpecificationOptionServiceImpl(TbSpecificationOptionMapper specificationOptionMapper) {
		super(specificationOptionMapper, TbSpecificationOption.class);
		this.specificationOptionMapper=specificationOptionMapper;
	}

	
	

	
	@Override
    public PageInfo<TbSpecificationOption> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbSpecificationOption> all = specificationOptionMapper.selectAll();
        PageInfo<TbSpecificationOption> info = new PageInfo<TbSpecificationOption>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSpecificationOption> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbSpecificationOption> findPage(Integer pageNo, Integer pageSize, TbSpecificationOption specificationOption) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbSpecificationOption.class);
        Example.Criteria criteria = example.createCriteria();

        if(specificationOption!=null){			
						if(StringUtils.isNotBlank(specificationOption.getOptionName())){
				criteria.andLike("optionName","%"+specificationOption.getOptionName()+"%");
				//criteria.andOptionNameLike("%"+specificationOption.getOptionName()+"%");
			}
	
		}
        List<TbSpecificationOption> all = specificationOptionMapper.selectByExample(example);
        PageInfo<TbSpecificationOption> info = new PageInfo<TbSpecificationOption>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSpecificationOption> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }
	
}
