package com.minzheng.blog.controller;


import com.minzheng.blog.constant.StatusConst;
import com.minzheng.blog.dto.FriendLinkBackDTO;
import com.minzheng.blog.dto.FriendLinkDTO;
import com.minzheng.blog.dto.PageDTO;
import com.minzheng.blog.entity.FriendLink;
import com.minzheng.blog.exception.ServeException;
import com.minzheng.blog.service.FriendLinkService;
import com.minzheng.blog.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author xiaojie
 * @since 2020-05-18
 */
@Api(tags = "友链模块")
@RestController
public class FriendLinkController {
    @Autowired
    private FriendLinkService friendLinkService;

    @ApiOperation(value = "查看友链列表")
    @GetMapping("/links")
    private Result<List<FriendLinkDTO>> listFriendLinks() {
        return new Result(true, StatusConst.OK, "查询成功", friendLinkService.listFriendLinks());
    }

    @ApiOperation(value = "查看后台友链列表")
    @GetMapping("/admin/links")
    private Result<PageDTO<FriendLinkBackDTO>> listFriendLinkDTO(ConditionVO condition) {
        return new Result(true, StatusConst.OK, "查询成功", friendLinkService.listFriendLinkDTO(condition));
    }

    @ApiOperation(value = "保存或修改友链")
    @PostMapping("/admin/links")
    private Result saveOrUpdateFriendLink(@Valid @RequestBody FriendLinkVO friendLinkVO) {
        friendLinkService.saveOrUpdate(new FriendLink(friendLinkVO));
        return new Result(true, StatusConst.OK, "操作成功");
    }

    @ApiOperation(value = "删除友链")
    @DeleteMapping("/admin/links")
    private Result deleteFriendLink(@RequestBody List<Integer> linkIdList) {
        friendLinkService.removeByIds(linkIdList);
        return new Result(true, StatusConst.OK, "删除成功");
    }

}

