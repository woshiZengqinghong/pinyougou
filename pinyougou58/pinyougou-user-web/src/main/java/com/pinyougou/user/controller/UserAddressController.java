package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.PhoneFormatCheckUtils;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.AddressService;
import com.pinyougou.user.service.UserService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/address")
public class UserAddressController {

    @Reference
    private AddressService addressService;


    @RequestMapping("/findAddressListByUserId")
    public List<TbAddress> findAddressListByUserId(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbAddress address = new TbAddress();
        address.setUserId(userId);
        List<TbAddress> addressList = addressService.select(address);
        return addressList;
    }

    @RequestMapping("/setDefault/{addressId}")
    public Result setDefault(@PathVariable("addressId") Long addressId) {
        try {
            TbAddress old = new TbAddress();
            old.setUserId(SecurityContextHolder.getContext().getAuthentication().getName());
            old.setIsDefault("1");
            TbAddress tbAddressOld = addressService.selectOne(old);
            tbAddressOld.setIsDefault("0");
            addressService.update(tbAddressOld);

            TbAddress tbAddress = addressService.selectByPrimaryKey(addressId);
            tbAddress.setIsDefault("1");
            addressService.update(tbAddress);
            return new Result(true, "更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "更新失败");
        }
    }
    /**
     * 获取实体
     *
     * @return
     */
    @RequestMapping("/findOne/{addressId}")
    public TbAddress findOne(@PathVariable("addressId") Long addressId) {
        TbAddress tbAddress = new TbAddress();
        tbAddress.setUserId(SecurityContextHolder.getContext().getAuthentication().getName());
        tbAddress.setId(addressId);
        TbAddress address = addressService.findOne(tbAddress);
        return address;
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        try {
            addressService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }
    /**
     * 增加
     * @param address
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbAddress address) {
        address.setUserId(SecurityContextHolder.getContext().getAuthentication().getName());
        addressService.add(address);
        return new Result(true, "增加成功");
    }

    /**
     * 修改
     * @param address
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbAddress address) {
        try {
            addressService.update(address);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbAddress> findAll() {
        return addressService.findAll();
    }
}
