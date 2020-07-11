package com.minzheng.blog.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * @author xiaojie
 * @since 2020-05-18
 */
@Data
@ApiModel(description = "用户权限")
public class UserRoleVO {
    /**
     * 用户昵称
     */
    @NotNull(message = "id不能为空")
    @ApiModelProperty(name = "userInfoId", value = "用户信息id", dataType = "Integer")
    private Integer userInfoId;

    /**
     * 用户昵称
     */
    @NotBlank(message = "昵称不能为空")
    @ApiModelProperty(name = "nickname", value = "昵称", dataType = "String")
    private String nickname;

    /**
     * 用户权限
     */
    @NotBlank(message = "用户权限不能为空")
    @ApiModelProperty(name = "userRole", value = "权限", dataType = "String")
    private String userRole;

}
