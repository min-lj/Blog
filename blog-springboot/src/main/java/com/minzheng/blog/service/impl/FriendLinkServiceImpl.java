package com.minzheng.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minzheng.blog.constant.DeleteConst;
import com.minzheng.blog.dto.FriendLinkBackDTO;
import com.minzheng.blog.dto.FriendLinkDTO;
import com.minzheng.blog.dto.PageDTO;
import com.minzheng.blog.entity.FriendLink;
import com.minzheng.blog.dao.FriendLinkDao;
import com.minzheng.blog.service.FriendLinkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minzheng.blog.utils.BeanCopyUtil;
import com.minzheng.blog.vo.ConditionVO;
import com.minzheng.blog.vo.DeleteVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaojie
 * @since 2020-05-18
 */
@Service
public class FriendLinkServiceImpl extends ServiceImpl<FriendLinkDao, FriendLink> implements FriendLinkService {
    @Autowired
    private FriendLinkDao friendLinkDao;

    @Override
    public List<FriendLinkDTO> listFriendLinks() {
        QueryWrapper<FriendLink> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "link_name", "link_avatar", "link_address", "link_intro");
        return BeanCopyUtil.copyList(friendLinkDao.selectList(queryWrapper), FriendLinkDTO.class);
    }

    @Override
    public PageDTO listFriendLinkDTO(ConditionVO condition) {
        Page<FriendLink> page = new Page<>(condition.getCurrent(), condition.getSize());
        QueryWrapper<FriendLink> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "link_name", "link_avatar", "link_address", "link_intro", "create_time")
                .like(condition.getKeywords() != null, "link_name", condition.getKeywords());
        Page<FriendLink> friendLinkPage = friendLinkDao.selectPage(page, queryWrapper);
        //转换DTO
        List<FriendLinkBackDTO> friendLinkBackDTOList = BeanCopyUtil.copyList(friendLinkPage.getRecords(), FriendLinkBackDTO.class);
        return new PageDTO(friendLinkBackDTOList, (int) friendLinkPage.getTotal());
    }

}
