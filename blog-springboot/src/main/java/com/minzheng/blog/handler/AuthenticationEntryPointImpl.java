package com.minzheng.blog.handler;

import com.alibaba.fastjson.JSON;
import com.minzheng.blog.enums.StatusCodeEnum;
import com.minzheng.blog.vo.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.minzheng.blog.constant.CommonConst.APPLICATION_JSON;

/**
 * 用户未登录处理
 *
 * @author yezhiqiu
 * @date 2021/07/28
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        httpServletResponse.setContentType(APPLICATION_JSON);
        httpServletResponse.getWriter().write(JSON.toJSONString(Result.fail(StatusCodeEnum.NO_LOGIN)));
    }

}
