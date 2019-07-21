package com.pinyougou.seckill.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbSeckillGoods;

import java.util.Map;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillGoodsService extends CoreService<TbSeckillGoods> {

	public Map getGoodsById(Long id);
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbSeckillGoods> findPage(Integer pageNo, Integer pageSize);



	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbSeckillGoods> findPage(Integer pageNo, Integer pageSize, TbSeckillGoods SeckillGoods);
	
}
