package com.minzheng.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评论数量dto
 *
 * @author yezhiqiu
 * @date 2022/01/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentCountDTO {

    /**
     * id
     */
    private Integer id;

    /**
     * 评论数量
     */
    private Integer commentCount;
}
