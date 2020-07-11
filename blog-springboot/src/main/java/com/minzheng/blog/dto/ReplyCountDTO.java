package com.minzheng.blog.dto;

import lombok.Data;

/**
 * 回复数量
 * @author 11921
 */
@Data
public class ReplyCountDTO {

    /**
     * 评论id
     */
    private Integer commentId;

    /**
     * 回复数量
     */
    private Integer replyCount;

}
