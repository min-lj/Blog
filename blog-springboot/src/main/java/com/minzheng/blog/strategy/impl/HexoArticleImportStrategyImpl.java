package com.minzheng.blog.strategy.impl;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import com.minzheng.blog.exception.BizException;
import com.minzheng.blog.service.ArticleService;
import com.minzheng.blog.strategy.ArticleImportStrategy;
import com.minzheng.blog.vo.ArticleVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.minzheng.blog.constant.HexoConst.*;
import static com.minzheng.blog.enums.ArticleStatusEnum.PUBLIC;

/**
 * Hexo文章导入策略
 *
 * @author linweiyuan
 * @date 2022/07/28
 */
@Slf4j
@Service("hexoArticleImportStrategyImpl")
public class HexoArticleImportStrategyImpl implements ArticleImportStrategy {
    @Autowired
    private ArticleService articleService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void importArticles(MultipartFile file) {
        try {
            HexoArticleVO hexoArticleVO = new HexoArticleVO();
            hexoArticleVO.setType(1); // 原创
            hexoArticleVO.setStatus(PUBLIC.getStatus()); // 公开 （DRAFT不保存分类）

            AtomicInteger hexoDelimiterCount = new AtomicInteger();
            StringBuilder articleContent = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            reader.lines().forEach(line -> {
                if (hexoDelimiterCount.get() == 2) {
                    // 分隔符结束就是正文
                    articleContent.append(line).append(NEW_LINE);
                } else {
                    if (line.equals(DELIMITER)) {
                        hexoDelimiterCount.getAndIncrement();
                    }
                    if (hexoDelimiterCount.get() == 1) {
                        if (line.startsWith(TITLE_PREFIX)) {
                            hexoArticleVO.setArticleTitle(line.replace(TITLE_PREFIX, "").trim());
                        } else if (line.startsWith(DATE_PREFIX)) {
                            hexoArticleVO.setCreateTime(LocalDateTime.parse(line.replace(DATE_PREFIX, "").trim(), formatter));
                        } else if (line.startsWith(CATEGORIES_PREFIX)) {
                            // 不支持多级分类
                            hexoArticleVO.setCategoryName(new JSONArray(line.replace(CATEGORIES_PREFIX, "").trim()).getStr(0));
                        } else if (line.startsWith(TAGS_PREFIX)) {
                            hexoArticleVO.setTagNameList(new JSONArray(line.replace(TAGS_PREFIX, "").trim()).toList(String.class));
                        }
                    }
                }
            });
            hexoArticleVO.setArticleContent(articleContent.toString());

            articleService.saveOrUpdateArticle(hexoArticleVO);
        } catch (IOException e) {
            log.error(StrUtil.format("导入Hexo文章失败, 堆栈:{}", ExceptionUtil.stacktraceToString(e)));
            throw new BizException("导入Hexo文章失败");
        }
    }

    @SuppressWarnings("Lombok")
    @Data
    static class HexoArticleVO extends ArticleVO {
        private String articleTitle;
        private String categoryName;
        private List<String> tagNameList;
        private String articleContent;
        private Integer type;
        private Integer status;
        private LocalDateTime createTime;
    }
}
