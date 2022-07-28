package com.minzheng.blog.strategy.impl;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import com.minzheng.blog.dao.ArticleDao;
import com.minzheng.blog.entity.Article;
import com.minzheng.blog.exception.BizException;
import com.minzheng.blog.strategy.ArticleImportStrategy;
import com.minzheng.blog.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import static com.minzheng.blog.enums.ArticleStatusEnum.DRAFT;

/**
 * 普通文章导入策略
 *
 * @author linweiyuan
 * @date 2022/07/28
 */
@Slf4j
@Service("normalArticleImportStrategyImpl")
public class NormalArticleImportStrategyImpl implements ArticleImportStrategy {
    @Autowired
    private ArticleDao articleDao;

    @Override
    public void importArticles(MultipartFile file) {
        // 获取文件名作为文章标题
        String articleTitle = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[0];
        // 获取文章内容
        StringBuilder articleContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            while (reader.ready()) {
                articleContent.append((char) reader.read());
            }
        } catch (IOException e) {
            log.error(StrUtil.format("导入文章失败, 堆栈:{}", ExceptionUtil.stacktraceToString(e)));
            throw new BizException("导入文章失败");
        }
        articleDao.insert(Article.builder()
                .userId(UserUtils.getLoginUser().getUserInfoId())
                .articleTitle(articleTitle)
                .articleContent(articleContent.toString())
                .status(DRAFT.getStatus())
                .build());
    }
}
