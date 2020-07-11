package com.minzheng.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.minzheng.blog.vo.FriendLinkVO;
import lombok.Data;

/**
 * 友链列表
 *
 * @author xiaojie
 * @since 2020-05-18
 */
@Data
@TableName("tb_friend_link")
public class FriendLink {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
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

    /**
     * 创建时间
     */
    private Date createTime;


    public FriendLink(FriendLinkVO friendLinkVO) {
        this.id = friendLinkVO.getId();
        this.linkName = friendLinkVO.getLinkName();
        this.linkAvatar = friendLinkVO.getLinkAvatar();
        this.linkAddress = friendLinkVO.getLinkAddress();
        this.linkIntro = friendLinkVO.getLinkIntro();
        this.createTime = this.id == null ? new Date() : null;
    }

    public FriendLink(Integer friendLinkId, Integer isDelete) {
        this.id = friendLinkId;
    }

    public FriendLink() {
    }

}
