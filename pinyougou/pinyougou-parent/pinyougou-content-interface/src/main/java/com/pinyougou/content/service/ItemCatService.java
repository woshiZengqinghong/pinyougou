package com.pinyougou.content.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbItemCat;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface ItemCatService extends CoreService<TbItemCat> {
	
	public List<TbItemCat> findByParentId(Long parentId);
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbItemCat> findPage(Integer pageNo, Integer pageSize);



	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbItemCat> findPage(Integer pageNo, Integer pageSize, TbItemCat ItemCat);
	
}
