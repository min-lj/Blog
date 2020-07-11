package com.minzheng.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minzheng.blog.constant.ArticleConst;
import com.minzheng.blog.constant.DeleteConst;
import com.minzheng.blog.dao.*;
import com.minzheng.blog.dto.*;
import com.minzheng.blog.entity.Article;
import com.minzheng.blog.entity.ArticleTag;
import com.minzheng.blog.entity.Category;
import com.minzheng.blog.entity.Tag;
import com.minzheng.blog.exception.ServeException;
import com.minzheng.blog.service.ArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minzheng.blog.service.ArticleTagService;
import com.minzheng.blog.utils.BeanCopyUtil;
import com.minzheng.blog.utils.HTMLUtil;
import com.minzheng.blog.utils.UserUtil;
import com.minzheng.blog.vo.ArticleVO;
import com.minzheng.blog.vo.ConditionVO;
import com.minzheng.blog.vo.DeleteVO;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author xiaojie
 * @since 2020-05-18
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleDao, Article> implements ArticleService {
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private TagDao tagDao;
    @Autowired
    private ElasticsearchDao elasticsearchDao;
    @Autowired
    private ArticleTagDao articleTagDao;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private HttpSession session;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleTagService articleTagService;

    @Override
    public PageDTO<ArchiveDTO> listArchives(Long current) {
        Page<Article> page = new Page<>(current, 10);
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "article_title", "create_time").orderByDesc("create_time").eq("is_delete", DeleteConst.NORMAL).eq("is_draft", ArticleConst.PUBLISH);
        //获取分页数据
        Page<Article> articlePage = articleDao.selectPage(page, queryWrapper);
        //拷贝dto集合
        List<ArchiveDTO> archiveDTOList = BeanCopyUtil.copyList(articlePage.getRecords(), ArchiveDTO.class);
        return new PageDTO<ArchiveDTO>(archiveDTOList, (int) articlePage.getTotal());
    }

    @Override
    public PageDTO<ArticleBackDTO> listArticleBackDTO(ConditionVO condition) {
        //转换页码
        condition.setCurrent((condition.getCurrent() - 1) * condition.getSize());
        //查询文章总量
        Integer count = articleDao.countArticleBacks(condition);
        if (count == 0) {
            return new PageDTO<ArticleBackDTO>();
        }
        //查询后台文章
        List<ArticleBackDTO> articleBackDTOList = articleDao.listArticleBacks(condition);
        //查询文章点赞量和浏览量
        Map<String, Integer> viewsCountMap = redisTemplate.boundHashOps("article_views_count").entries();
        Map<String, Integer> likeCountMap = redisTemplate.boundHashOps("article_like_count").entries();
        //封装点赞量和浏览量
        for (ArticleBackDTO articleBackDTO : articleBackDTOList) {
            articleBackDTO.setViewsCount(viewsCountMap.get(articleBackDTO.getId().toString()));
            articleBackDTO.setLikeCount(likeCountMap.get(articleBackDTO.getId().toString()));
        }
        return new PageDTO<ArticleBackDTO>(articleBackDTOList, count);
    }

    @Override
    public List<ArticleHomeDTO> listArticles(Long current) {
        //转换页码查询
        List<ArticleHomeDTO> articleDTOList = articleDao.listArticles((current - 1) * 10);
        //文章内容过滤markdown标签展示
        for (ArticleHomeDTO articleDTO : articleDTOList) {
            articleDTO.setArticleContent(HTMLUtil.deleteArticleTag(articleDTO.getArticleContent()));
        }
        return articleDTOList;
    }

    @Override
    public ArticlePreviewListDTO listArticlesByCondition(ConditionVO condition) {
        //转换页码
        condition.setCurrent((condition.getCurrent() - 1) * 9);
        //搜索条件对应数据
        List<ArticlePreviewDTO> articlePreviewDTOList = articleDao.listArticlesByCondition(condition);
        //搜索条件对应名(标签或分类名)
        String name = null;
        if (condition.getCategoryId() != null) {
            QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("category_name").eq("id", condition.getCategoryId());
            name = categoryDao.selectOne(queryWrapper).getCategoryName();
        } else {
            QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("tag_name").eq("id", condition.getTagId());
            name = tagDao.selectOne(queryWrapper).getTagName();
        }
        return new ArticlePreviewListDTO(articlePreviewDTOList, name);
    }

    @Override
    public ArticleDTO getArticleById(Integer articleId) {
        //判断是否第一次访问，增加浏览量
        Set<Integer> set = (Set<Integer>) session.getAttribute("articleSet");
        if (set == null) {
            set = new HashSet<Integer>();
        }
        if (!set.contains(articleId)) {
            set.add(articleId);
            session.setAttribute("articleSet", set);
            //浏览量+1
            redisTemplate.boundHashOps("article_views_count").increment(articleId.toString(), 1);
        }
        //查询id对应的文章
        ArticleDTO article = articleDao.getArticleById(articleId);
        //封装点赞量和浏览量封装
        article.setViewsCount((Integer) redisTemplate.boundHashOps("article_views_count").get(articleId.toString()));
        article.setLikeCount((Integer) redisTemplate.boundHashOps("article_like_count").get(articleId.toString()));
        return article;
    }

    @Override
    public ArticleOptionDTO listArticleOptionDTO() {
        //查询文章分类选项
        QueryWrapper<Category> categoryQueryWrapper = new QueryWrapper<>();
        categoryQueryWrapper.select("id", "category_name");
        List<CategoryBackDTO> categoryDTOList = BeanCopyUtil.copyList(categoryDao.selectList(categoryQueryWrapper), CategoryBackDTO.class);
        //查询文章标签选项
        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        tagQueryWrapper.select("id", "tag_name");
        List<TagDTO> tagDTOList = BeanCopyUtil.copyList(tagDao.selectList(tagQueryWrapper), TagDTO.class);
        return new ArticleOptionDTO(categoryDTOList, tagDTOList);
    }

    @Transactional(rollbackFor = ServeException.class)
    @Override
    public void saveArticleLike(Integer articleId) {
        //查询当前用户点赞过的文章id集合
        Set<Integer> articleLikeSet = (Set<Integer>) redisTemplate.boundHashOps("article_user_like").get(UserUtil.getLoginUser().getUserInfoId().toString());
        //第一次点赞则创建
        if (articleLikeSet == null) {
            articleLikeSet = new HashSet<Integer>();
        }
        //判断是否点赞
        if (articleLikeSet.contains(articleId)) {
            //点过赞则删除文章id
            articleLikeSet.remove(articleId);
            //文章点赞量-1
            redisTemplate.boundHashOps("article_like_count").increment(articleId.toString(), -1);
        } else {
            //未点赞则增加文章id
            articleLikeSet.add(articleId);
            //文章点赞量+1
            redisTemplate.boundHashOps("article_like_count").increment(articleId.toString(), 1);
        }
        //保存点赞记录
        redisTemplate.boundHashOps("article_user_like").put(UserUtil.getLoginUser().getUserInfoId().toString(), articleLikeSet);
    }

    @Transactional(rollbackFor = ServeException.class)
    @Override
    public void saveOrUpdateArticle(ArticleVO articleVO) {
        Article article = new Article(articleVO);
        //编辑文章则删除文章所有标签
        if (articleVO.getId() != null && articleVO.getIsDraft() == ArticleConst.PUBLISH) {
            QueryWrapper<ArticleTag> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("article_id", articleVO.getId());
            articleTagDao.delete(queryWrapper);
        }
        articleService.saveOrUpdate(article);
        //添加文章标签
        if (!articleVO.getTagIdList().isEmpty()) {
            List<ArticleTag> articleTagList = new ArrayList<>();
            for (Integer tagId : articleVO.getTagIdList()) {
                articleTagList.add(new ArticleTag(article.getId(), tagId));
            }
            articleTagService.saveBatch(articleTagList);
        }
    }

    @Transactional(rollbackFor = ServeException.class)
    @Override
    public void updateArticleTop(Integer articleId, Integer isTop) {
        //修改文章置顶状态
        articleDao.updateById(new Article(articleId, isTop));
    }

    @Transactional(rollbackFor = ServeException.class)
    @Override
    public void updateArticleDelete(DeleteVO deleteVO) {
        //修改文章逻辑删除状态
        List<Article> articleList = new ArrayList<>();
        for (Integer articleId : deleteVO.getIdList()) {
            Article article = new Article(articleId);
            article.setIsDelete(deleteVO.getIsDelete());
            articleList.add(article);
        }
        articleService.updateBatchById(articleList);
    }

    @Transactional(rollbackFor = ServeException.class)
    @Override
    public void deleteArticles(List<Integer> articleIdList) {
        //删除文章标签关联
        QueryWrapper<ArticleTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("article_id", articleIdList);
        articleTagDao.delete(queryWrapper);
        //删除文章
        articleDao.deleteBatchIds(articleIdList);
    }




    @Override
    public List<ArticleSearchDTO> listArticlesBySearch(ConditionVO condition) {
        return searchArticle(buildQuery(condition));
    }

    @Override
    public ArticleVO getArticleBackById(Integer articleId) {
        //查询文章信息
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.select("id", "article_title", "article_content", "article_cover", "category_id", "is_top", "is_draft").eq("id", articleId);
        Article article = articleDao.selectOne(articleQueryWrapper);
        //查询文章标签
        QueryWrapper<ArticleTag> articleTagQueryWrapper = new QueryWrapper<>();
        articleTagQueryWrapper.select("tag_id").eq("article_id", article.getId());
        List<ArticleTag> articleTagList = articleTagDao.selectList(articleTagQueryWrapper);
        //提取标签id集合
        List<Integer> tagIdList = new ArrayList<>();
        for (ArticleTag articleTag : articleTagList) {
            tagIdList.add(articleTag.getTagId());
        }
        return new ArticleVO(article, tagIdList);
    }

    /**
     * 搜索文章构造
     *
     * @param condition 条件
     * @return es条件构造器
     */
    private NativeSearchQueryBuilder buildQuery(ConditionVO condition) {
        //条件构造器
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //根据关键词搜索文章标题或内容
        if (condition.getKeywords() != null) {
            boolQueryBuilder.must(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("articleTitle", condition.getKeywords()))
                    .should(QueryBuilders.matchQuery("articleContent", condition.getKeywords())))
                    .must(QueryBuilders.termQuery("isDelete", DeleteConst.NORMAL));
        }
        //查询
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
        return nativeSearchQueryBuilder;
    }

    /**
     * 文章搜索结果高亮
     *
     * @param nativeSearchQueryBuilder es条件构造器
     * @return 搜索结果
     */
    private List<ArticleSearchDTO> searchArticle(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        //添加文章标题高亮
        HighlightBuilder.Field titleField = new HighlightBuilder.Field("articleTitle");
        titleField.preTags("<span style='color:#f47466'>");
        titleField.postTags("</span>");
        //添加文章内容高亮
        HighlightBuilder.Field contentField = new HighlightBuilder.Field("articleContent");
        contentField.preTags("<span style='color:#f47466'>");
        contentField.postTags("</span>");
        contentField.fragmentSize(200);
        nativeSearchQueryBuilder.withHighlightFields(titleField, contentField);
        //搜索
        AggregatedPage<ArticleSearchDTO> page = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), ArticleSearchDTO.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
                List list = new ArrayList();
                for (SearchHit hit : response.getHits()) {
                    //获取所有数据
                    ArticleSearchDTO article = JSON.parseObject(hit.getSourceAsString(), ArticleSearchDTO.class);
                    //获取文章标题高亮数据
                    HighlightField titleField = hit.getHighlightFields().get("articleTitle");
                    if (titleField != null && titleField.getFragments() != null) {
                        //替换标题数据
                        article.setArticleTitle(titleField.getFragments()[0].toString());
                    }
                    //获取文章内容高亮数据
                    HighlightField contentField = hit.getHighlightFields().get("articleContent");
                    if (contentField != null && contentField.getFragments() != null) {
                        //替换内容数据
                        article.setArticleContent(contentField.getFragments()[0].toString());
                    }
                    list.add(article);
                }
                return new AggregatedPageImpl<T>(list, pageable, response.getHits().getTotalHits());
            }
        });
        return page.getContent();
    }

}
