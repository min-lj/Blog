package com.minzheng.blog.controller;


import com.minzheng.blog.constant.StatusConst;
import com.minzheng.blog.dto.ArticlePreviewListDTO;
import com.minzheng.blog.dto.PageDTO;
import com.minzheng.blog.dto.TagDTO;
import com.minzheng.blog.entity.Tag;
import com.minzheng.blog.exception.ServeException;
import com.minzheng.blog.service.ArticleService;
import com.minzheng.blog.service.TagService;
import com.minzheng.blog.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 *
 * @author xiaojie
 * @since 2020-05-18
 */
@Api(tags = "标签模块")
@RestController
public class TagController {
    @Autowired
    private TagService tagService;
    @Autowired
    private ArticleService articleService;

    @ApiOperation(value = "查看标签列表")
    @GetMapping("/tags")
    private Result<PageDTO<TagDTO>> listTags(Long current) {
        return new Result(true, StatusConst.OK, "查询成功", tagService.listTags());
    }

    @ApiOperation(value = "查看分类下对应的文章")
    @GetMapping("/tags/{tagId}")
    private Result<ArticlePreviewListDTO> listArticlesByCategoryId(@PathVariable("tagId") Integer tagId, Long current) {
        return new Result(true, StatusConst.OK, "查询成功", articleService.listArticlesByCondition(new ConditionVO(tagId, current)));
    }

    @ApiOperation(value = "查看后台标签列表")
    @GetMapping("/admin/tags")
    private Result<PageDTO<Tag>> listTagBackDTO(ConditionVO condition) {
        return new Result(true, StatusConst.OK, "查询成功", tagService.listTagBackDTO(condition));
    }

    @ApiOperation(value = "添加或修改标签")
    @PostMapping("/admin/tags")
    private Result saveOrUpdateTag(@Valid @RequestBody TagVO tagVO) {
        tagService.saveOrUpdate(new Tag(tagVO));
        return new Result(true, StatusConst.OK, "操作成功");
    }

    @ApiOperation(value = "删除标签")
    @DeleteMapping("/admin/tags")
    private Result deleteTag(@RequestBody List<Integer> tagIdList) {
        tagService.deleteTag(tagIdList);
        return new Result(true, StatusConst.OK, "删除成功");
    }


}

