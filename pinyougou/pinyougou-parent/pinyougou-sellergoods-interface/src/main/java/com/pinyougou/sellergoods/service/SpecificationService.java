package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.Specification;
import com.pinyougou.pojo.TbSpecification;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SpecificationService extends CoreService<TbSpecification> {
	
	
	void add(Specification specification);

	public Specification findOne(Long id);

	public Specification update(Specification specification);

	public void delete(Long[] ids);
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbSpecification> findPage(Integer pageNo,Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbSpecification> findPage(Integer pageNo,Integer pageSize,TbSpecification Specification);
	
}
