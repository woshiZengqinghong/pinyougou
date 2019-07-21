package com.pinyougou.shop.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.shop.security.config *
 * @since 1.0
 */
@Component
@EnableWebSecurity//开启自动配置
public class ShopSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //认证
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //从数据库中查询相关的用户信息 进行认证
        //auth.inMemoryAuthentication().withUser("seller").password("{noop}123456").roles("SELLER");
        //使用加密器 进行加密处理
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    //授权
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //1.设置拦截的请求   （排除 登录的页面  css js ）

        http.authorizeRequests()
                .antMatchers("/css/**","/img/**","/js/**","/plugins/**","/*.html","/seller/add.shtml").permitAll()
                .antMatchers("/**").hasRole("SELLER");
//                .anyRequest().authenticated();

        //2.设置 自定义登录的页面
        http.formLogin()
                .loginPage("/shoplogin.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/index.html",true)
                .failureUrl("/shoplogin.html?error");


        //3.禁用csrf
        http.csrf().disable();

        //4.设置iframe 的同源可以访问的策略

        http.headers().frameOptions().sameOrigin();


    }


}
