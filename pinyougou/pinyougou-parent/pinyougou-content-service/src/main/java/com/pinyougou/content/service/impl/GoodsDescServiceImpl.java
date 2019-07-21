package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.content.service.GoodsDescService;
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
public class GoodsDescServiceImpl extends CoreServiceImpl<TbGoodsDesc>  implements GoodsDescService {

	
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	public GoodsDescServiceImpl(TbGoodsDescMapper goodsDescMapper) {
		super(goodsDescMapper, TbGoodsDesc.class);
		this.goodsDescMapper=goodsDescMapper;
	}

	
	

	
	@Override
    public PageInfo<TbGoodsDesc> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbGoodsDesc> all = goodsDescMapper.selectAll();
        PageInfo<TbGoodsDesc> info = new PageInfo<TbGoodsDesc>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbGoodsDesc> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbGoodsDesc> findPage(Integer pageNo, Integer pageSize, TbGoodsDesc goodsDesc) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbGoodsDesc.class);
        Example.Criteria criteria = example.createCriteria();

        if(goodsDesc!=null){			
						if(StringUtils.isNotBlank(goodsDesc.getIntroduction())){
				criteria.andLike("introduction","%"+goodsDesc.getIntroduction()+"%");
				//criteria.andIntroductionLike("%"+goodsDesc.getIntroduction()+"%");
			}
			if(StringUtils.isNotBlank(goodsDesc.getSpecificationItems())){
				criteria.andLike("specificationItems","%"+goodsDesc.getSpecificationItems()+"%");
				//criteria.andSpecificationItemsLike("%"+goodsDesc.getSpecificationItems()+"%");
			}
			if(StringUtils.isNotBlank(goodsDesc.getCustomAttributeItems())){
				criteria.andLike("customAttributeItems","%"+goodsDesc.getCustomAttributeItems()+"%");
				//criteria.andCustomAttributeItemsLike("%"+goodsDesc.getCustomAttributeItems()+"%");
			}
			if(StringUtils.isNotBlank(goodsDesc.getItemImages())){
				criteria.andLike("itemImages","%"+goodsDesc.getItemImages()+"%");
				//criteria.andItemImagesLike("%"+goodsDesc.getItemImages()+"%");
			}
			if(StringUtils.isNotBlank(goodsDesc.getPackageList())){
				criteria.andLike("packageList","%"+goodsDesc.getPackageList()+"%");
				//criteria.andPackageListLike("%"+goodsDesc.getPackageList()+"%");
			}
			if(StringUtils.isNotBlank(goodsDesc.getSaleService())){
				criteria.andLike("saleService","%"+goodsDesc.getSaleService()+"%");
				//criteria.andSaleServiceLike("%"+goodsDesc.getSaleService()+"%");
			}
	
		}
        List<TbGoodsDesc> all = goodsDescMapper.selectByExample(example);
        PageInfo<TbGoodsDesc> info = new PageInfo<TbGoodsDesc>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbGoodsDesc> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }
	
}
