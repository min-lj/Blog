package com.minzheng.blog.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: yezhiqiu
 * @date: 2021-04-16
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailVO {

    /**
     * 用户Id
     */
    private Integer userId;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 验证码
     */
    private String code;

}
