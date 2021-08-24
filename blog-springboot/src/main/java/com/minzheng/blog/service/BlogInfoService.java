package com.minzheng.blog.service;

import com.minzheng.blog.dto.BlogBackInfoDTO;
import com.minzheng.blog.dto.BlogHomeInfoDTO;
import com.minzheng.blog.vo.BlogInfoVO;
import com.minzheng.blog.vo.WebsiteConfigVO;

/**
 * 博客信息服务
 *
 * @author yezhiqiu
 * @date 2021/08/09
 */
public interface BlogInfoService {

    /**
     * 获取首页数据
     *
     * @return 博客首页信息
     */
    BlogHomeInfoDTO getBlogHomeInfo();

    /**
     * 获取后台首页数据
     *
     * @return 博客后台信息
     */
    BlogBackInfoDTO getBlogBackInfo();

    /**
     * 保存或更新网站配置
     *
     * @param websiteConfigVO 网站配置
     */
    void updateWebsiteConfig(WebsiteConfigVO websiteConfigVO);

    /**
     * 获取网站配置
     *
     * @return {@link WebsiteConfigVO} 网站配置
     */
    WebsiteConfigVO getWebsiteConfig();

    /**
     * 获取关于我内容
     *
     * @return 关于我内容
     */
    String getAbout();

    /**
     * 修改关于我内容
     *
     * @param blogInfoVO 博客信息
     */
    void updateAbout(BlogInfoVO blogInfoVO);

    /**
     * 上传访客信息
     */
    void report();

}
