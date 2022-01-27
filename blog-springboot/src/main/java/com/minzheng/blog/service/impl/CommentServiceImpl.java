package com.minzheng.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.minzheng.blog.dao.ArticleDao;
import com.minzheng.blog.dao.TalkDao;
import com.minzheng.blog.dao.UserInfoDao;
import com.minzheng.blog.dto.*;
import com.minzheng.blog.entity.Comment;
import com.minzheng.blog.dao.CommentDao;
import com.minzheng.blog.service.BlogInfoService;
import com.minzheng.blog.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minzheng.blog.service.RedisService;
import com.minzheng.blog.util.HTMLUtils;
import com.minzheng.blog.util.PageUtils;
import com.minzheng.blog.util.UserUtils;
import com.minzheng.blog.vo.*;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.minzheng.blog.constant.CommonConst.*;
import static com.minzheng.blog.constant.MQPrefixConst.EMAIL_EXCHANGE;
import static com.minzheng.blog.constant.RedisPrefixConst.COMMENT_LIKE_COUNT;
import static com.minzheng.blog.constant.RedisPrefixConst.COMMENT_USER_LIKE;
import static com.minzheng.blog.enums.CommentTypeEnum.*;

/**
 * 评论服务
 *
 * @author yezhiqiu
 * @date 2021/07/31
 * @since 2020-05-18
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentDao, Comment> implements CommentService {
    @Autowired
    private CommentDao commentDao;
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private TalkDao talkDao;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private BlogInfoService blogInfoService;

    /**
     * 网站网址
     */
    @Value("${website.url}")
    private String websiteUrl;

    @Override
    public PageResult<CommentDTO> listComments(CommentVO commentVO) {
        // 查询评论量
        Integer commentCount = commentDao.selectCount(new LambdaQueryWrapper<Comment>()
                .eq(ARTICLE.getType().equals(commentVO.getType()), Comment::getArticleId, commentVO.getArticleId())
                .eq(TALK.getType().equals(commentVO.getType()), Comment::getTalkId, commentVO.getTalkId())
                .eq(LINK.getType().equals(commentVO.getType()), Comment::getType, commentVO.getType())
                .isNull(Comment::getParentId)
                .eq(Comment::getIsReview, TRUE));
        if (commentCount == 0) {
            return new PageResult<>();
        }
        // 分页查询评论集合
        List<CommentDTO> commentDTOList = commentDao.listComments(PageUtils.getLimitCurrent(), PageUtils.getSize(), commentVO);
        if (CollectionUtils.isEmpty(commentDTOList)) {
            return new PageResult<>();
        }
        // 查询redis的评论点赞数据
        Map<String, Object> likeCountMap = redisService.hGetAll(COMMENT_LIKE_COUNT);
        // 提取评论id集合
        List<Integer> commentIdList = commentDTOList.stream()
                .map(CommentDTO::getId)
                .collect(Collectors.toList());
        // 根据评论id集合查询回复数据
        List<ReplyDTO> replyDTOList = commentDao.listReplies(commentIdList);
        // 封装回复点赞量
        replyDTOList.forEach(item -> item.setLikeCount((Integer) likeCountMap.get(item.getId().toString())));
        // 根据评论id分组回复数据
        Map<Integer, List<ReplyDTO>> replyMap = replyDTOList.stream()
                .collect(Collectors.groupingBy(ReplyDTO::getParentId));
        // 根据评论id查询回复量
        Map<Integer, Integer> replyCountMap = commentDao.listReplyCountByCommentId(commentIdList)
                .stream().collect(Collectors.toMap(ReplyCountDTO::getCommentId, ReplyCountDTO::getReplyCount));
        // 封装评论数据
        commentDTOList.forEach(item -> {
            item.setLikeCount((Integer) likeCountMap.get(item.getId().toString()));
            item.setReplyDTOList(replyMap.get(item.getId()));
            item.setReplyCount(replyCountMap.get(item.getId()));
        });
        return new PageResult<>(commentDTOList, commentCount);
    }

    @Override
    public List<ReplyDTO> listRepliesByCommentId(Integer commentId) {
        // 转换页码查询评论下的回复
        List<ReplyDTO> replyDTOList = commentDao.listRepliesByCommentId(PageUtils.getLimitCurrent(), PageUtils.getSize(), commentId);
        // 查询redis的评论点赞数据
        Map<String, Object> likeCountMap = redisService.hGetAll(COMMENT_LIKE_COUNT);
        // 封装点赞数据
        replyDTOList.forEach(item -> item.setLikeCount((Integer) likeCountMap.get(item.getId().toString())));
        return replyDTOList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveComment(CommentVO commentVO) {
        // 判断是否需要审核
        WebsiteConfigVO websiteConfig = blogInfoService.getWebsiteConfig();
        Integer isReview = websiteConfig.getIsCommentReview();
        // 过滤标签
        commentVO.setCommentContent(HTMLUtils.deleteTag(commentVO.getCommentContent()));
        Comment comment = Comment.builder()
                .userId(UserUtils.getLoginUser().getUserInfoId())
                .replyUserId(commentVO.getReplyUserId())
                .articleId(commentVO.getArticleId())
                .talkId(commentVO.getTalkId())
                .commentContent(commentVO.getCommentContent())
                .parentId(commentVO.getParentId())
                .type(commentVO.getType())
                .isReview(isReview == TRUE ? FALSE : TRUE)
                .build();
        commentDao.insert(comment);
        // 判断是否开启邮箱通知,通知用户
        CompletableFuture.runAsync(() -> {
            if (websiteConfig.getIsEmailNotice().equals(TRUE)) {
                notice(comment);
            }
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveCommentLike(Integer commentId) {
        // 判断是否点赞
        String commentLikeKey = COMMENT_USER_LIKE + UserUtils.getLoginUser().getUserInfoId();
        if (redisService.sIsMember(commentLikeKey, commentId)) {
            // 点过赞则删除评论id
            redisService.sRemove(commentLikeKey, commentId);
            // 评论点赞量-1
            redisService.hDecr(COMMENT_LIKE_COUNT, commentId.toString(), 1L);
        } else {
            // 未点赞则增加评论id
            redisService.sAdd(commentLikeKey, commentId);
            // 评论点赞量+1
            redisService.hIncr(COMMENT_LIKE_COUNT, commentId.toString(), 1L);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCommentsReview(ReviewVO reviewVO) {
        // 修改评论审核状态
        List<Comment> commentList = reviewVO.getIdList().stream().map(item -> Comment.builder()
                        .id(item)
                        .isReview(reviewVO.getIsReview())
                        .build())
                .collect(Collectors.toList());
        this.updateBatchById(commentList);
    }

    @Override
    public PageResult<CommentBackDTO> listCommentBackDTO(ConditionVO condition) {
        // 统计后台评论量
        Integer count = commentDao.countCommentDTO(condition);
        if (count == 0) {
            return new PageResult<>();
        }
        // 查询后台评论集合
        List<CommentBackDTO> commentBackDTOList = commentDao.listCommentBackDTO(PageUtils.getLimitCurrent(), PageUtils.getSize(), condition);
        return new PageResult<>(commentBackDTOList, count);
    }

    /**
     * 通知评论用户
     *
     * @param comment 评论信息
     */
    public void notice(Comment comment) {
        // 查询回复用户邮箱号
        Integer userId = BLOGGER_ID;
        String id = "";
        switch (Objects.requireNonNull(getCommentEnum(comment.getType()))) {
            case ARTICLE:
                userId = articleDao.selectById(comment.getArticleId()).getUserId();
                id = comment.getArticleId().toString();
                break;
            case TALK:
                userId = talkDao.selectById(comment.getTalkId()).getUserId();
                id = comment.getTalkId().toString();
                break;
            default:
                break;
        }
        if (Objects.nonNull(comment.getReplyUserId())) {
            userId = comment.getReplyUserId();
        }
        String email = userInfoDao.selectById(userId).getEmail();
        if (StringUtils.isNotBlank(email)) {
            // 发送消息
            EmailDTO emailDTO = new EmailDTO();
            if (comment.getIsReview().equals(TRUE)) {
                // 评论提醒
                emailDTO.setEmail(email);
                emailDTO.setSubject("评论提醒");
                // 获取评论路径
                String url = websiteUrl + getCommentPath(comment.getType()) + id;
                emailDTO.setContent("您收到了一条新的回复，请前往" + url + "\n页面查看");
            } else {
                // 管理员审核提醒
                String adminEmail = userInfoDao.selectById(BLOGGER_ID).getEmail();
                emailDTO.setEmail(adminEmail);
                emailDTO.setSubject("审核提醒");
                emailDTO.setContent("您收到了一条新的回复，请前往后台管理页面审核");
            }
            rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, "*", new Message(JSON.toJSONBytes(emailDTO), new MessageProperties()));
        }
    }

}
