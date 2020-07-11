package com.minzheng.blog.dao;

import com.minzheng.blog.entity.UniqueView;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author xiaojie
 * @since 2020-05-18
 */
@Repository
public interface UniqueViewDao extends BaseMapper<UniqueView> {

    /**
     * 获取一周用户量
     * @return 用户量集合
     */
    List<Integer> listUniqueViews();
}
