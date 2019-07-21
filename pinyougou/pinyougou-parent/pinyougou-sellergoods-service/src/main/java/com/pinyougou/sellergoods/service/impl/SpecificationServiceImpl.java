package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.List;

import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.Specification;
import com.pinyougou.pojo.TbSpecificationOption;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecification;  

import com.pinyougou.sellergoods.service.SpecificationService;



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl extends CoreServiceImpl<TbSpecification>  implements SpecificationService {

	
	private TbSpecificationMapper specificationMapper;



	@Autowired
	public SpecificationServiceImpl(TbSpecificationMapper specificationMapper) {
		super(specificationMapper, TbSpecification.class);
		this.specificationMapper=specificationMapper;
	}

	@Autowired
    private TbSpecificationOptionMapper tbSpecificationOptionMapper;


    @Override
    public void add(Specification specification) {
        TbSpecification specification1 = specification.getSpecification();
        //插入规格表
        specificationMapper.insert(specification1);

        //插入规格选项表
        List<TbSpecificationOption> optionList = specification.getOptionList();
        for (TbSpecificationOption tbSpecificationOption : optionList) {
            tbSpecificationOption.setSpecId(specification1.getId());
            tbSpecificationOptionMapper.insert(tbSpecificationOption);
        }
    }

    @Override
    public Specification findOne(Long id) {
        Specification specification = new Specification();

        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        //添加规格属性
        specification.setSpecification(tbSpecification);

        Example example = new Example(TbSpecificationOption.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("specId",tbSpecification.getId());
        List<TbSpecificationOption> tbSpecificationOptions = tbSpecificationOptionMapper.selectByExample(example);
        //添加规格选项属性
        specification.setOptionList(tbSpecificationOptions);
        return specification;
    }

    @Override
    public Specification update(Specification specification) {
        //更新规格表
        TbSpecification tbSpecification = specification.getSpecification();
        specificationMapper.updateByPrimaryKey(tbSpecification);

        //更新规格选项表,先删除此规格的规格选项，再重新插入
        TbSpecificationOption option= new TbSpecificationOption();
        option.setSpecId(specification.getSpecification().getId());
        int delete = tbSpecificationOptionMapper.delete(option);

        List<TbSpecificationOption> optionList = specification.getOptionList();
        for (TbSpecificationOption tbSpecificationOption : optionList) {
            tbSpecificationOption.setSpecId(specification.getSpecification().getId());
            tbSpecificationOptionMapper.insert(tbSpecificationOption);
        }
        return null;
    }

    @Override
    public void delete(Long[] ids) {
        Example example = new Example(TbSpecification.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",Arrays.asList(ids));
        specificationMapper.deleteByExample(example);

        for (Long id : ids) {
            TbSpecificationOption tbSpecificationOption = new TbSpecificationOption();
            tbSpecificationOption.setSpecId(id);
            tbSpecificationOptionMapper.delete(tbSpecificationOption);
        }
    }


    @Override
    public PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbSpecification> all = specificationMapper.selectAll();
        PageInfo<TbSpecification> info = new PageInfo<TbSpecification>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSpecification> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize, TbSpecification specification) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbSpecification.class);
        Example.Criteria criteria = example.createCriteria();

        if(specification!=null){			
						if(StringUtils.isNotBlank(specification.getSpecName())){
				criteria.andLike("specName","%"+specification.getSpecName()+"%");
				//criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
        List<TbSpecification> all = specificationMapper.selectByExample(example);
        PageInfo<TbSpecification> info = new PageInfo<TbSpecification>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSpecification> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }
	
}
