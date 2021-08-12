package com.minzheng.blog.dao;

import com.minzheng.blog.dto.PhotoAlbumBackDTO;
import com.minzheng.blog.entity.Photo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minzheng.blog.vo.ConditionVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 照片映射器
 *
 * @author yezhiqiu
 * @date 2021/08/04
 */
@Repository
public interface PhotoDao extends BaseMapper<Photo> {



}




