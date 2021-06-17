package com.minzheng.blog.constant;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;

/**
 * 公共常量
 *
 * @author: yezhiqiu
 * @date: 2021-01-29
 **/
public class CommonConst {

    /**
     * 否
     */
    public static final int FALSE = 0;

    /**
     * 是
     */
    public static final int TRUE = 1;

    /**
     * 博主id
     */
    public static final int BLOGGER_ID = 1;

    /**
     * 默认用户昵称
     */
    public static final String DEFAULT_NICKNAME = "用户" + IdWorker.getId();

    /**
     * 默认用户头像
     */
    public static final String DEFAULT_AVATAR = "https://www.static.talkxj.com/avatar/user.png";

    /**
     * 浏览文章集合
     */
    public static String ARTICLE_SET = "articleSet";

    /**
     * 前端组件名
     */
    public static String COMPONENT = "Layout";

    /**
     * 网站域名
     */
    public static final String URL = "https://www.talkxj.com";

    /**
     * 用户ip
     */
    public static final String IP = "ip";

    /**
     * 文章页面路径
     */
    public static final String ARTICLE_PATH = "/articles/";

    /**
     * 友联页面路径
     */
    public static final String LINK_PATH = "/links";

}
