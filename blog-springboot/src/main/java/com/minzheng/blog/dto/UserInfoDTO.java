package com.minzheng.blog.dto;

import com.minzheng.blog.entity.UserInfo;
import lombok.Data;

import java.util.Set;

/**
 * 用户登录信息
 *
 * @author 11921
 */
@Data
public class UserInfoDTO {

    /**
     * 用户账号id
     */
    private Integer id;

    /**
     * 用户信息id
     */
    private Integer userInfoId;

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
     * 用户禁言状态
     */
    private Integer isSilence;

    /**
     * 点赞文章集合
     */
    private Set articleLikeSet;

    /**
     * 点赞评论集合
     */
    private Set commentLikeSet;

    public UserInfoDTO() {
    }

    public UserInfoDTO(Integer id, UserInfo userInfo, Set articleLikeSet, Set commentLikeSet) {
        this.id = id;
        this.userInfoId = userInfo.getId();
        this.userRole = userInfo.getUserRole();
        this.nickname = userInfo.getNickname();
        this.avatar = userInfo.getAvatar();
        this.isSilence = userInfo.getIsSilence();
        this.intro = userInfo.getIntro();
        this.webSite = userInfo.getWebSite();
        this.articleLikeSet = articleLikeSet;
        this.commentLikeSet = commentLikeSet;
    }

}
