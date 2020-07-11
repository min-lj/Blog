package com.minzheng.blog.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.minzheng.blog.utils.UserUtil;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * 用户账号
 *
 * @author xiaojie
 * @since 2020-05-18
 */
@Data
@TableName("tb_user_auth")
public class UserAuth {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户信息id
     */
    private Integer userInfoId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 登录类型
     */
    private Integer loginType;

    /**
     * 用户登录ip
     */
    private String ipAddr;

    /**
     * ip来源
     */
    private String ipSource;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最近登录时间
     */
    private Date lastLoginTime;

    public UserAuth() {
    }

    public UserAuth(Integer userInfoId, String username, String password, Integer loginType) {
        this.userInfoId = userInfoId;
        this.username = username;
        this.password = password;
        this.loginType = loginType;
        this.createTime = new Date();
    }

    public UserAuth(Integer userInfoId, String username, String password, Integer loginType, String ipAddr, String ipSource) {
        this.userInfoId = userInfoId;
        this.username = username;
        this.password = password;
        this.loginType = loginType;
        this.ipAddr = ipAddr;
        this.ipSource = ipSource;
        this.createTime = new Date();
        this.lastLoginTime = new Date();
    }

    public UserAuth(String ipAddr, String ipSource) {
        this.id = UserUtil.getLoginUser().getId();
        this.ipAddr = ipAddr;
        this.ipSource = ipSource;
        this.lastLoginTime = new Date();
    }

    public UserAuth(String password) {
        this.id = UserUtil.getLoginUser().getId();
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }


}
