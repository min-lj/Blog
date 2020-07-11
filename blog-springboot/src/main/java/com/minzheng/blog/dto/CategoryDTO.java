package com.minzheng.blog.dto;

import lombok.Data;

/**
 * 分类列表
 * @author 11921
 */
@Data
public class CategoryDTO {

    /**
     * id
     */
    private Integer id;

    /**
     * 分类名
     */
    private String categoryName;

    /**
     * 分类下的文章数量
     */
    private Long articleCount;

}
