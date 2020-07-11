package com.minzheng.blog.service.impl;

import com.minzheng.blog.entity.UniqueView;
import com.minzheng.blog.dao.UniqueViewDao;
import com.minzheng.blog.service.UniqueViewService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;

/**
 *
 * @author xiaojie
 * @since 2020-05-18
 */
@Service
public class UniqueViewServiceImpl extends ServiceImpl<UniqueViewDao, UniqueView> implements UniqueViewService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UniqueViewDao uniqueViewDao;

    @Scheduled(cron = " 0 0 0 * * ?")
    @Override
    public void saveUniqueView() {
        //获取每天用户量
        Long count = redisTemplate.boundSetOps("ip_set").size();
        //获取昨天日期
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, -24);
        uniqueViewDao.insert(new UniqueView(calendar.getTime(), count.intValue()));
    }

}
