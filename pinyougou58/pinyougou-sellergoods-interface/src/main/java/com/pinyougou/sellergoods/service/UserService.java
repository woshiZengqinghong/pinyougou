package com.pinyougou.sellergoods.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbUser;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface UserService extends CoreService<TbUser> {

	/**
	 * 导出用户数据到excel
	 */
	public void exportUserData();

	/**
	 *统计用户
	 */
	public Integer countTotalUsers();


	/**
	 * 冻结账户
	 */
	public void frozenAccount();

	/**
	 * 冻结用户
	 */
	public void updateUserStatus(String status, Long[] ids);
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize, TbUser User);
	
}
