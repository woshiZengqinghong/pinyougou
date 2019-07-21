package com.pinyougou.seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/page")
public class PageController {

    @RequestMapping("/login")
    public String showPage(String url){
        return "redirect:" + url;
    }
}
