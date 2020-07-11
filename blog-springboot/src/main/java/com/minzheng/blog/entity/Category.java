package com.minzheng.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.minzheng.blog.vo.CategoryVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 分类
 *
 * @author xiaojie
 * @since 2020-05-18
 */
@Data
@TableName("tb_category")
public class Category {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 分类名
     */
    private String categoryName;

    /**
     * 创建时间
     */
    private Date createTime;

    public Category(CategoryVO categoryVO) {
        this.id = categoryVO.getId();
        this.categoryName = categoryVO.getCategoryName();
        this.createTime = this.id == null ? new Date() : null;
    }

    public Category() {
    }

}
