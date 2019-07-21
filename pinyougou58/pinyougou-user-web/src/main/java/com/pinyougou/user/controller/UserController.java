package com.pinyougou.user.controller;

import java.util.Date;
import java.util.List;

import com.pinyougou.common.util.PhoneFormatCheckUtils;
import com.pinyougou.user.service.UserService;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;

import com.github.pagehelper.PageInfo;
import entity.Result;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbUser> findAll() {
        return userService.findAll();
    }


    @RequestMapping("/findPage")
    public PageInfo<TbUser> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                     @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return userService.findPage(pageNo, pageSize);
    }

    /***
     * 校验手机号码  生成验证码
     * @param phone
     */
    @RequestMapping("/sendCode")
    public Result sendCode(String phone) {
        if (!PhoneFormatCheckUtils.isPhoneLegal(phone)) {
            //手机号码格式不正确
            return new Result(false, "手机号格式不正确");
        }
        //生成验证码 发送验证码
        try {
            userService.createSmsCode(phone);
            return new Result(true, "验证码发送成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "验证码发送失败");

    }

    /**
     * 增加
     *
     * @param user
     * @return
     */
    @RequestMapping("/add/{smsCode}")
    public Result add(@PathVariable("smsCode") String smsCode, @RequestBody TbUser user) {
        try {
            //判断验证码是否正确
            boolean flag = userService.checkSmsCode(user.getPhone(), smsCode);
            if (!flag) {
                return new Result(false, "验证码错误");
            }

            //密码加密
            String password = user.getPassword();
            String pwd = DigestUtils.md5DigestAsHex(password.getBytes());
            user.setPassword(pwd);
            //注册与修改时间
            user.setCreated(new Date());
            user.setUpdated(user.getCreated());
            userService.add(user);
            return new Result(true, "增加成功");

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param user
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbUser user) {
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
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne/{id}")
    public TbUser findOne(@PathVariable(value = "id") Long id) {
        return userService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
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
