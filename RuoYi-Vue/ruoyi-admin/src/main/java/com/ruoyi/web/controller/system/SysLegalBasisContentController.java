package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysLegalBasisContent;
import com.ruoyi.system.service.ISysLegalBasisContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/legalBasisContent")
public class SysLegalBasisContentController extends BaseController {

    @Autowired
    private ISysLegalBasisContentService sysLegalBasisContentService;

    /**
     * 获取某定性依据的所有内容
     */
    @PreAuthorize("@ss.hasPermi('system:legalBasis:list')")
    @GetMapping("/list/{basisId}")
    public TableDataInfo list(@PathVariable Long basisId) {
        List<SysLegalBasisContent> list = sysLegalBasisContentService.selectSysLegalBasisContentByBasisId(basisId);
        return getDataTable(list);
    }

    /**
     * 新增内容行
     */
    @PreAuthorize("@ss.hasPermi('system:legalBasis:add')")
    @Log(title = "定性依据内容", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysLegalBasisContent sysLegalBasisContent) {
        return toAjax(sysLegalBasisContentService.insertSysLegalBasisContent(sysLegalBasisContent));
    }

    /**
     * 修改内容行
     */
    @PreAuthorize("@ss.hasPermi('system:legalBasis:edit')")
    @Log(title = "定性依据内容", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysLegalBasisContent sysLegalBasisContent) {
        return toAjax(sysLegalBasisContentService.updateSysLegalBasisContent(sysLegalBasisContent));
    }

    /**
     * 删除内容行
     */
    @PreAuthorize("@ss.hasPermi('system:legalBasis:remove')")
    @Log(title = "定性依据内容", businessType = BusinessType.DELETE)
    @DeleteMapping("/{contentIds}")
    public AjaxResult remove(@PathVariable Long[] contentIds) {
        return toAjax(sysLegalBasisContentService.deleteSysLegalBasisContentByIds(contentIds));
    }
}
