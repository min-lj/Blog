package com.minzheng.blog.dto;

import lombok.Data;

import java.util.Date;

/**
 * 归档文章
 * @author 11921
 */
@Data
public class ArchiveDTO {
    /**
     * id
     */
    private Integer id;

    /**
     * 标题
     */
    private String articleTitle;

    /**
     * 发表时间
     */
    private Date createTime;

}
