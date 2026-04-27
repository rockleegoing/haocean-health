package com.ruoyi.web.controller.system;

import java.util.List;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.SysProcessingBasis;
import com.ruoyi.system.service.ISysProcessingBasisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 处理依据管理
 */
@RestController
@RequestMapping("/system/processingBasis")
public class SysProcessingBasisController extends BaseController {

    @Autowired
    private ISysProcessingBasisService sysProcessingBasisService;

    /**
     * 获取处理依据列表
     */
    @Anonymous
    @GetMapping("/list")
    public TableDataInfo list(SysProcessingBasis sysProcessingBasis) {
        startPage();
        List<SysProcessingBasis> list = sysProcessingBasisService.selectSysProcessingBasisList(sysProcessingBasis);
        return getDataTable(list);
    }

    /**
     * 获取处理依据详情
     */
    @Anonymous
    @GetMapping("/{basisId}")
    public AjaxResult getInfo(@PathVariable("basisId") Long basisId) {
        return AjaxResult.success(sysProcessingBasisService.selectSysProcessingBasisById(basisId));
    }

    /**
     * 获取某法律法规关联的处理依据列表
     */
    @Anonymous
    @GetMapping("/regulation/{regulationId}")
    public AjaxResult getListByRegulationId(@PathVariable("regulationId") Long regulationId) {
        List<SysProcessingBasis> list = sysProcessingBasisService.selectSysProcessingBasisListByRegulationId(regulationId);
        return AjaxResult.success(list);
    }

    /**
     * 新增处理依据
     */
    @Anonymous
    @PostMapping
    public AjaxResult add(@RequestBody SysProcessingBasis sysProcessingBasis) {
        return AjaxResult.success(sysProcessingBasisService.insertSysProcessingBasis(sysProcessingBasis));
    }

    /**
     * 修改处理依据
     */
    @Anonymous
    @PutMapping
    public AjaxResult edit(@RequestBody SysProcessingBasis sysProcessingBasis) {
        return AjaxResult.success(sysProcessingBasisService.updateSysProcessingBasis(sysProcessingBasis));
    }

    /**
     * 删除处理依据
     */
    @Anonymous
    @DeleteMapping("/{basisIds}")
    public AjaxResult remove(@PathVariable Long[] basisIds) {
        return AjaxResult.success(sysProcessingBasisService.deleteSysProcessingBasisByIds(basisIds));
    }
}
