package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.RegulatoryCategoryBind;
import com.ruoyi.system.domain.RegulatoryMatter;
import com.ruoyi.system.domain.RegulatoryMatterItem;
import com.ruoyi.system.service.IRegulatoryCategoryBindService;
import com.ruoyi.system.service.IRegulatoryMatterItemService;
import com.ruoyi.system.service.IRegulatoryMatterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Android 端监管事项接口（匿名可访问）
 */
@RestController
@RequestMapping("/app/regulatory")
public class AppRegulatoryController extends BaseController {

    @Autowired
    private IRegulatoryMatterService matterService;

    @Autowired
    private IRegulatoryMatterItemService itemService;

    @Autowired
    private IRegulatoryCategoryBindService categoryBindService;

    /**
     * 获取监管事项列表
     */
    @Anonymous
    @GetMapping("/matter/list")
    public AjaxResult listMatter() {
        return AjaxResult.success(matterService.selectRegulatoryMatterList(new RegulatoryMatter()));
    }

    /**
     * 获取监管事项详情
     */
    @Anonymous
    @GetMapping("/matter/{matterId}")
    public AjaxResult getMatter(@PathVariable Long matterId) {
        return AjaxResult.success(matterService.selectRegulatoryMatterById(matterId));
    }

    /**
     * 获取监管事项明细列表
     */
    @Anonymous
    @GetMapping("/matter/{matterId}/item/list")
    public AjaxResult listItemByMatter(@PathVariable Long matterId) {
        RegulatoryMatterItem item = new RegulatoryMatterItem();
        item.setRegulatoryMatterId(matterId);
        return AjaxResult.success(itemService.selectRegulatoryMatterItemList(item));
    }

    /**
     * 获取行业分类监管事项关联列表
     */
    @Anonymous
    @GetMapping("/categorybind/list")
    public AjaxResult listCategoryBind() {
        return AjaxResult.success(categoryBindService.selectRegulatoryCategoryBindList(new RegulatoryCategoryBind()));
    }
}
