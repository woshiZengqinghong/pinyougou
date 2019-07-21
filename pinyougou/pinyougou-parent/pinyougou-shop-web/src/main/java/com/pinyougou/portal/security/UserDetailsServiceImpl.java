package com.pinyougou.portal.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public class UserDetailsServiceImpl implements UserDetailsService {

    @Reference
    private SellerService sellerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        TbSeller tbSeller = sellerService.findOne(username);

        if (tbSeller==null) {
            return null;
        }

        String status = tbSeller.getStatus();

//        if (!"1".equals(status)) {
//            //未审核通过
//            return null;
//        }
        System.out.println(tbSeller);
        return new User(username,tbSeller.getPassword(),AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_SELLER"));
//        return new User(username,"{noop}123456", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_SELLER"));
    }
}
