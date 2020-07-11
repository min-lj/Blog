package com.minzheng.blog.dto;

import lombok.Data;

/**
 * 博客首页信息
 *
 * @author 11921
 */
@Data
public class BlogHomeInfoDTO {
    /**
     * 博主昵称
     */
    private String nickname;

    /**
     * 博主头像
     */
    private String avatar;

    /**
     * 博主简介
     */
    private String intro;

    /**
     * 文章数量
     */
    private Integer articleCount;

    /**
     * 分类数量
     */
    private Integer categoryCount;

    /**
     * 标签数量
     */
    private Integer tagCount;

    /**
     * 公告
     */
    private String notice;

    /**
     * 访问量
     */
    private String viewsCount;

    public BlogHomeInfoDTO(String nickname, String avatar, String intro, Integer articleCount, Integer categoryCount, Integer tagCount, String notice, String viewsCount) {
        this.nickname = nickname;
        this.avatar = avatar;
        this.intro = intro;
        this.articleCount = articleCount;
        this.categoryCount = categoryCount;
        this.tagCount = tagCount;
        this.notice = notice;
        this.viewsCount = viewsCount;
    }

    public BlogHomeInfoDTO() {
    }

}
