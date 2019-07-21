package com.pinyougou.content.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbItem;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface ItemService extends CoreService<TbItem> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbItem> findPage(Integer pageNo, Integer pageSize);



	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbItem> findPage(Integer pageNo, Integer pageSize, TbItem Item);
	
}
