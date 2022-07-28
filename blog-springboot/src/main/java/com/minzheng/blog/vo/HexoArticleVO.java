package com.minzheng.blog.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * hexo文章
 *
 * @author yezhiqiu
 * @date 2022/07/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HexoArticleVO extends ArticleVO {
    private LocalDateTime createTime;
}
