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

import com.pinyougou.mapper.TbFreightTemplateMapper;
import com.pinyougou.pojo.TbFreightTemplate;  

import com.pinyougou.sellergoods.service.FreightTemplateService;



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class FreightTemplateServiceImpl extends CoreServiceImpl<TbFreightTemplate>  implements FreightTemplateService {

	
	private TbFreightTemplateMapper freightTemplateMapper;

	@Autowired
	public FreightTemplateServiceImpl(TbFreightTemplateMapper freightTemplateMapper) {
		super(freightTemplateMapper, TbFreightTemplate.class);
		this.freightTemplateMapper=freightTemplateMapper;
	}

	
	

	
	@Override
    public PageInfo<TbFreightTemplate> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbFreightTemplate> all = freightTemplateMapper.selectAll();
        PageInfo<TbFreightTemplate> info = new PageInfo<TbFreightTemplate>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbFreightTemplate> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbFreightTemplate> findPage(Integer pageNo, Integer pageSize, TbFreightTemplate freightTemplate) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbFreightTemplate.class);
        Example.Criteria criteria = example.createCriteria();

        if(freightTemplate!=null){			
						if(StringUtils.isNotBlank(freightTemplate.getSellerId())){
				criteria.andLike("sellerId","%"+freightTemplate.getSellerId()+"%");
				//criteria.andSellerIdLike("%"+freightTemplate.getSellerId()+"%");
			}
			if(StringUtils.isNotBlank(freightTemplate.getIsDefault())){
				criteria.andLike("isDefault","%"+freightTemplate.getIsDefault()+"%");
				//criteria.andIsDefaultLike("%"+freightTemplate.getIsDefault()+"%");
			}
			if(StringUtils.isNotBlank(freightTemplate.getName())){
				criteria.andLike("name","%"+freightTemplate.getName()+"%");
				//criteria.andNameLike("%"+freightTemplate.getName()+"%");
			}
			if(StringUtils.isNotBlank(freightTemplate.getSendTimeType())){
				criteria.andLike("sendTimeType","%"+freightTemplate.getSendTimeType()+"%");
				//criteria.andSendTimeTypeLike("%"+freightTemplate.getSendTimeType()+"%");
			}
	
		}
        List<TbFreightTemplate> all = freightTemplateMapper.selectByExample(example);
        PageInfo<TbFreightTemplate> info = new PageInfo<TbFreightTemplate>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbFreightTemplate> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }
	
}
