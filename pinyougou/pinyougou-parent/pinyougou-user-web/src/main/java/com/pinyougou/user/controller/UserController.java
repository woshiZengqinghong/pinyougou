package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import com.pinyougou.utils.PhoneFormatCheckUtils;
import entity.Error;
import entity.Result;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.regex.PatternSyntaxException;

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



	@RequestMapping("/sendCode")
	public Result sendCode(String phone){
		//判断手机号格式
		try {
			if (!PhoneFormatCheckUtils.isPhoneLegal(phone)) {
				return new Result(false,"格式不正确");
			}
			userService.createSmsCode(phone);
			return new Result(true,"验证码发送成功");
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
			return new Result(true, "验证码发送失败");
		}
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
	@RequestMapping("/add/{smsCode}")
	public Result add(@Valid  @RequestBody TbUser user, BindingResult bindingResult, @PathVariable("smsCode")String smsCode){
		try {
			//先检验
			if (bindingResult.hasErrors()) {
				Result result = new Result(false, "失败");
				List<FieldError> fieldErrors = bindingResult.getFieldErrors();
				for (FieldError fieldError : fieldErrors) {
					Error error = new Error(fieldError.getField(),fieldError.getDefaultMessage());
					result.getErrorsList().add(error);

				}
				return result;
			}
			//如果没有错误
			if (userService.checkSmsCode(user.getPhone(),smsCode)==false) {
				Result result = new Result(false, "验证码输入错误");
				result.getErrorsList().add(new Error("smsCode","验证码输入错误"));
				return result;
			}
			user.setCreated(new Date());
			user.setUpdated(new Date());

			String password = DigestUtils.md5Hex(user.getPassword());//对密码加密
			user.setPassword(password);

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
