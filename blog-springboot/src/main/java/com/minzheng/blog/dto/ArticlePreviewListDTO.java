package com.minzheng.blog.dto;

import lombok.Data;

import java.util.List;

/**
 * 分类或标签下的文章列表
 * @author 11921
 */
@Data
public class ArticlePreviewListDTO {
    /**
     * 条件对应的文章列表
     */
    private List<ArticlePreviewDTO> articlePreviewDTOList;

    /**
     * 条件名
     */
    private String name;

    public ArticlePreviewListDTO(List<ArticlePreviewDTO> articlePreviewDTOList, String name) {
        this.articlePreviewDTOList = articlePreviewDTOList;
        this.name = name;
    }

    public ArticlePreviewListDTO() {
    }

}
