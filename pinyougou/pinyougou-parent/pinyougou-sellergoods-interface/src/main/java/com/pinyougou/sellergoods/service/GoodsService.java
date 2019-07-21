package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.Goods;
import com.pinyougou.pojo.TbGoods;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbItem;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService extends CoreService<TbGoods> {

	public List<TbItem> findTbItemListByIds(Long[] ids);

	public void updateStatus(Long[] ids,String status);

	public void update(Goods goods);

	public Goods findOne(Long id);

	public void add(Goods goods);
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbGoods> findPage(Integer pageNo,Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbGoods> findPage(Integer pageNo,Integer pageSize,TbGoods Goods);
	
}
