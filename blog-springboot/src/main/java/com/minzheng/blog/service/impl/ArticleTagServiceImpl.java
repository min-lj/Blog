package com.minzheng.blog.service.impl;

import com.minzheng.blog.entity.ArticleTag;
import com.minzheng.blog.dao.ArticleTagDao;
import com.minzheng.blog.service.ArticleTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 文章标签服务
 *
 * @author yezhiqiu
 * @date 2021/08/10
 */
@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagDao, ArticleTag> implements ArticleTagService {

}
