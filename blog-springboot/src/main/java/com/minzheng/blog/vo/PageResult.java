package com.minzheng.blog.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页对象
 *
 * @author yezhiqiu
 * @date 2021/08/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "分页对象")
public class PageResult<T> {

    /**
     * 分页列表
     */
    @ApiModelProperty(name = "recordList", value = "分页列表", required = true, dataType = "List<T>")
    private List<T> recordList;

    /**
     * 总数
     */
    @ApiModelProperty(name = "count", value = "总数", required = true, dataType = "Integer")
    private Integer count;

}
