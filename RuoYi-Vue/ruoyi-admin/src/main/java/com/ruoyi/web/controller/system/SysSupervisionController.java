package com.ruoyi.web.controller.system;

import java.util.List;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysSupervisionCategory;
import com.ruoyi.system.domain.SysSupervisionItem;
import com.ruoyi.system.domain.SysSupervisionLanguageLink;
import com.ruoyi.system.domain.SysSupervisionRegulationLink;
import com.ruoyi.system.service.ISysSupervisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 监管事项移动端API
 */
@RestController
@RequestMapping("/api/supervision")
public class SysSupervisionController extends BaseController {

    @Autowired
    private ISysSupervisionService sysSupervisionService;

    /**
     * 获取监管事项首页数据（分类列表）
     */
    @Anonymous
    @GetMapping("/home")
    public AjaxResult getHomeData() {
        List<SysSupervisionCategory> categories = sysSupervisionService.selectAllCategories();
        List<SysSupervisionItem> topItems = sysSupervisionService.selectTopLevelItems();
        return AjaxResult.success()
                .put("categories", categories)
                .put("topItems", topItems);
    }

    /**
     * 获取监管类型列表
     */
    @Anonymous
    @GetMapping("/category")
    public AjaxResult listCategory(SysSupervisionCategory category) {
        List<SysSupervisionCategory> list = sysSupervisionService.selectSysSupervisionCategoryList(category);
        return AjaxResult.success(list);
    }

    /**
     * 获取所有启用的监管类型
     */
    @Anonymous
    @GetMapping("/category/all")
    public AjaxResult listAllCategories() {
        List<SysSupervisionCategory> list = sysSupervisionService.selectAllCategories();
        return AjaxResult.success(list);
    }

    /**
     * 获取监管子项列表
     */
    @Anonymous
    @GetMapping("/item-list")
    public AjaxResult listItems(SysSupervisionItem item) {
        List<SysSupervisionItem> list = sysSupervisionService.selectSupervisionItemList(item);
        return AjaxResult.success(list);
    }

    /**
     * 根据父级ID获取子事项
     */
    @Anonymous
    @GetMapping("/item-children/{parentId}")
    public AjaxResult listItemsByParentId(@PathVariable("parentId") Long parentId) {
        List<SysSupervisionItem> list = sysSupervisionService.selectItemsByParentId(parentId);
        return AjaxResult.success(list);
    }

    /**
     * 根据监管类型获取事项列表
     */
    @Anonymous
    @GetMapping("/item-list/category/{categoryId}")
    public AjaxResult listItemsByCategoryId(@PathVariable("categoryId") Long categoryId) {
        List<SysSupervisionItem> list = sysSupervisionService.selectItemsByCategoryId(categoryId);
        return AjaxResult.success(list);
    }

    /**
     * 获取监管事项详情
     */
    @Anonymous
    @GetMapping("/item-detail/{itemId}")
    public AjaxResult getItemDetail(@PathVariable("itemId") Long itemId) {
        SysSupervisionItem item = sysSupervisionService.selectSysSupervisionItemById(itemId);
        List<SysSupervisionLanguageLink> languageLinks = sysSupervisionService.selectLanguageLinksByItemId(itemId);
        List<SysSupervisionRegulationLink> regulationLinks = sysSupervisionService.selectRegulationLinksByItemId(itemId);
        return AjaxResult.success()
                .put("item", item)
                .put("languageLinks", languageLinks)
                .put("regulationLinks", regulationLinks);
    }

    /**
     * 搜索监管事项
     */
    @Anonymous
    @GetMapping("/search")
    public AjaxResult searchItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        List<SysSupervisionItem> list = sysSupervisionService.searchItems(keyword, categoryId);
        return AjaxResult.success(list);
    }

    /**
     * 获取监管事项关联的规范用语
     */
    @Anonymous
    @GetMapping("/language-link/{itemId}")
    public AjaxResult getLanguageLinks(@PathVariable("itemId") Long itemId) {
        List<SysSupervisionLanguageLink> list = sysSupervisionService.selectLanguageLinksByItemId(itemId);
        return AjaxResult.success(list);
    }

    /**
     * 获取监管事项关联的法律法规
     */
    @Anonymous
    @GetMapping("/regulation-link/{itemId}")
    public AjaxResult getRegulationLinks(@PathVariable("itemId") Long itemId) {
        List<SysSupervisionRegulationLink> list = sysSupervisionService.selectRegulationLinksByItemId(itemId);
        return AjaxResult.success(list);
    }
}
