package com.minzheng.blog.handler;

import com.alibaba.fastjson.JSON;
import com.minzheng.blog.dao.UserAuthDao;
import com.minzheng.blog.dto.UserInfoDTO;
import com.minzheng.blog.entity.UserAuth;
import com.minzheng.blog.utils.IpUtil;
import com.minzheng.blog.utils.UserUtil;
import com.minzheng.blog.vo.Result;
import com.minzheng.blog.constant.StatusConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 登录成功处理
 *
 * @author 11921
 */
@Component
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {
    @Autowired
    private UserAuthDao userAuthDao;
    @Autowired
    private HttpServletRequest request;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(new Result<UserInfoDTO>(true, StatusConst.OK, "登录成功！", UserUtil.getLoginUser())));
        //更新用户登录ip地址，最新登录时间
        String ipAddr = IpUtil.getIpAddr(request);
        String ipSource = IpUtil.getIpSource(ipAddr);
        userAuthDao.updateById(new UserAuth(ipAddr, ipSource));
    }

}
