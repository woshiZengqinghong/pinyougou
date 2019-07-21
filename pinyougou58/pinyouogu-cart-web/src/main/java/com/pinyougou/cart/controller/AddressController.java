package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.user.service.AddressService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

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
}
