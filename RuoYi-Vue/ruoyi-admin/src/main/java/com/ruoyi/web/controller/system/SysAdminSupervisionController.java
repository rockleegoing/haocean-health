package com.ruoyi.web.controller.system;

import java.util.List;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysSupervisionCategory;
import com.ruoyi.system.domain.SysSupervisionItem;
import com.ruoyi.system.domain.SysSupervisionLanguageLink;
import com.ruoyi.system.domain.SysSupervisionRegulationLink;
import com.ruoyi.system.service.ISysSupervisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 监管事项后台管理API
 */
@RestController
@RequestMapping("/api/admin/supervision")
public class SysAdminSupervisionController extends BaseController {

    @Autowired
    private ISysSupervisionService sysSupervisionService;

    // ==================== 监管事项管理 ====================

    /**
     * 获取监管事项列表
     */
    @PreAuthorize("@ss.hasPermi('system:supervision:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysSupervisionItem sysSupervisionItem) {
        startPage();
        List<SysSupervisionItem> list = sysSupervisionService.selectSupervisionItemList(sysSupervisionItem);
        return getDataTable(list);
    }

    /**
     * 获取监管事项详情
     */
    @PreAuthorize("@ss.hasPermi('system:supervision:query')")
    @GetMapping("/detail/{itemId}")
    public AjaxResult getDetail(@PathVariable("itemId") Long itemId) {
        SysSupervisionItem item = sysSupervisionService.selectSysSupervisionItemById(itemId);
        List<SysSupervisionLanguageLink> languageLinks = sysSupervisionService.selectLanguageLinksByItemId(itemId);
        List<SysSupervisionRegulationLink> regulationLinks = sysSupervisionService.selectRegulationLinksByItemId(itemId);
        return AjaxResult.success()
                .put("item", item)
                .put("languageLinks", languageLinks)
                .put("regulationLinks", regulationLinks);
    }

    /**
     * 添加监管事项
     */
    @PreAuthorize("@ss.hasPermi('system:supervision:add')")
    @Log(title = "监管事项", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysSupervisionItem sysSupervisionItem) {
        return AjaxResult.success(sysSupervisionService.insertSysSupervisionItem(sysSupervisionItem));
    }

    /**
     * 修改监管事项
     */
    @PreAuthorize("@ss.hasPermi('system:supervision:edit')")
    @Log(title = "监管事项", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysSupervisionItem sysSupervisionItem) {
        return AjaxResult.success(sysSupervisionService.updateSysSupervisionItem(sysSupervisionItem));
    }

    /**
     * 删除监管事项
     */
    @PreAuthorize("@ss.hasPermi('system:supervision:remove')")
    @Log(title = "监管事项", businessType = BusinessType.DELETE)
    @DeleteMapping("/{itemIds}")
    public AjaxResult remove(@PathVariable Long[] itemIds) {
        return AjaxResult.success(sysSupervisionService.deleteSysSupervisionItemByIds(itemIds));
    }

    // ==================== 监管类型管理 ====================

    /**
     * 获取监管类型列表
     */
    @PreAuthorize("@ss.hasPermi('system:supervision:list')")
    @GetMapping("/category/list")
    public TableDataInfo listCategory(SysSupervisionCategory category) {
        startPage();
        List<SysSupervisionCategory> list = sysSupervisionService.selectSysSupervisionCategoryList(category);
        return getDataTable(list);
    }

    /**
     * 获取监管类型详情
     */
    @PreAuthorize("@ss.hasPermi('system:supervision:query')")
    @GetMapping("/category/detail/{categoryId}")
    public AjaxResult getCategoryDetail(@PathVariable("categoryId") Long categoryId) {
        return AjaxResult.success(sysSupervisionService.selectSysSupervisionCategoryById(categoryId));
    }

    /**
     * 添加监管类型
     */
    @PreAuthorize("@ss.hasPermi('system:supervision:add')")
    @Log(title = "监管类型", businessType = BusinessType.INSERT)
    @PostMapping("/category")
    public AjaxResult addCategory(@RequestBody SysSupervisionCategory category) {
        return AjaxResult.success(sysSupervisionService.insertSysSupervisionCategory(category));
    }

    /**
     * 修改监管类型
     */
    @PreAuthorize("@ss.hasPermi('system:supervision:edit')")
    @Log(title = "监管类型", businessType = BusinessType.UPDATE)
    @PutMapping("/category")
    public AjaxResult editCategory(@RequestBody SysSupervisionCategory category) {
        return AjaxResult.success(sysSupervisionService.updateSysSupervisionCategory(category));
    }

    /**
     * 删除监管类型
     */
    @PreAuthorize("@ss.hasPermi('system:supervision:remove')")
    @Log(title = "监管类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/category/{categoryIds}")
    public AjaxResult removeCategory(@PathVariable Long[] categoryIds) {
        int result = 0;
        for (Long categoryId : categoryIds) {
            result += sysSupervisionService.deleteSysSupervisionCategoryById(categoryId);
        }
        return AjaxResult.success(result);
    }

    // ==================== 关联管理 ====================

    /**
     * 添加规范用语关联
     */
    @PreAuthorize("@ss.hasPermi('system:supervision:edit')")
    @Log(title = "监管事项关联", businessType = BusinessType.INSERT)
    @PostMapping("/language-link")
    public AjaxResult addLanguageLink(
            @RequestParam("itemId") Long itemId,
            @RequestParam("languageId") Long languageId) {
        return AjaxResult.success(sysSupervisionService.insertLanguageLink(itemId, languageId));
    }

    /**
     * 删除规范用语关联
     */
    @PreAuthorize("@ss.hasPermi('system:supervision:edit')")
    @Log(title = "监管事项关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/language-link/{linkId}")
    public AjaxResult removeLanguageLink(@PathVariable("linkId") Long linkId) {
        return AjaxResult.success(sysSupervisionService.deleteLanguageLink(linkId));
    }

    /**
     * 添加法律法规关联
     */
    @PreAuthorize("@ss.hasPermi('system:supervision:edit')")
    @Log(title = "监管事项关联", businessType = BusinessType.INSERT)
    @PostMapping("/regulation-link")
    public AjaxResult addRegulationLink(
            @RequestParam("itemId") Long itemId,
            @RequestParam("regulationId") Long regulationId) {
        return AjaxResult.success(sysSupervisionService.insertRegulationLink(itemId, regulationId));
    }

    /**
     * 删除法律法规关联
     */
    @PreAuthorize("@ss.hasPermi('system:supervision:edit')")
    @Log(title = "监管事项关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/regulation-link/{linkId}")
    public AjaxResult removeRegulationLink(@PathVariable("linkId") Long linkId) {
        return AjaxResult.success(sysSupervisionService.deleteRegulationLink(linkId));
    }
}
