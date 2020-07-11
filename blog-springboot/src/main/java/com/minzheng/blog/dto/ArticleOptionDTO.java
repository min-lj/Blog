package com.minzheng.blog.dto;

import lombok.Data;

import java.util.List;

/**
 * 文章选项
 *
 * @author 11921
 */
@Data
public class ArticleOptionDTO {
    /**
     * 文章标签列表
     */
    private List<TagDTO> tagDTOList;

    /**
     * 文章分类列表
     */
    private List<CategoryBackDTO> categoryDTOList;

    public ArticleOptionDTO(List<CategoryBackDTO> categoryDTOList, List<TagDTO> tagDTOList) {
        this.categoryDTOList = categoryDTOList;
        this.tagDTOList = tagDTOList;
    }

    public ArticleOptionDTO() {
    }

}
