package com.ruoyi.web.controller.system;

import java.util.List;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.SysLegalBasis;
import com.ruoyi.system.service.ISysLegalBasisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 定性依据管理
 */
@RestController
@RequestMapping("/system/legalBasis")
public class SysLegalBasisController extends BaseController {

    @Autowired
    private ISysLegalBasisService sysLegalBasisService;

    /**
     * 获取定性依据列表
     */
    @Anonymous
    @GetMapping("/list")
    public TableDataInfo list(SysLegalBasis sysLegalBasis) {
        startPage();
        List<SysLegalBasis> list = sysLegalBasisService.selectSysLegalBasisList(sysLegalBasis);
        return getDataTable(list);
    }

    /**
     * 获取定性依据详情
     */
    @Anonymous
    @GetMapping("/{basisId}")
    public AjaxResult getInfo(@PathVariable("basisId") Long basisId) {
        return AjaxResult.success(sysLegalBasisService.selectLegalBasisDetail(basisId));
    }

    /**
     * 获取某法律法规关联的定性依据列表
     */
    @Anonymous
    @GetMapping("/regulation/{regulationId}")
    public AjaxResult getListByRegulationId(@PathVariable("regulationId") Long regulationId) {
        List<SysLegalBasis> list = sysLegalBasisService.selectSysLegalBasisListByRegulationId(regulationId);
        return AjaxResult.success(list);
    }

    /**
     * 新增定性依据
     */
    @Anonymous
    @PostMapping
    public AjaxResult add(@RequestBody SysLegalBasis sysLegalBasis) {
        return AjaxResult.success(sysLegalBasisService.insertSysLegalBasis(sysLegalBasis));
    }

    /**
     * 修改定性依据
     */
    @Anonymous
    @PutMapping
    public AjaxResult edit(@RequestBody SysLegalBasis sysLegalBasis) {
        return AjaxResult.success(sysLegalBasisService.updateSysLegalBasis(sysLegalBasis));
    }

    /**
     * 删除定性依据
     */
    @Anonymous
    @DeleteMapping("/{basisIds}")
    public AjaxResult remove(@PathVariable Long[] basisIds) {
        return AjaxResult.success(sysLegalBasisService.deleteSysLegalBasisByIds(basisIds));
    }
}
