package com.minzheng.blog.controller;


import com.minzheng.blog.dto.BlogHomeInfoDTO;
import com.minzheng.blog.service.BlogInfoService;
import com.minzheng.blog.vo.Result;
import com.minzheng.blog.constant.StatusConst;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaojie
 * @since 2020-05-18
 */
@Api(tags = "博客信息模块")
@RestController
public class BlogInfoController {
    @Autowired
    private BlogInfoService blogInfoService;

    @ApiOperation(value = "查看博客信息")
    @GetMapping("/")
    private Result<BlogHomeInfoDTO> getBlogHomeInfo() {
        return new Result(true, StatusConst.OK, "查询成功", blogInfoService.getBlogInfo());
    }

    @ApiOperation(value = "查看后台信息")
    @GetMapping("/admin")
    private Result<BlogHomeInfoDTO> getBlogBackInfo() {
        return new Result(true, StatusConst.OK, "查询成功", blogInfoService.getBlogBackInfo());
    }

    @ApiOperation(value = "查看关于我信息")
    @GetMapping("/about")
    private Result<String> getAbout() {
        return new Result(true, StatusConst.OK, "查询成功", blogInfoService.getAbout());
    }

    @ApiOperation(value = "修改关于我信息")
    @PutMapping("/admin/about")
    private Result updateAbout(String aboutContent) {
        blogInfoService.updateAbout(aboutContent);
        return new Result(true, StatusConst.OK, "修改成功");
    }

    @ApiOperation(value = "修改公告")
    @PutMapping("/admin/notice")
    private Result updateNotice(String notice) {
        blogInfoService.updateNotice(notice);
        return new Result(true, StatusConst.OK, "修改成功");
    }

    @ApiOperation(value = "查看公告")
    @GetMapping("/admin/notice")
    private Result<String> getNotice() {
        return new Result(true, StatusConst.OK, "查看成功", blogInfoService.getNotice());
    }

}

