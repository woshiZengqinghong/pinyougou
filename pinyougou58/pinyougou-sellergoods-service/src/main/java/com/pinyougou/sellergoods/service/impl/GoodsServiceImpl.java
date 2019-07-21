package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import tk.mybatis.mapper.entity.Example;

import com.pinyougou.sellergoods.service.GoodsService;



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl extends CoreServiceImpl<TbGoods>  implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbSellerMapper tbSellerMapper;

	@Autowired
	private TbBrandMapper brandMapper;

	@Autowired
	public GoodsServiceImpl(TbGoodsMapper goodsMapper) {
		super(goodsMapper, TbGoods.class);
		this.goodsMapper=goodsMapper;
	}

	
	

	
	@Override
    public PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbGoods> all = goodsMapper.selectAll();
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbGoods> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize, TbGoods goods) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDelete",false);
        if(goods!=null){			
						if(StringUtils.isNotBlank(goods.getSellerId())){
				criteria.andEqualTo("sellerId",goods.getSellerId());
				//criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
			}
			if(StringUtils.isNotBlank(goods.getGoodsName())){
				criteria.andLike("goodsName","%"+goods.getGoodsName()+"%");
				//criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(StringUtils.isNotBlank(goods.getAuditStatus())){
				criteria.andEqualTo("auditStatus",goods.getAuditStatus());
				//criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(StringUtils.isNotBlank(goods.getIsMarketable())){
				criteria.andLike("isMarketable","%"+goods.getIsMarketable()+"%");
				//criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(StringUtils.isNotBlank(goods.getCaption())){
				criteria.andLike("caption","%"+goods.getCaption()+"%");
				//criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(StringUtils.isNotBlank(goods.getSmallPic())){
				criteria.andLike("smallPic","%"+goods.getSmallPic()+"%");
				//criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(StringUtils.isNotBlank(goods.getIsEnableSpec())){
				criteria.andLike("isEnableSpec","%"+goods.getIsEnableSpec()+"%");
				//criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
	
		}
        List<TbGoods> all = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbGoods> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

	@Override
	public void add(Goods goods) {
		//1.获取SPU的数据
		TbGoods tbGoods = goods.getGoods();
		tbGoods.setAuditStatus("0");//默认就是未审核的状态
		tbGoods.setIsDelete(false);//不删除的状态
		//2.获取SPU对应的描述的数据
		TbGoodsDesc goodsDesc = goods.getGoodsDesc();

		//3.获取SKU的列表数据
		List<TbItem> itemList = goods.getItemList();

		//4.插入到三个表中
		goodsMapper.insert(tbGoods);


		//设置描述表的主键
		goodsDesc.setGoodsId(tbGoods.getId());

		goodsDescMapper.insert(goodsDesc);

		saveItems(goods,tbGoods,goodsDesc);
	}

	private void saveItems(Goods goods, TbGoods tbGoods, TbGoodsDesc goodsDesc) {
		if("1".equals(tbGoods.getIsEnableSpec())) {

			//先获取SKU的列表
			List<TbItem> itemList = goods.getItemList();

			for (TbItem tbItem : itemList) {

				//设置title  SPU名 + 空格+ 规格名称 +
				String spec = tbItem.getSpec();//{"网络":"移动4G","机身内存":"16G"}
				String title = tbGoods.getGoodsName();
				Map map = JSON.parseObject(spec, Map.class);
				for (Object key : map.keySet()) {
					String o1 = (String) map.get(key);
					title += " " + o1;
				}
				tbItem.setTitle(title);

				//设置图片从goodsDesc中获取
				//[{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/03/wKgZhVq7N-qAEDgSAAJfMemqtP8461.jpg"}]
				String itemImages = goodsDesc.getItemImages();//

				List<Map> maps = JSON.parseArray(itemImages, Map.class);
				if (maps!=null&&maps.size()>0) {
					String url = maps.get(0).get("url").toString();//图片的地址
					tbItem.setImage(url);
				}


				//设置分类
				TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
				tbItem.setCategoryid(tbItemCat.getId());
				tbItem.setCategory(tbItemCat.getName());

				//时间
				tbItem.setCreateTime(new Date());
				tbItem.setUpdateTime(new Date());

				//设置SPU的ID
				tbItem.setGoodsId(tbGoods.getId());

				//设置商家
				TbSeller tbSeller = tbSellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
				tbItem.setSellerId(tbSeller.getSellerId());
				tbItem.setSeller(tbSeller.getNickName());//店铺名

				//设置品牌明后
				TbBrand tbBrand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
				tbItem.setBrand(tbBrand.getName());
				itemMapper.insert(tbItem);
			}
		}else{
			//插入到SKU表 一条记录
			TbItem tbItem = new TbItem();
			tbItem.setTitle(tbGoods.getGoodsName());
			tbItem.setPrice(tbGoods.getPrice());
			tbItem.setNum(999);//默认一个
			tbItem.setStatus("1");//正常启用
			tbItem.setIsDefault("1");//默认的

			tbItem.setSpec("{}");


			//设置图片从goodsDesc中获取
			//[{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/03/wKgZhVq7N-qAEDgSAAJfMemqtP8461.jpg"}]
			String itemImages = goodsDesc.getItemImages();//

			List<Map> maps = JSON.parseArray(itemImages, Map.class);
			if (maps!=null&&maps.size()>0) {
				String url = maps.get(0).get("url").toString();//图片的地址
				tbItem.setImage(url);
			}

			//设置分类
			TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
			tbItem.setCategoryid(tbItemCat.getId());
			tbItem.setCategory(tbItemCat.getName());

			//时间
			tbItem.setCreateTime(new Date());
			tbItem.setUpdateTime(new Date());

			//设置SPU的ID
			tbItem.setGoodsId(tbGoods.getId());

			//设置商家
			TbSeller tbSeller = tbSellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
			tbItem.setSellerId(tbSeller.getSellerId());
			tbItem.setSeller(tbSeller.getNickName());//店铺名

			//设置品牌明后
			TbBrand tbBrand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
			tbItem.setBrand(tbBrand.getName());
			itemMapper.insert(tbItem);
		}
	}

	@Override
	public Goods findOne(Long id) {
		Goods goods = new Goods();
		//查找tbGoods
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		//查找tbGoodsDesc
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		//查找tbItem
		Example example = new Example(TbItem.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("goodsId",id);
		List<TbItem> tbItems = itemMapper.selectByExample(example);

		goods.setGoods(tbGoods);
		goods.setGoodsDesc(tbGoodsDesc);
		goods.setItemList(tbItems);

		return goods;
	}

	@Override
	public void update(Goods goods) {
		//1.获取SPU的数据
		TbGoods tbGoods = goods.getGoods();
		tbGoods.setAuditStatus("0");//默认就是未审核的状态
		tbGoods.setIsDelete(false);//不删除的状态
		//2.获取SPU对应的描述的数据
		TbGoodsDesc goodsDesc = goods.getGoodsDesc();

		//3.清空goodId对应的 item
		TbItem tbItem = new TbItem();
		tbItem.setGoodsId(goodsDesc.getGoodsId());
		itemMapper.delete(tbItem);

		//4.插入到三个表中
		goodsMapper.updateByPrimaryKeySelective(tbGoods);
		goodsDescMapper.updateByPrimaryKeySelective(goodsDesc);
		saveItems(goods,tbGoods,goodsDesc);
	}

	@Override
	public void updateStatus(String status, Long[] ids) {
		//设置goods状态
		TbGoods tbGoods = new TbGoods();
		tbGoods.setAuditStatus(status);
		Example exampleGoods = new Example(TbGoods.class);
		exampleGoods.createCriteria().andIn("id",Arrays.asList(ids));
		//设置tbItem状态
		TbItem tbItem = new TbItem();
		tbItem.setStatus(status);
		Example exampleItem = new Example(TbItem.class);
		exampleItem.createCriteria().andIn("goodsId",Arrays.asList(ids));
		//更新数据
		goodsMapper.updateByExampleSelective(tbGoods,exampleGoods);
		itemMapper.updateByExampleSelective(tbItem,exampleItem);
	}

	@Override
	public void delete(Object[] ids) {
		Long[] idsL = new Long[ids.length];
		for (int i = 0; i < ids.length; i++) {
			idsL[i] = (Long) ids[i];
		}
		Example example = new Example(TbGoods.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id",Arrays.asList(idsL));
		TbGoods tbGoods = new TbGoods();
		tbGoods.setIsDelete(true);
		goodsMapper.updateByExampleSelective(tbGoods,example);
	}

	/***
	 * goodIds 查找 tbItem
	 * @param ids
	 * @return
	 */
	@Override
	public List<TbItem> findTbItemListByIds(Long[] ids) {
		//构建查询条件
		Example example = new Example(TbItem.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("goodsId",Arrays.asList(ids)).andEqualTo("status","1");
		//tbItemMapper 查询
		return itemMapper.selectByExample(example);
	}
}
