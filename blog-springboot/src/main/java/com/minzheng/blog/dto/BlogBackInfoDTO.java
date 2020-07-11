package com.minzheng.blog.dto;

import lombok.Data;

import java.util.List;

/**
 * 博客后台信息
 *
 * @author 11921
 */
@Data
public class BlogBackInfoDTO {
    /**
     * 访问量
     */
    private Integer viewsCount;

    /**
     * 留言量
     */
    private Integer messageCount;

    /**
     * 用户量
     */
    private Integer userCount;

    /**
     * 文章量
     */
    private Integer articleCount;

    /**
     * 分类统计
     */
    private List<CategoryDTO> categoryDTOList;

    /**
     * 一周用户量集合
     */
    private List<Integer> uniqueViewDTOList;

    /**
     * 文章浏览量排行
     */
    private List<ArticleRankDTO>articleRankDTOList;

    public BlogBackInfoDTO(Integer viewsCount, Integer messageCount, Integer userCount, Integer articleCount, List<CategoryDTO> categoryDTOList, List<Integer> uniqueViewDTOList, List<ArticleRankDTO> articleRankDTOList) {
        this.viewsCount = viewsCount;
        this.messageCount = messageCount;
        this.userCount = userCount;
        this.articleCount = articleCount;
        this.categoryDTOList = categoryDTOList;
        this.uniqueViewDTOList = uniqueViewDTOList;
        this.articleRankDTOList = articleRankDTOList;
    }

    public BlogBackInfoDTO(Integer viewsCount, Integer messageCount, Integer userCount, Integer articleCount, List<CategoryDTO> categoryDTOList, List<Integer> uniqueViewDTOList) {
        this.viewsCount = viewsCount;
        this.messageCount = messageCount;
        this.userCount = userCount;
        this.articleCount = articleCount;
        this.categoryDTOList = categoryDTOList;
        this.uniqueViewDTOList = uniqueViewDTOList;
    }

    public BlogBackInfoDTO() {
    }

}
