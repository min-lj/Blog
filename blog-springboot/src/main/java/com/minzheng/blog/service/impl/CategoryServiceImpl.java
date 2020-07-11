package com.minzheng.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minzheng.blog.dao.ArticleDao;
import com.minzheng.blog.dto.CategoryDTO;
import com.minzheng.blog.dto.PageDTO;
import com.minzheng.blog.entity.Article;
import com.minzheng.blog.entity.Category;
import com.minzheng.blog.dao.CategoryDao;
import com.minzheng.blog.exception.ServeException;
import com.minzheng.blog.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minzheng.blog.vo.ConditionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



/**
 *
 * @author xiaojie
 * @since 2020-05-18
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService {
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private ArticleDao articleDao;

    @Override
    public PageDTO<CategoryDTO> listCategories() {
        //获取分类列表
        List<CategoryDTO> categoryDTOList = categoryDao.listCategoryDTO();
        //获取分类数量
        Integer count = categoryDao.selectCount(null);
        return new PageDTO(categoryDTOList, count);
    }

    @Override
    public PageDTO<Category> listCategoryBackDTO(ConditionVO condition) {
        Page<Category> page = new Page<>(condition.getCurrent(), condition.getSize());
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "category_name", "create_time")
                .like(condition.getKeywords() != null, "category_name", condition.getKeywords());
        Page<Category> categoryPage = categoryDao.selectPage(page, queryWrapper);
        return new PageDTO(categoryPage.getRecords(), (int) categoryPage.getTotal());
    }

    @Transactional(rollbackFor = ServeException.class)
    @Override
    public void deleteCategory(List<Integer> categoryIdList) {
        //查询分类id下是否有文章
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id").in("category_id", categoryIdList);
        if (!articleDao.selectList(queryWrapper).isEmpty()) {
            throw new ServeException("删除失败，该分类下存在文章");
        }
        categoryDao.deleteBatchIds(categoryIdList);
    }

}
