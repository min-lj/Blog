package com.minzheng.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minzheng.blog.constant.CommonConst;
import com.minzheng.blog.dao.*;
import com.minzheng.blog.dto.*;
import com.minzheng.blog.entity.Article;
import com.minzheng.blog.entity.UserInfo;
import com.minzheng.blog.service.BlogInfoService;
import com.minzheng.blog.service.RedisService;
import com.minzheng.blog.service.UniqueViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.minzheng.blog.constant.CommonConst.FALSE;
import static com.minzheng.blog.constant.RedisPrefixConst.*;

/**
 * @author xiaojie
 * @since 2020-05-18
 */
@Service
public class BlogInfoServiceImpl implements BlogInfoService {
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private TagDao tagDao;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private UniqueViewService uniqueViewService;
    @Autowired
    private RedisService redisService;

    @Override
    public BlogHomeInfoDTO getBlogInfo() {
        // 查询博主信息
        UserInfo userInfo = userInfoDao.selectOne(new LambdaQueryWrapper<UserInfo>()
                .select(UserInfo::getAvatar, UserInfo::getNickname, UserInfo::getIntro)
                .eq(UserInfo::getId, CommonConst.BLOGGER_ID));
        // 查询文章数量
        Integer articleCount = articleDao.selectCount(new LambdaQueryWrapper<Article>()
                .eq(Article::getIsDraft, FALSE)
                .eq(Article::getIsDelete, FALSE));
        // 查询分类数量
        Integer categoryCount = categoryDao.selectCount(null);
        // 查询标签数量
        Integer tagCount = tagDao.selectCount(null);
        // 查询公告
        Object value = redisService.get(NOTICE);
        String notice = Objects.nonNull(value) ? value.toString() : "发布你的第一篇公告吧";
        // 查询访问量
        String viewsCount = Objects.requireNonNull(redisService.get(BLOG_VIEWS_COUNT)).toString();
        // 封装数据
        return BlogHomeInfoDTO.builder()
                .nickname(userInfo.getNickname())
                .avatar(userInfo.getAvatar())
                .intro(userInfo.getIntro())
                .articleCount(articleCount)
                .categoryCount(categoryCount)
                .tagCount(tagCount)
                .notice(notice)
                .viewsCount(viewsCount)
                .build();
    }

    @Override
    public BlogBackInfoDTO getBlogBackInfo() {
        // 查询访问量
        Integer viewsCount = (Integer) redisService.get(BLOG_VIEWS_COUNT);
        // 查询留言量
        Integer messageCount = messageDao.selectCount(null);
        // 查询用户量
        Integer userCount = userInfoDao.selectCount(null);
        // 查询文章量
        Integer articleCount = articleDao.selectCount(new LambdaQueryWrapper<Article>()
                .eq(Article::getIsDelete, FALSE)
                .eq(Article::getIsDraft, FALSE));
        // 查询一周用户量
        List<UniqueViewDTO> uniqueViewList = uniqueViewService.listUniqueViews();
        // 查询分类数据
        List<CategoryDTO> categoryDTOList = categoryDao.listCategoryDTO();
        // 查询redis访问量前五的文章
        Map<String, Integer> articleViewsMap = redisService.hGetAll(ARTICLE_VIEWS_COUNT);
        // 将文章进行倒序排序
        List<Integer> articleIdList = Objects.requireNonNull(articleViewsMap).entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(item -> Integer.valueOf(item.getKey()))
                .collect(Collectors.toList());
        // 提取前五篇文章
        int index = Math.min(articleIdList.size(), 5);
        articleIdList = articleIdList.subList(0, index);
        // 文章为空直接返回
        if (articleIdList.isEmpty()) {
            return BlogBackInfoDTO.builder()
                    .viewsCount(viewsCount)
                    .messageCount(messageCount)
                    .userCount(userCount)
                    .articleCount(articleCount)
                    .categoryDTOList(categoryDTOList)
                    .uniqueViewDTOList(uniqueViewList)
                    .build();
        }
        // 查询文章标题
        List<Article> articleList = articleDao.listArticleRank(articleIdList);
        // 封装浏览量
        List<ArticleRankDTO> articleRankDTOList = articleList.stream().map(article -> ArticleRankDTO.builder()
                .articleTitle(article.getArticleTitle())
                .viewsCount(articleViewsMap.get(article.getId().toString()))
                .build())
                .collect(Collectors.toList());
        return BlogBackInfoDTO.builder()
                .viewsCount(viewsCount)
                .messageCount(messageCount)
                .userCount(userCount)
                .articleCount(articleCount)
                .categoryDTOList(categoryDTOList)
                .uniqueViewDTOList(uniqueViewList)
                .articleRankDTOList(articleRankDTOList)
                .build();
    }

    @Override
    public String getAbout() {
        Object value = redisService.get(ABOUT);
        return Objects.nonNull(value) ? value.toString() : "";
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAbout(String aboutContent) {
        redisService.set(ABOUT, aboutContent);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateNotice(String notice) {
        redisService.set(NOTICE, notice);
    }

    @Override
    public String getNotice() {
        Object value = redisService.get(NOTICE);
        return Objects.nonNull(value) ? value.toString() : "发布你的第一篇公告吧";
    }

}
