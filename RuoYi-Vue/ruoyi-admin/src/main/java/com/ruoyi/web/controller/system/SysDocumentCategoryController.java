package com.ruoyi.web.controller.system;

import java.util.List;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysDocumentCategory;
import com.ruoyi.system.service.ISysDocumentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/document/category")
public class SysDocumentCategoryController extends BaseController {

    @Autowired
    private ISysDocumentCategoryService categoryService;

    @GetMapping("/list")
    public AjaxResult list(SysDocumentCategory category) {
        startPage();
        List<SysDocumentCategory> list = categoryService.selectSysDocumentCategoryList(category);
        return AjaxResult.success(getDataTable(list));
    }

    @GetMapping("/{categoryId}")
    public AjaxResult getInfo(@PathVariable Long categoryId) {
        return AjaxResult.success(categoryService.selectSysDocumentCategoryById(categoryId));
    }

    @PostMapping
    public AjaxResult add(@RequestBody SysDocumentCategory category) {
        return AjaxResult.success(categoryService.insertSysDocumentCategory(category));
    }

    @PutMapping
    public AjaxResult edit(@RequestBody SysDocumentCategory category) {
        return AjaxResult.success(categoryService.updateSysDocumentCategory(category));
    }

    @DeleteMapping("/{categoryIds}")
    public AjaxResult remove(@PathVariable Long[] categoryIds) {
        return AjaxResult.success(categoryService.deleteSysDocumentCategoryByIds(categoryIds));
    }

    @Anonymous
    @GetMapping("/sync")
    public AjaxResult syncCategory() {
        List<SysDocumentCategory> list = categoryService.selectAllSysDocumentCategories();
        return AjaxResult.success(list);
    }
}
