package com.pinyougou.content.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbProvinces;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface ProvincesService extends CoreService<TbProvinces> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbProvinces> findPage(Integer pageNo, Integer pageSize);



	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbProvinces> findPage(Integer pageNo, Integer pageSize, TbProvinces Provinces);
	
}
