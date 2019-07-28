package com.pinyougou.user.service;
import com.pinyougou.pojo.TbUser;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import entity.UserOrderList;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface UserService extends CoreService<TbUser> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize);



	/***
	 * 生成验证码 发送验证码
	 * @param phone
	 */
    void createSmsCode(String phone);

	/**
	 * 判断短信验证码是否存在
	 * @param phone
	 * @return
	 */
	boolean  checkSmsCode(String phone,String code);

}
