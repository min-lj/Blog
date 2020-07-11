package com.minzheng.blog.utils;

import com.alibaba.fastjson.JSON;
import com.minzheng.blog.dto.UserInfoDTO;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 用户工具类
 * @author 11921
 */
public class UserUtil {
    /**
     * 获取当前登录用户
     *
     * @return
     */
    public static UserInfoDTO getLoginUser() {
        return JSON.parseObject(SecurityContextHolder.getContext().getAuthentication().getName(), UserInfoDTO.class);
    }

}
