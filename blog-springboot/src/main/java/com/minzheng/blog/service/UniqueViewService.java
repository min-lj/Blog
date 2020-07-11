package com.minzheng.blog.service;

import com.minzheng.blog.entity.UniqueView;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 * @author xiaojie
 * @since 2020-05-18
 */
public interface UniqueViewService extends IService<UniqueView> {

    /**
     * 统计每日用户量
     */
    void saveUniqueView();

}
