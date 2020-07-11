package com.minzheng.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.minzheng.blog.dao.UserAuthDao;
import com.minzheng.blog.dao.UserInfoDao;
import com.minzheng.blog.dto.UserInfoDTO;
import com.minzheng.blog.entity.UserAuth;
import com.minzheng.blog.entity.UserInfo;
import com.minzheng.blog.exception.ServeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Set;


/**
 * @author 11921
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserAuthDao userAuthDao;
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            throw new ServeException("用户名不能为空！");
        }
        //查询账号是否存在
        QueryWrapper<UserAuth> userAuthQueryWrapper = new QueryWrapper<>();
        userAuthQueryWrapper.select("id", "user_info_id", "password").eq("username", username);
        UserAuth user = userAuthDao.selectOne(userAuthQueryWrapper);
        if (user == null) {
            throw new ServeException("用户名不存在");
        }
        //查询账号对应的信息
        QueryWrapper<UserInfo> userInfoWrapper = new QueryWrapper<>();
        userInfoWrapper.select("id", "user_role", "nickname", "avatar", "intro", "web_site", "is_silence").eq("id", user.getUserInfoId());
        UserInfo userInfo = userInfoDao.selectOne(userInfoWrapper);
        //查询账号点赞信息
        Set articleLikeSet = (Set<Integer>) redisTemplate.boundHashOps("article_user_like").get(userInfo.getId().toString());
        Set commentLikeSet = (Set) redisTemplate.boundHashOps("comment_user_like").get(userInfo.getId().toString());
        //封装信息
        return User.withUsername(JSON.toJSONString(new UserInfoDTO(user.getId(), userInfo, articleLikeSet, commentLikeSet))).password(user.getPassword()).roles(userInfo.getUserRole()).build();
    }

}
