package com.minzheng.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.minzheng.blog.utils.UserUtil;
import com.minzheng.blog.vo.CommentVO;
import lombok.Data;

/**
 * 评论
 *
 * @author xiaojie
 * @since 2020-05-18
 */
@Data
@TableName("tb_comment")
public class Comment {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 评论用户Id
     */
    private Integer userId;

    /**
     * 回复用户id
     */
    private Integer replyId;

    /**
     * 评论文章id
     */
    private Integer articleId;

    /**
     * 评论内容
     */
    private String commentContent;

    /**
     * 评论时间
     */
    private Date createTime;

    /**
     * 父评论id
     */
    private Integer parentId;

    /**
     * 状态码
     */
    private Integer isDelete;

    public Comment(CommentVO commentVO) {
        this.userId = UserUtil.getLoginUser().getUserInfoId();
        this.replyId = commentVO.getReplyId();
        this.articleId = commentVO.getArticleId();
        this.commentContent = commentVO.getCommentContent();
        this.parentId = commentVO.getParentId();
        this.createTime = new Date();
    }

    public Comment(Integer commentId, Integer isDelete) {
        this.id = commentId;
        this.isDelete = isDelete;
    }

}
