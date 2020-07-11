package com.minzheng.blog.dto;

import lombok.Data;

import java.util.List;

/**
 * 分页列表
 * @author 11921
 */
@Data
public class PageDTO<T> {

    /**
     * 分页列表
     */
    private List<T> recordList;

    /**
     * 总数
     */
    private Integer count;

    public PageDTO(List<T> recordList, Integer count) {
        this.recordList = recordList;
        this.count = count;
    }

    public PageDTO() {
    }

}
