package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.content.service.BrandService;
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
public class BrandServiceImpl extends CoreServiceImpl<TbBrand>  implements BrandService {

	
	private TbBrandMapper brandMapper;

	@Autowired
	public BrandServiceImpl(TbBrandMapper brandMapper) {
		super(brandMapper, TbBrand.class);
		this.brandMapper=brandMapper;
	}

	
	

	
	@Override
    public PageInfo<TbBrand> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbBrand> all = brandMapper.selectAll();
        PageInfo<TbBrand> info = new PageInfo<TbBrand>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbBrand> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbBrand> findPage(Integer pageNo, Integer pageSize, TbBrand brand) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();

        if(brand!=null){			
						if(StringUtils.isNotBlank(brand.getName())){
				criteria.andLike("name","%"+brand.getName()+"%");
				//criteria.andNameLike("%"+brand.getName()+"%");
			}
			if(StringUtils.isNotBlank(brand.getFirstChar())){
				criteria.andLike("firstChar","%"+brand.getFirstChar()+"%");
				//criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
			}
	
		}
        List<TbBrand> all = brandMapper.selectByExample(example);
        PageInfo<TbBrand> info = new PageInfo<TbBrand>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbBrand> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }
	
}
