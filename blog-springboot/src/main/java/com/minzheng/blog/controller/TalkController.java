package com.minzheng.blog.controller;

import com.minzheng.blog.dto.TalkBackDTO;
import com.minzheng.blog.dto.TalkDTO;
import com.minzheng.blog.enums.FilePathEnum;
import com.minzheng.blog.service.TalkService;
import com.minzheng.blog.strategy.context.UploadStrategyContext;
import com.minzheng.blog.vo.ConditionVO;
import com.minzheng.blog.vo.PageResult;
import com.minzheng.blog.vo.Result;
import com.minzheng.blog.vo.TalkVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * 说说控制器
 *
 * @author yezhiqiu
 * @date 2022/01/23
 */
@Api(tags = "说说模块")
@RestController
public class TalkController {
    @Autowired
    private TalkService talkService;
    @Autowired
    private UploadStrategyContext uploadStrategyContext;

    /**
     * 查看首页说说
     *
     * @return {@link Result<String>}
     */
    @ApiOperation(value = "查看首页说说")
    @GetMapping("/home/talks")
    public Result<List<String>> listHomeTalks() {
        return Result.ok(talkService.listHomeTalks());
    }

    /**
     * 查看说说列表
     *
     * @return {@link Result<TalkDTO>}
     */
    @ApiOperation(value = "查看说说列表")
    @GetMapping("/talks")
    public Result<PageResult<TalkDTO>> listTalks() {
        return Result.ok(talkService.listTalks());
    }

    /**
     * 根据id查看说说
     *
     * @param talkId 说说id
     * @return {@link Result<TalkDTO>}
     */
    @ApiOperation(value = "根据id查看说说")
    @ApiImplicitParam(name = "talkId", value = "说说id", required = true, dataType = "Integer")
    @GetMapping("/talks/{talkId}")
    public Result<TalkDTO> getTalkById(@PathVariable("talkId") Integer talkId) {
        return Result.ok(talkService.getTalkById(talkId));
    }

    /**
     * 点赞说说
     *
     * @param talkId 说说id
     * @return {@link Result<>}
     */
    @ApiOperation(value = "点赞说说")
    @ApiImplicitParam(name = "talkId", value = "说说id", required = true, dataType = "Integer")
    @PostMapping("/talks/{talkId}/like")
    public Result<?> saveTalkLike(@PathVariable("talkId") Integer talkId) {
        talkService.saveTalkLike(talkId);
        return Result.ok();
    }

    /**
     * 上传说说图片
     *
     * @param file 文件
     * @return {@link Result<String>} 说说图片地址
     */
    @ApiOperation(value = "上传说说图片")
    @ApiImplicitParam(name = "file", value = "说说图片", required = true, dataType = "MultipartFile")
    @PostMapping("/admin/talks/images")
    public Result<String> saveTalkImages(MultipartFile file) {
        return Result.ok(uploadStrategyContext.executeUploadStrategy(file, FilePathEnum.TALK.getPath()));
    }

    /**
     * 保存或修改说说
     *
     * @param talkVO 说说信息
     * @return {@link Result<>}
     */
    @ApiOperation(value = "保存或修改说说")
    @PostMapping("/admin/talks")
    public Result<?> saveOrUpdateTalk(@Valid @RequestBody TalkVO talkVO) {
        talkService.saveOrUpdateTalk(talkVO);
        return Result.ok();
    }

    /**
     * 删除说说
     *
     * @param talkIdList 说说id列表
     * @return {@link Result<>}
     */
    @ApiOperation(value = "删除说说")
    @DeleteMapping("/admin/talks")
    public Result<?> deleteTalks(@RequestBody List<Integer> talkIdList) {
        talkService.deleteTalks(talkIdList);
        return Result.ok();
    }

    /**
     * 查看后台说说
     *
     * @param conditionVO 条件
     * @return {@link Result<TalkBackDTO>} 说说列表
     */
    @ApiOperation(value = "查看后台说说")
    @GetMapping("/admin/talks")
    public Result<PageResult<TalkBackDTO>> listBackTalks(ConditionVO conditionVO) {
        return Result.ok(talkService.listBackTalks(conditionVO));
    }

    /**
     * 根据id查看后台说说
     *
     * @param talkId 说说id
     * @return {@link Result<TalkDTO>}
     */
    @ApiOperation(value = "根据id查看后台说说")
    @ApiImplicitParam(name = "talkId", value = "说说id", required = true, dataType = "Integer")
    @GetMapping("/admin/talks/{talkId}")
    public Result<TalkBackDTO> getBackTalkById(@PathVariable("talkId") Integer talkId) {
        return Result.ok(talkService.getBackTalkById(talkId));
    }


}
