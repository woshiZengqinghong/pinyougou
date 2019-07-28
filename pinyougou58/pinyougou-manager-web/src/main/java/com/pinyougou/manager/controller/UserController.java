package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.sellergoods.service.OrderService;
import com.pinyougou.sellergoods.service.UserService;
import entity.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;

	@Reference
	private OrderService orderService;

	@RequestMapping("/paymentGroupBySellerId")
	public Result paymentGroupBySellerId(){
		Result result = null;
		try {
			Map<String, Object> map = orderService.paymentGroupBySellerId();
			result = new Result(true, JSON.toJSONString(map));
		} catch (Exception e) {
			e.printStackTrace();
			result = new Result(false,"查询失败");
		}

		return result;
	}

	@RequestMapping("/exportUserData")
	public Result exportUserData(){
		Result result = null;
		try {
			userService.exportUserData();
			result = new Result(true,"导出成功");
		} catch (Exception e) {
			e.printStackTrace();
			result = new Result(false,"导出失败");
		}

		return result;
	}

	/**
	 * 统计总用户
	 */
	@RequestMapping("/countTotalUsers")
	public Result countTotalUsers(){
		Result result = null;
		try {
			Integer totalCount = userService.countTotalUsers();
			result = new Result(true,totalCount+"");
		} catch (Exception e) {
			e.printStackTrace();
			result = new Result(false,"0");
		}

		return result;
	}

	/**
	 *Frozen account
	 * 冻结账户
	 */
	@RequestMapping("/frozenAccount")
	public Result frozenAccount(){
		Result result = null;
		try {
			userService.frozenAccount();
			result = new Result(true,"冻结成功");
		} catch (Exception e) {
			e.printStackTrace();
			result = new Result(false,"冻结失败");
		}

		return  result;
	}

	/**
	 * 更新用户状态
	 */
	@RequestMapping("/updateUserStatus/{status}")
	public Result updateUserStatus(@PathVariable("status") String status, @RequestBody Long[] ids){
		Result result = null;
		try {
			userService.updateUserStatus(status,ids);
			result= new Result(true,"更新用户状态成功");
		} catch (Exception e) {
			e.printStackTrace();
			result = new Result(false,"更新用户状态失败");
		}

		return result;
	}

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){
		return userService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbUser> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                     @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return userService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbUser user){
		try {
			userService.add(user);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser user){
		try {
			userService.update(user);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne/{id}")
	public TbUser findOne(@PathVariable(value = "id") Long id){
		return userService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			userService.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public PageInfo<TbUser> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                     @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                     @RequestBody TbUser user) {
        return userService.findPage(pageNo, pageSize, user);
    }
	
}
