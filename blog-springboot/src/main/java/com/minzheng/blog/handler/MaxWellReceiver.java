package com.minzheng.blog.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.minzheng.blog.constant.ArticleConst;
import com.minzheng.blog.dao.ElasticsearchDao;
import com.minzheng.blog.dto.ArticleSearchDTO;
import com.minzheng.blog.entity.Article;
import com.minzheng.blog.utils.HTMLUtil;
import org.elasticsearch.action.update.UpdateRequest;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 同步es数据
 *
 * @author 11921
 */
@Component
@RabbitListener(queues = "article")
public class MaxWellReceiver {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private ElasticsearchDao elasticsearchDao;

    @RabbitHandler
    public void process(byte[] data) {
        //获取更改信息
        Map map = JSON.parseObject(new String(data), Map.class);
        //获取文章数据
        Article article = JSONObject.toJavaObject((JSONObject) map.get("data"), Article.class);
        //过滤markdown标签
        article.setArticleContent(HTMLUtil.deleteArticleTag(article.getArticleContent()));
        //判断操作类型
        String type = map.get("type").toString();
        switch (type) {
            case "insert":
                //发布文章后更新es文章
                if (article.getIsDraft() == ArticleConst.PUBLISH) {
                    elasticsearchDao.save(new ArticleSearchDTO(article));
                }
                break;
            case "update":
                //文章更新操作
                updateEsArticle(article, map);
                break;
            case "delete":
                //物理删除文章
                if (article.getIsDraft() == ArticleConst.PUBLISH) {
                    elasticsearchDao.deleteById(article.getId());
                }
            default:
                break;
        }
    }

    /**
     * 文章更新操作
     *
     * @param map     mysql数据
     * @param article 文章数据
     */
    private void updateEsArticle(Article article, Map map) {
        //获取文章修改属性
        Map<String, Object> oldData = (Map<String, Object>) map.get("old");
        //修改文章不为草稿
        if (article.getIsDraft() == ArticleConst.PUBLISH) {
            //发布草稿后添加es文章
            if (oldData.get("is_draft") != null) {
                elasticsearchDao.save(new ArticleSearchDTO(article));
                return;
            }
            UpdateRequest updateRequest = new UpdateRequest();
            if (oldData.get("is_delete") != null) {
                //修改文章状态后更改es文章状态
                updateRequest.doc("isDelete", article.getIsDelete());
            } else {
                //修改发布文章内容后同步es文章
                updateRequest.doc("articleTitle", article.getArticleTitle(), "articleContent", article.getArticleContent());
            }
            UpdateQuery updateQuery = new UpdateQueryBuilder().withId(article.getId().toString()).withUpdateRequest(updateRequest).withClass(ArticleSearchDTO.class).build();
            elasticsearchTemplate.update(updateQuery);
        }
    }

}