package com.minzheng.blog.config;

import com.minzheng.blog.handler.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security配置类
 *
 * @author 11921
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private AuthenticationEntryPointImpl authenticationEntryPoint;
    @Autowired
    private AccessDeniedHandlerImpl accessDeniedHandler;
    @Autowired
    private AuthenticationSuccessHandlerImpl authenticationSuccessHandler;
    @Autowired
    private AuthenticationFailHandlerImpl authenticationFailHandler;
    @Autowired
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    /**
     * 密码加密
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置权限
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin().loginProcessingUrl("/login").successHandler(authenticationSuccessHandler).failureHandler(authenticationFailHandler).and()
                .logout().logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler).and()
                .authorizeRequests()
                //开放测试账号权限
                .antMatchers(HttpMethod.GET,"/admin/**").hasAnyRole("test","admin")
                //管理员页面需要权限
                .antMatchers("/admin/**").hasRole("admin")
                .antMatchers(HttpMethod.GET,"/comments").permitAll()
                //用户操作，发表评论需要登录
                .antMatchers("/users/info","/users/avatar","/comments").authenticated()
                .anyRequest().permitAll()
                .and()
                //关闭跨站请求防护
                .csrf().disable().exceptionHandling()
                //未登录处理
                .authenticationEntryPoint(authenticationEntryPoint)
                //权限不足处理
                .accessDeniedHandler(accessDeniedHandler).and()
                //开启嵌入
                .headers().frameOptions().disable();
    }

}
