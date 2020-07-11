package com.minzheng.blog.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 查询条件
 *
 * @author 11921
 */
@Data
@ApiModel(description = "查询条件")
public class ConditionVO {

    /**
     * 分类id
     */
    @ApiModelProperty(name = "categoryId", value = "分类id", dataType = "Integer")
    private Integer categoryId;

    /**
     * 标签id
     */
    @ApiModelProperty(name = "tagId", value = "标签id", dataType = "Integer")
    private Integer tagId;

    /**
     * 当前页码
     */
    @ApiModelProperty(name = "current", value = "当前页码", required = true, dataType = "Long")
    private Long current;

    /**
     * 显示数量
     */
    @ApiModelProperty(name = "size", value = "显示数量", required = true, dataType = "Long")
    private Long size;

    /**
     * 搜索内容
     */
    @ApiModelProperty(name = "keywords", value = "搜索内容", required = true, dataType = "String")
    private String keywords;

    /**
     * 状态值
     */
    @ApiModelProperty(name = "isDelete", value = "是否删除", dataType = "Integer")
    private Integer isDelete;
    public ConditionVO(Integer tagId, Long current) {
        this.tagId = tagId;
        this.current = current;
    }

    /**
     * 是否为草稿
     */
    @ApiModelProperty(name = "isDraft", value = "草稿状态", dataType = "Integer")
    private Integer isDraft;

    public ConditionVO(Long current, Integer categoryId) {
        this.current = current;
        this.categoryId = categoryId;
    }

    public ConditionVO(Long current, String keywords) {
        this.current = current;
        this.keywords = keywords;
    }

    public ConditionVO() {
    }

}
