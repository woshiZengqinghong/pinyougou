package com.pinyougou.manager.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.manager.controller *
 * @since 1.0
 */
@RestController
public class LoginUserInfoController {

    @RequestMapping("/login/user/info")
    public String getUserInfo(){
        return "zhangsanfeng";
//        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
