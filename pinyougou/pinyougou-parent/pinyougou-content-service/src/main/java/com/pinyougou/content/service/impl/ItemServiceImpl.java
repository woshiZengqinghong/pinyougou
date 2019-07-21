package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.content.service.ItemService;
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
public class ItemServiceImpl extends CoreServiceImpl<TbItem>  implements ItemService {

	
	private TbItemMapper itemMapper;

	@Autowired
	public ItemServiceImpl(TbItemMapper itemMapper) {
		super(itemMapper, TbItem.class);
		this.itemMapper=itemMapper;
	}

	
	

	
	@Override
    public PageInfo<TbItem> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbItem> all = itemMapper.selectAll();
        PageInfo<TbItem> info = new PageInfo<TbItem>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbItem> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbItem> findPage(Integer pageNo, Integer pageSize, TbItem item) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbItem.class);
        Example.Criteria criteria = example.createCriteria();

        if(item!=null){			
						if(StringUtils.isNotBlank(item.getTitle())){
				criteria.andLike("title","%"+item.getTitle()+"%");
				//criteria.andTitleLike("%"+item.getTitle()+"%");
			}
			if(StringUtils.isNotBlank(item.getSellPoint())){
				criteria.andLike("sellPoint","%"+item.getSellPoint()+"%");
				//criteria.andSellPointLike("%"+item.getSellPoint()+"%");
			}
			if(StringUtils.isNotBlank(item.getBarcode())){
				criteria.andLike("barcode","%"+item.getBarcode()+"%");
				//criteria.andBarcodeLike("%"+item.getBarcode()+"%");
			}
			if(StringUtils.isNotBlank(item.getImage())){
				criteria.andLike("image","%"+item.getImage()+"%");
				//criteria.andImageLike("%"+item.getImage()+"%");
			}
			if(StringUtils.isNotBlank(item.getStatus())){
				criteria.andLike("status","%"+item.getStatus()+"%");
				//criteria.andStatusLike("%"+item.getStatus()+"%");
			}
			if(StringUtils.isNotBlank(item.getItemSn())){
				criteria.andLike("itemSn","%"+item.getItemSn()+"%");
				//criteria.andItemSnLike("%"+item.getItemSn()+"%");
			}
			if(StringUtils.isNotBlank(item.getIsDefault())){
				criteria.andLike("isDefault","%"+item.getIsDefault()+"%");
				//criteria.andIsDefaultLike("%"+item.getIsDefault()+"%");
			}
			if(StringUtils.isNotBlank(item.getSellerId())){
				criteria.andLike("sellerId","%"+item.getSellerId()+"%");
				//criteria.andSellerIdLike("%"+item.getSellerId()+"%");
			}
			if(StringUtils.isNotBlank(item.getCartThumbnail())){
				criteria.andLike("cartThumbnail","%"+item.getCartThumbnail()+"%");
				//criteria.andCartThumbnailLike("%"+item.getCartThumbnail()+"%");
			}
			if(StringUtils.isNotBlank(item.getCategory())){
				criteria.andLike("category","%"+item.getCategory()+"%");
				//criteria.andCategoryLike("%"+item.getCategory()+"%");
			}
			if(StringUtils.isNotBlank(item.getBrand())){
				criteria.andLike("brand","%"+item.getBrand()+"%");
				//criteria.andBrandLike("%"+item.getBrand()+"%");
			}
			if(StringUtils.isNotBlank(item.getSpec())){
				criteria.andLike("spec","%"+item.getSpec()+"%");
				//criteria.andSpecLike("%"+item.getSpec()+"%");
			}
			if(StringUtils.isNotBlank(item.getSeller())){
				criteria.andLike("seller","%"+item.getSeller()+"%");
				//criteria.andSellerLike("%"+item.getSeller()+"%");
			}
	
		}
        List<TbItem> all = itemMapper.selectByExample(example);
        PageInfo<TbItem> info = new PageInfo<TbItem>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbItem> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }
	
}
