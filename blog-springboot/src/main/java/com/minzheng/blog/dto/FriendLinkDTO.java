package com.minzheng.blog.dto;

import lombok.Data;

/**
 * 友链列表
 * @author 11921
 */
@Data
public class FriendLinkDTO {
    /**
     * id
     */
    private Integer id;

    /**
     * 链接名
     */
    private String linkName;

    /**
     * 链接头像
     */
    private String linkAvatar;

    /**
     * 链接地址
     */
    private String linkAddress;

    /**
     * 介绍
     */
    private String linkIntro;
}
