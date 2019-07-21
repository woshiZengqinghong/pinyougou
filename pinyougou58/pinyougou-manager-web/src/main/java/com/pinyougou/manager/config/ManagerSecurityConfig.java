package com.pinyougou.manager.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.manager.config *
 * @since 1.0
 */
@EnableWebSecurity
public class ManagerSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("admin").password("{noop}admin").roles("ADMIN");

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //拦截
        http.authorizeRequests()
                .antMatchers("/css/**","/img/**","/js/**","/plugins/**","/login.html").permitAll()
               // .antMatchers("/**").hasRole("ADMIN")
                .anyRequest().authenticated();


        //自定义页面

        http.formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/index.html",true)
                .failureUrl("/login.html?error");




        //禁用CSRF
        http.csrf().disable();

        //设置 iframe 同源 可以访问 的策略。

        http.headers().frameOptions().sameOrigin();


        //退出
        http.logout().logoutUrl("/logout").invalidateHttpSession(true);


        //先使用默认的登录页面
        //super.configure(http);
    }
}
