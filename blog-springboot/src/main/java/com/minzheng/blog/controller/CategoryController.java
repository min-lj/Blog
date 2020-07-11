package com.minzheng.blog.controller;


import com.minzheng.blog.dto.ArticlePreviewListDTO;
import com.minzheng.blog.dto.CategoryDTO;
import com.minzheng.blog.dto.PageDTO;
import com.minzheng.blog.entity.Category;
import com.minzheng.blog.exception.ServeException;
import com.minzheng.blog.service.ArticleService;
import com.minzheng.blog.service.CategoryService;
import com.minzheng.blog.vo.CategoryVO;
import com.minzheng.blog.vo.ConditionVO;
import com.minzheng.blog.vo.Result;
import com.minzheng.blog.constant.StatusConst;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 *
 * @author xiaojie
 * @since 2020-05-18
 */
@Api(tags = "分类模块")
@RestController
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ArticleService articleService;

    @ApiOperation(value = "查看分类列表")
    @GetMapping("/categories")
    private Result<PageDTO<CategoryDTO>> listCategories() {
        return new Result(true, StatusConst.OK, "查询成功", categoryService.listCategories());
    }

    @ApiOperation(value = "查看后台分类列表")
    @GetMapping("/admin/categories")
    private Result<PageDTO<Category>> listCategoryBackDTO(ConditionVO condition) {
        return new Result(true, StatusConst.OK, "查询成功", categoryService.listCategoryBackDTO(condition));
    }

    @ApiOperation(value = "添加或修改分类")
    @PostMapping("/admin/categories")
    private Result saveOrUpdateCategory(@Valid @RequestBody CategoryVO categoryVO) {
        categoryService.saveOrUpdate(new Category(categoryVO));
        return new Result(true, StatusConst.OK, "操作成功");
    }

    @ApiOperation(value = "删除分类")
    @DeleteMapping("/admin/categories")
    private Result deleteCategories(@RequestBody List<Integer> categoryIdList) {
        categoryService.deleteCategory(categoryIdList);
        return new Result(true, StatusConst.OK, "删除成功");
    }

    @ApiOperation(value = "查看分类下对应的文章")
    @GetMapping("/categories/{categoryId}")
    private Result<ArticlePreviewListDTO> listArticlesByCategoryId(@PathVariable("categoryId") Integer categoryId, Long current) {
        return new Result(true, StatusConst.OK, "查询成功", articleService.listArticlesByCondition(new ConditionVO(current, categoryId)));
    }

}

