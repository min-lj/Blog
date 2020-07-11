package com.minzheng.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minzheng.blog.dao.ArticleTagDao;
import com.minzheng.blog.dto.PageDTO;
import com.minzheng.blog.dto.TagDTO;
import com.minzheng.blog.entity.ArticleTag;
import com.minzheng.blog.entity.Tag;
import com.minzheng.blog.dao.TagDao;
import com.minzheng.blog.exception.ServeException;
import com.minzheng.blog.service.TagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minzheng.blog.utils.BeanCopyUtil;
import com.minzheng.blog.vo.ConditionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author xiaojie
 * @since 2020-05-18
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagDao, Tag> implements TagService {
    @Autowired
    private TagDao tagDao;
    @Autowired
    private ArticleTagDao articleTagDao;

    @Override
    public PageDTO<TagDTO> listTags() {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tag_name");
        //查询标签列表
        List<TagDTO> tagDTOList = BeanCopyUtil.copyList(tagDao.selectList(queryWrapper), TagDTO.class);
        //查询标签数量
        Integer count = tagDao.selectCount(null);
        return new PageDTO(tagDTOList, count);
    }

    @Override
    public PageDTO<Tag> listTagBackDTO(ConditionVO condition) {
        Page<Tag> page = new Page<>(condition.getCurrent(), condition.getSize());
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tag_name", "create_time")
                .like(condition.getKeywords() != null, "tag_name", condition.getKeywords());
        Page<Tag> tagPage = tagDao.selectPage(page, queryWrapper);
        return new PageDTO(tagPage.getRecords(), (int) tagPage.getTotal());
    }

    @Transactional(rollbackFor = ServeException.class)
    @Override
    public void deleteTag(List<Integer> tagIdList) {
        //查询标签下是否有文章
        QueryWrapper<ArticleTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id").in("tag_id", tagIdList);
        if (!articleTagDao.selectList(queryWrapper).isEmpty()) {
            throw new ServeException("删除失败，该标签下存在文章");
        }
        tagDao.deleteBatchIds(tagIdList);
    }

}
