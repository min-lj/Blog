package com.minzheng.blog.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.minzheng.blog.vo.MessageVO;
import lombok.Data;

/**
 * 留言
 *
 * @author xiaojie
 * @since 2020-05-18
 */
@Data
@TableName("tb_message")
public class Message {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ip
     */
    private String ipAddress;

    /**
     * 用户地址
     */
    private String ipSource;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 留言内容
     */
    private String messageContent;

    /**
     * 弹幕速度
     */
    private Integer time;

    /**
     * 留言时间
     */
    private Date createTime;


    public Message(MessageVO messageVO, String ipAddress, String ipSource) {
        this.ipAddress = ipAddress;
        this.ipSource = ipSource;
        this.nickname = messageVO.getNickname();
        this.avatar = messageVO.getAvatar();
        this.messageContent = messageVO.getMessageContent();
        this.time = messageVO.getTime();
        this.createTime = new Date();
    }

    public Message() {
    }

}
