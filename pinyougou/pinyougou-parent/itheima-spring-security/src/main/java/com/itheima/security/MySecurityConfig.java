package com.itheima.security;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

public class MySecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //定义了一个用户名 admin 密码admin  并且拥有ADMIN角色的用户
        auth.inMemoryAuthentication().withUser("admin").password("admin");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //授权 登陆和错误页面 不需要登陆
        // 其他的请求 /admin/** 都需要拥有ADMIN角色的人 才可以访问
        // 其他的请求 /user/** 都需要拥有USER角色的人，才可以访问
        //其他的人已请求 都只要登陆就可以访问了
        http.authorizeRequests()
                .antMatchers("login.html","/error.html").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasRole("USER")
                .anyRequest().authenticated();

        //设置使用表单登陆
        http.formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("index.html",true)
                .failureUrl("/error.html");

        //禁用Csrf 禁用跨站请求伪造
        http.csrf().disable();
    }
}
