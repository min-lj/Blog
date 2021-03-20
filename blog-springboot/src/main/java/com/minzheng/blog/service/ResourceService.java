package com.minzheng.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minzheng.blog.dto.ResourceDTO;
import com.minzheng.blog.dto.labelOptionDTO;
import com.minzheng.blog.entity.Resource;
import com.minzheng.blog.vo.ResourceVO;

import java.util.List;


/**
 * @author: yezhiqiu
 * @date: 2020-12-27
 **/
public interface ResourceService extends IService<Resource> {

    /**
     * 导入swagger权限
     */
    void importSwagger();

    /**
     * 添加或修改资源
     * @param resourceVO
     */
    void saveOrUpdateResource(ResourceVO resourceVO);

    /**
     * 查看资源列表
     *
     * @return 资源列表
     */
    List<ResourceDTO> listResources();

    /**
     * 查看资源选项
     * @return 资源选项
     */
    List<labelOptionDTO> listResourceOption();

}
