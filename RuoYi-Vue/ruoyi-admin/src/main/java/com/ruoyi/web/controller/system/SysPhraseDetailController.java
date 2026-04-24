package com.ruoyi.web.controller.system;

import java.util.List;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysPhraseDetail;
import com.ruoyi.system.service.ISysPhraseDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 规范用语项明细管理
 */
@RestController
@RequestMapping("/system/phrase/detail")
public class SysPhraseDetailController extends BaseController
{
    @Autowired
    private ISysPhraseDetailService phraseDetailService;

    /**
     * 获取明细列表
     */
    @Anonymous
    @GetMapping("/list")
    public AjaxResult list(SysPhraseDetail phraseDetail)
    {
        List<SysPhraseDetail> list = phraseDetailService.selectSysPhraseDetailList(phraseDetail);
        return AjaxResult.success(list);
    }

    /**
     * 按项获取明细列表
     */
    @Anonymous
    @GetMapping("/listByItem/{itemId}")
    public AjaxResult listByItem(@PathVariable("itemId") Long itemId)
    {
        List<SysPhraseDetail> list = phraseDetailService.selectSysPhraseDetailByItemId(itemId);
        return AjaxResult.success(list);
    }

    /**
     * 获取明细详情
     */
    @Anonymous
    @GetMapping("/{detailId}")
    public AjaxResult getInfo(@PathVariable("detailId") Long detailId)
    {
        return AjaxResult.success(phraseDetailService.selectSysPhraseDetailByDetailId(detailId));
    }

    /**
     * 新增明细
     */
    @Anonymous
    @PostMapping
    public AjaxResult add(@RequestBody SysPhraseDetail phraseDetail)
    {
        return AjaxResult.success(phraseDetailService.insertSysPhraseDetail(phraseDetail));
    }

    /**
     * 修改明细
     */
    @Anonymous
    @PutMapping
    public AjaxResult edit(@RequestBody SysPhraseDetail phraseDetail)
    {
        return AjaxResult.success(phraseDetailService.updateSysPhraseDetail(phraseDetail));
    }

    /**
     * 删除明细
     */
    @Anonymous
    @DeleteMapping("/{detailIds}")
    public AjaxResult remove(@PathVariable Long[] detailIds)
    {
        return AjaxResult.success(phraseDetailService.deleteSysPhraseDetailByDetailIds(detailIds));
    }
}
