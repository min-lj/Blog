package com.minzheng.blog.controller;

import com.minzheng.blog.constant.StatusConst;
import com.minzheng.blog.dto.CommentBackDTO;
import com.minzheng.blog.dto.CommentDTO;
import com.minzheng.blog.dto.PageDTO;
import com.minzheng.blog.dto.ReplyDTO;
import com.minzheng.blog.exception.ServeException;
import com.minzheng.blog.service.CommentService;
import com.minzheng.blog.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author xiaojie
 * @since 2020-05-18
 */
@RestController
@Api(tags = "评论模块")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @ApiOperation(value = "查询评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "articleId", value = "文章id", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "current", value = "当前页码", required = true, dataType = "Long")
    })
    @GetMapping("/comments")
    private Result<PageDTO<CommentDTO>> listComments(Integer articleId, Long current) {
        return new Result(true, StatusConst.OK, "查询成功！", commentService.listComments(articleId, current));
    }

    @ApiOperation(value = "添加评论或回复")
    @PostMapping("/comments")
    private Result saveComment(@Valid @RequestBody CommentVO commentVO) {
        commentService.saveComment(commentVO);
        return new Result(true, StatusConst.OK, "评论成功！");
    }

    @ApiOperation(value = "查询评论下的回复")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentId", value = "文章id", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "current", value = "当前页码", required = true, dataType = "Long")
    })
    @GetMapping("/comments/replies")
    private Result<List<ReplyDTO>> listRepliesByCommentId(Integer commentId, Long current) {
        return new Result(true, StatusConst.OK, "查询成功！", commentService.listRepliesByCommentId(commentId, current));
    }

    @ApiOperation(value = "评论点赞")
    @PostMapping("/comments/like")
    private Result saveCommentList(Integer commentId) {
        commentService.saveCommentLike(commentId);
        return new Result(true, StatusConst.OK, "点赞成功！");
    }

    @ApiOperation(value = "删除或恢复评论")
    @PutMapping("/admin/comments")
    private Result deleteComment(DeleteVO deleteVO) {
        commentService.updateCommentDelete(deleteVO);
        return new Result(true, StatusConst.OK, "操作成功！");
    }

    @ApiOperation(value = "物理删除评论")
    @DeleteMapping("/admin/comments")
    public Result deleteComments(@RequestBody List<Integer> commentIdList) {
        commentService.removeByIds(commentIdList);
        return new Result(true, StatusConst.OK, "操作成功！");
    }

    @ApiOperation(value = "查询后台评论")
    @GetMapping("/admin/comments")
    private Result<PageDTO<CommentBackDTO>> listCommentBackDTO(ConditionVO condition) {
        return new Result(true, StatusConst.OK, "查询成功", commentService.listCommentBackDTO(condition));
    }

}

