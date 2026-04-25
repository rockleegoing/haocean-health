package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.SysIndustryCategory;
import com.ruoyi.system.service.ISysIndustryCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 行业分类管理
 */
@RestController
@RequestMapping("/system/category")
public class SysCategoryController extends BaseController {

    @Autowired
    private ISysIndustryCategoryService categoryService;

    /**
     * 获取行业分类列表
     */
    @Anonymous
    @GetMapping("/list")
    public TableDataInfo list() {
        startPage();
        List<SysIndustryCategory> list = categoryService.selectSysIndustryCategoryList(new SysIndustryCategory());
        return getDataTable(list);
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

    /**
     * 新增行业分类
     */
    @Anonymous
    @PostMapping
    public AjaxResult add(@RequestBody SysIndustryCategory category) {
        return AjaxResult.success(categoryService.insertSysIndustryCategory(category));
    }

    /**
     * 修改行业分类
     */
    @Anonymous
    @PutMapping
    public AjaxResult edit(@RequestBody SysIndustryCategory category) {
        return AjaxResult.success(categoryService.updateSysIndustryCategory(category));
    }

    /**
     * 删除行业分类
     */
    @Anonymous
    @DeleteMapping("/{categoryId}")
    public AjaxResult remove(@PathVariable Long categoryId) {
        return AjaxResult.success(categoryService.deleteSysIndustryCategoryByCategoryId(categoryId));
    }
}
