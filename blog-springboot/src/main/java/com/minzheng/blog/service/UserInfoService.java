package com.minzheng.blog.service;

import com.minzheng.blog.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.minzheng.blog.vo.DeleteVO;
import com.minzheng.blog.vo.UserInfoVO;
import com.minzheng.blog.vo.UserRoleVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author xiaojie
 * @since 2020-05-18
 */
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 修改用户资料
     *
     * @param userInfoVO 用户资料
     */
    void updateUserInfo(UserInfoVO userInfoVO);

    /**
     * 修改用户头像
     *
     * @param file 头像图片
     * @return 头像OSS地址
     */
    String updateUserAvatar(MultipartFile file);

    /**
     * 修改用户权限
     *
     * @param userRoleVO 用户权限
     */
    void updateUserRole(UserRoleVO userRoleVO);

    /**
     * 修改用户禁言状态
     *
     * @param userInfoId 用户信息id
     * @param isSilence  禁言状态
     */
    void updateUserSilence(Integer userInfoId, Integer isSilence);

}
