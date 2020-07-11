package com.minzheng.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minzheng.blog.constant.DeleteConst;
import com.minzheng.blog.dto.MessageBackDTO;
import com.minzheng.blog.dto.PageDTO;
import com.minzheng.blog.exception.ServeException;
import com.minzheng.blog.vo.ConditionVO;
import com.minzheng.blog.vo.MessageVO;
import com.minzheng.blog.dto.MessageDTO;
import com.minzheng.blog.entity.Message;
import com.minzheng.blog.dao.MessageDao;
import com.minzheng.blog.service.MessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minzheng.blog.utils.BeanCopyUtil;
import com.minzheng.blog.utils.IpUtil;
import com.minzheng.blog.vo.DeleteVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaojie
 * @since 2020-05-18
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageDao, Message> implements MessageService {
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private HttpServletRequest request;

    @Transactional(rollbackFor = ServeException.class)
    @Override
    public void saveMessage(MessageVO messageVO) {
        String ipAddr = IpUtil.getIpAddr(request);
        String ipSource = IpUtil.getIpSource(ipAddr);
        messageDao.insert(new Message(messageVO, ipAddr, ipSource));
    }

    @Override
    public List<MessageDTO> listMessages() {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "nickname", "avatar", "message_content", "time");
        return BeanCopyUtil.copyList(messageDao.selectList(queryWrapper), MessageDTO.class);
    }

    @Override
    public PageDTO listMessageBackDTO(ConditionVO condition) {
        Page<Message> page = new Page<>(condition.getCurrent(), condition.getSize());
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "nickname", "avatar", "ip_address", "ip_source", "message_content", "create_time")
                .like(condition.getKeywords() != null, "nickname", condition.getKeywords())
                .orderByDesc("create_time");
        Page<Message> messagePage = messageDao.selectPage(page, queryWrapper);
        //转换DTO
        List<MessageBackDTO> messageBackDTOList = BeanCopyUtil.copyList(messagePage.getRecords(), MessageBackDTO.class);
        return new PageDTO(messageBackDTOList, (int) messagePage.getTotal());
    }

}
