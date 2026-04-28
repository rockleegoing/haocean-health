package com.ruoyi.web.controller.system;

import java.util.List;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.SysBasisChapterLink;
import com.ruoyi.system.service.ISysBasisChapterLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 依据关联管理
 */
@RestController
@RequestMapping("/system/basisLink")
public class SysBasisChapterLinkController extends BaseController {

    @Autowired
    private ISysBasisChapterLinkService sysBasisChapterLinkService;

    /**
     * 获取依据关联列表
     */
    @Anonymous
    @GetMapping("/list")
    public TableDataInfo list(SysBasisChapterLink sysBasisChapterLink) {
        startPage();
        List<SysBasisChapterLink> list = sysBasisChapterLinkService.selectSysBasisChapterLinkList(sysBasisChapterLink);
        return getDataTable(list);
    }

    /**
     * 获取依据关联详情
     */
    @Anonymous
    @GetMapping("/{linkId}")
    public AjaxResult getInfo(@PathVariable("linkId") Long linkId) {
        return AjaxResult.success(sysBasisChapterLinkService.selectSysBasisChapterLinkById(linkId));
    }

    /**
     * 根据章节ID获取关联列表
     */
    @Anonymous
    @GetMapping("/chapter/{chapterId}")
    public AjaxResult getListByChapterId(@PathVariable("chapterId") Long chapterId) {
        List<SysBasisChapterLink> list = sysBasisChapterLinkService.selectSysBasisChapterLinkByChapterId(chapterId);
        return AjaxResult.success(list);
    }

    /**
     * 根据章节ID和依据类型获取关联列表
     */
    @Anonymous
    @GetMapping("/chapter/{chapterId}/{basisType}")
    public AjaxResult getListByChapterIdAndType(@PathVariable("chapterId") Long chapterId, @PathVariable("basisType") String basisType) {
        List<SysBasisChapterLink> list = sysBasisChapterLinkService.selectSysBasisChapterLinkByChapterIdAndType(chapterId, basisType);
        return AjaxResult.success(list);
    }

    /**
     * 新增依据关联
     */
    @Anonymous
    @PostMapping
    public AjaxResult add(@RequestBody SysBasisChapterLink sysBasisChapterLink) {
        return AjaxResult.success(sysBasisChapterLinkService.insertSysBasisChapterLink(sysBasisChapterLink));
    }

    /**
     * 修改依据关联
     */
    @Anonymous
    @PutMapping
    public AjaxResult edit(@RequestBody SysBasisChapterLink sysBasisChapterLink) {
        return AjaxResult.success(sysBasisChapterLinkService.updateSysBasisChapterLink(sysBasisChapterLink));
    }

    /**
     * 删除依据关联
     */
    @Anonymous
    @DeleteMapping("/{linkIds}")
    public AjaxResult remove(@PathVariable Long[] linkIds) {
        return AjaxResult.success(sysBasisChapterLinkService.deleteSysBasisChapterLinkByIds(linkIds));
    }
}
