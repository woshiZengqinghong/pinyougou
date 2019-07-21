package com.pinyougou.sellergoods.service;
import com.pinyougou.pojo.TbGoods;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbItem;
import entity.Goods;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService extends CoreService<TbGoods> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize, TbGoods Goods);

	/**
	 * 接收组合对象 获取SPU  和描述  和 SKU列表
	 * @param goods
	 */
	void add(Goods goods);

	Goods findOne(Long id);

	void update(Goods goods);

	void updateStatus(String status, Long[] ids);

	void delete(Object[] ids);

    List<TbItem> findTbItemListByIds(Long[] ids);
}
