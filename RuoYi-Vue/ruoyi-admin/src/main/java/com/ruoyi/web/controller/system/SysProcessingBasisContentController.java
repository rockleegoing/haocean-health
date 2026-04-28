package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysProcessingBasisContent;
import com.ruoyi.system.service.ISysProcessingBasisContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/processingBasisContent")
public class SysProcessingBasisContentController extends BaseController {

    @Autowired
    private ISysProcessingBasisContentService sysProcessingBasisContentService;

    /**
     * 获取某处理依据的所有内容
     */
    @PreAuthorize("@ss.hasPermi('system:processingBasis:list')")
    @GetMapping("/list/{basisId}")
    public TableDataInfo list(@PathVariable Long basisId) {
        List<SysProcessingBasisContent> list = sysProcessingBasisContentService.selectSysProcessingBasisContentByBasisId(basisId);
        return getDataTable(list);
    }

    /**
     * 新增内容行
     */
    @PreAuthorize("@ss.hasPermi('system:processingBasis:add')")
    @Log(title = "处理依据内容", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysProcessingBasisContent sysProcessingBasisContent) {
        return toAjax(sysProcessingBasisContentService.insertSysProcessingBasisContent(sysProcessingBasisContent));
    }

    /**
     * 修改内容行
     */
    @PreAuthorize("@ss.hasPermi('system:processingBasis:edit')")
    @Log(title = "处理依据内容", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysProcessingBasisContent sysProcessingBasisContent) {
        return toAjax(sysProcessingBasisContentService.updateSysProcessingBasisContent(sysProcessingBasisContent));
    }

    /**
     * 删除内容行
     */
    @PreAuthorize("@ss.hasPermi('system:processingBasis:remove')")
    @Log(title = "处理依据内容", businessType = BusinessType.DELETE)
    @DeleteMapping("/{contentIds}")
    public AjaxResult remove(@PathVariable Long[] contentIds) {
        return toAjax(sysProcessingBasisContentService.deleteSysProcessingBasisContentByIds(contentIds));
    }
}
