package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysIndustryCategory;
import com.ruoyi.system.service.ISysIndustryCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Android 端行业分类接口（匿名可访问）
 */
@RestController
@RequestMapping("/app/category")
public class AppCategoryController extends BaseController {

    @Autowired
    private ISysIndustryCategoryService categoryService;

    /**
     * 获取行业分类列表
     */
    @Anonymous
    @GetMapping("/list")
    public AjaxResult list() {
        List<SysIndustryCategory> list = categoryService.selectSysIndustryCategoryList(new SysIndustryCategory());
        return AjaxResult.success(list);
    }

    /**
     * 获取行业分类详情
     */
    @Anonymous
    @GetMapping("/{categoryId}")
    public AjaxResult getInfo(@PathVariable Long categoryId) {
        SysIndustryCategory category = categoryService.selectSysIndustryCategoryByCategoryId(categoryId);
        return AjaxResult.success(category);
    }
}
