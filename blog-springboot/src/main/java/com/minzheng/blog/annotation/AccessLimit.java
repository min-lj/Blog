package com.minzheng.blog.annotation;

import java.lang.annotation.*;

/**
 * @author hnz
 * @date 2022/3/23 11:16
 * @description redis接口限流
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessLimit {

    /**
     * @return 单位时间（秒）
     */
    int seconds();

    /**
     * @return 单位时间最大请求次数
     */
    int maxCount();
}
