package com.minzheng.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 文章点赞记录
 *
 * @author xiaojie
 * @since 2020-05-18
 */
@Data
@TableName("tb_article_like")
public class ArticleLike {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 点赞用户
     */
    private Integer userId;

    /**
     * 点赞文章
     */
    private Integer articleId;


}
