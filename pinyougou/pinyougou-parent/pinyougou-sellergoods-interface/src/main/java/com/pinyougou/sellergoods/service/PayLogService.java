package com.pinyougou.sellergoods.service;
import com.pinyougou.pojo.TbPayLog;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface PayLogService extends CoreService<TbPayLog> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbPayLog> findPage(Integer pageNo,Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbPayLog> findPage(Integer pageNo,Integer pageSize,TbPayLog PayLog);
	
}
