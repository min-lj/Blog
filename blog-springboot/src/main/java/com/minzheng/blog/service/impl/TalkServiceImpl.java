package com.minzheng.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minzheng.blog.dao.CommentDao;
import com.minzheng.blog.dto.CommentCountDTO;
import com.minzheng.blog.dto.TalkBackDTO;
import com.minzheng.blog.dto.TalkDTO;
import com.minzheng.blog.entity.Talk;
import com.minzheng.blog.exception.BizException;
import com.minzheng.blog.service.RedisService;
import com.minzheng.blog.service.TalkService;
import com.minzheng.blog.dao.TalkDao;
import com.minzheng.blog.util.BeanCopyUtils;
import com.minzheng.blog.util.HTMLUtils;
import com.minzheng.blog.util.PageUtils;
import com.minzheng.blog.util.UserUtils;
import com.minzheng.blog.vo.ConditionVO;
import com.minzheng.blog.vo.PageResult;
import com.minzheng.blog.vo.TalkVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.minzheng.blog.constant.RedisPrefixConst.*;
import static com.minzheng.blog.enums.TalkStatusEnum.PUBLIC;

/**
 * 说说服务
 *
 * @author yezhiqiu
 * @date 2022/01/23
 */
@Service
public class TalkServiceImpl extends ServiceImpl<TalkDao, Talk> implements TalkService {
    @Autowired
    private TalkDao talkDao;
    @Autowired
    private CommentDao commentDao;
    @Autowired
    private RedisService redisService;

    @Override
    public List<String> listHomeTalks() {
        // 查询最新10条说说
        return talkDao.selectList(new LambdaQueryWrapper<Talk>()
                        .eq(Talk::getStatus, PUBLIC.getStatus())
                        .orderByDesc(Talk::getIsTop)
                        .orderByDesc(Talk::getId)
                        .last("limit 10"))
                .stream()
                .map(item -> item.getContent().length() > 200 ? HTMLUtils.deleteTag(item.getContent().substring(0, 200)) : HTMLUtils.deleteTag(item.getContent()))
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<TalkDTO> listTalks() {
        // 查询说说总量
        Integer count = talkDao.selectCount((new LambdaQueryWrapper<Talk>()
                .eq(Talk::getStatus, PUBLIC.getStatus())));
        if (count == 0) {
            return new PageResult<>();
        }
        // 分页查询说说
        List<TalkDTO> talkDTOList = talkDao.listTalks(PageUtils.getLimitCurrent(), PageUtils.getSize());
        // 查询说说评论量
        List<Integer> talkIdList = talkDTOList.stream()
                .map(TalkDTO::getId)
                .collect(Collectors.toList());
        Map<Integer, Integer> commentCountMap = commentDao.listCommentCountByTalkIds(talkIdList)
                .stream()
                .collect(Collectors.toMap(CommentCountDTO::getId, CommentCountDTO::getCommentCount));
        // 查询说说点赞量
        Map<String, Object> likeCountMap = redisService.hGetAll(TALK_LIKE_COUNT);
        talkDTOList.forEach(item -> {
            item.setLikeCount((Integer) likeCountMap.get(item.getId().toString()));
            item.setCommentCount(commentCountMap.get(item.getId()));
            // 转换图片格式
            if (Objects.nonNull(item.getImages())) {
                item.setImgList(JSON.parseObject(item.getImages(), List.class));
            }
        });
        return new PageResult<>(talkDTOList, count);
    }

    @Override
    public TalkDTO getTalkById(Integer talkId) {
        // 查询说说信息
        TalkDTO talkDTO = talkDao.getTalkById(talkId);
        if (Objects.isNull(talkDTO)) {
            throw new BizException("说说不存在");
        }
        // 查询说说点赞量
        talkDTO.setLikeCount((Integer) redisService.hGet(TALK_LIKE_COUNT, talkId.toString()));
        // 转换图片格式
        if (Objects.nonNull(talkDTO.getImages())) {
            talkDTO.setImgList(JSON.parseObject(talkDTO.getImages(), List.class));
        }
        return talkDTO;
    }

    @Override
    public void saveTalkLike(Integer talkId) {
        // 判断是否点赞
        String talkLikeKey = TALK_USER_LIKE + UserUtils.getLoginUser().getUserInfoId();
        if (redisService.sIsMember(talkLikeKey, talkId)) {
            // 点过赞则删除说说id
            redisService.sRemove(talkLikeKey, talkId);
            // 说说点赞量-1
            redisService.hDecr(TALK_LIKE_COUNT, talkId.toString(), 1L);
        } else {
            // 未点赞则增加说说id
            redisService.sAdd(talkLikeKey, talkId);
            // 说说点赞量+1
            redisService.hIncr(TALK_LIKE_COUNT, talkId.toString(), 1L);
        }
    }

    @Override
    public void saveOrUpdateTalk(TalkVO talkVO) {
        Talk talk = BeanCopyUtils.copyObject(talkVO, Talk.class);
        talk.setUserId(UserUtils.getLoginUser().getUserInfoId());
        this.saveOrUpdate(talk);
    }

    @Override
    public void deleteTalks(List<Integer> talkIdList) {
        talkDao.deleteBatchIds(talkIdList);
    }

    @Override
    public PageResult<TalkBackDTO> listBackTalks(ConditionVO conditionVO) {
        // 查询说说总量
        Integer count = talkDao.selectCount(new LambdaQueryWrapper<Talk>()
                .eq(Objects.nonNull(conditionVO.getStatus()), Talk::getStatus, conditionVO.getStatus()));
        if (count == 0) {
            return new PageResult<>();
        }
        // 分页查询说说
        List<TalkBackDTO> talkDTOList = talkDao.listBackTalks(PageUtils.getLimitCurrent(), PageUtils.getSize(), conditionVO);
        talkDTOList.forEach(item -> {
            // 转换图片格式
            if (Objects.nonNull(item.getImages())) {
                item.setImgList(JSON.parseObject(item.getImages(), List.class));
            }
        });
        return new PageResult<>(talkDTOList, count);
    }

    @Override
    public TalkBackDTO getBackTalkById(Integer talkId) {
        TalkBackDTO talkBackDTO = talkDao.getBackTalkById(talkId);
        // 转换图片格式
        if (Objects.nonNull(talkBackDTO.getImages())) {
            talkBackDTO.setImgList(JSON.parseObject(talkBackDTO.getImages(), List.class));
        }
        return talkBackDTO;
    }

}




