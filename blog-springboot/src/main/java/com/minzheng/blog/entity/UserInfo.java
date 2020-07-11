package com.minzheng.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.minzheng.blog.utils.UserUtil;
import com.minzheng.blog.vo.UserInfoVO;
import com.minzheng.blog.vo.UserRoleVO;
import lombok.Data;

/**
 * 用户信息
 *
 * @author xiaojie
 * @since 2020-05-18
 */
@Data
@TableName("tb_user_info")
public class UserInfo {

    /**
     * 用户ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户角色
     */
    private String userRole;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户简介
     */
    private String intro;

    /**
     * 个人网站
     */
    private String webSite;

    /**
     * 是否禁言
     */
    private Integer isSilence;

    /**
     * 创建时间
     */
    private Date createTime;


    public UserInfo() {
        this.nickname = "用户" + System.currentTimeMillis();
        this.createTime = new Date();
    }

    public UserInfo(String nickname, String avatar) {
        this.nickname = nickname;
        this.avatar = avatar;
        this.createTime = new Date();
    }

    public UserInfo(UserInfoVO userInfoVO) {
        this.id = UserUtil.getLoginUser().getUserInfoId();
        this.nickname = userInfoVO.getNickname();
        this.intro = userInfoVO.getIntro();
        this.webSite = userInfoVO.getWebSite();
    }

    public UserInfo(String avatar) {
        this.id = UserUtil.getLoginUser().getUserInfoId();
        this.avatar = avatar;
    }

    public UserInfo(UserRoleVO userRoleVO) {
        this.id = userRoleVO.getUserInfoId();
        this.nickname = userRoleVO.getNickname();
        this.userRole = userRoleVO.getUserRole();
    }

    public UserInfo(Integer id, Integer isSilence) {
        this.id = id;
        this.isSilence = isSilence;
    }
}
