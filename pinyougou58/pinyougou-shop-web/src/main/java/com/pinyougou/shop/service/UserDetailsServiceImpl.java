package com.pinyougou.shop.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.shop.service *
 * @since 1.0
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {



    @Reference
    private SellerService sellerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //1 从数据库中查询用户的信息
        TbSeller tbSeller = sellerService.findOne(username);

        //2. 判断用户的信息 的逻辑
        if(tbSeller==null){
            return null;
        }

        if(!"1".equals(tbSeller.getStatus())){
            return null;
        }


        //3.返回数据给springsecurity的框架 自动的 进行匹配
        /*List<GrantedAuthority> list  = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("ROLE_SELLER"));*/

        System.out.println("sellerService>>>>>经过了自定义认证类"+username);


//        return new User(username,"{noop}"+tbSeller.getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_SELLER"));
        return new User(username,tbSeller.getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_SELLER"));
    }
}
