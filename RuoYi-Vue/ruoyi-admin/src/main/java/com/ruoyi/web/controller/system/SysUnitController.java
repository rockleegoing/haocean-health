package com.ruoyi.web.controller.system;

import java.util.List;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysUnit;
import com.ruoyi.system.service.ISysUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 执法单位管理
 */
@RestController
@RequestMapping("/system/unit")
public class SysUnitController extends BaseController {

    @Autowired
    private ISysUnitService sysUnitService;

    /**
     * 获取执法单位列表
     */
    @Anonymous
    @GetMapping("/list")
    public AjaxResult list(SysUnit sysUnit) {
        List<SysUnit> list = sysUnitService.selectUnitListWithCategory(sysUnit);
        return AjaxResult.success(list);
    }

    /**
     * 获取执法单位详情
     */
    @Anonymous
    @GetMapping("/{unitId}")
    public AjaxResult getInfo(@PathVariable("unitId") Long unitId) {
        return AjaxResult.success(sysUnitService.selectSysUnitByUnitId(unitId));
    }

    /**
     * 新增执法单位
     */
    @Anonymous
    @PostMapping
    public AjaxResult add(@RequestBody SysUnit sysUnit) {
        return AjaxResult.success(sysUnitService.insertSysUnit(sysUnit));
    }

    /**
     * 修改执法单位
     */
    @Anonymous
    @PutMapping
    public AjaxResult edit(@RequestBody SysUnit sysUnit) {
        return AjaxResult.success(sysUnitService.updateSysUnit(sysUnit));
    }

    /**
     * 删除执法单位
     */
    @Anonymous
    @DeleteMapping("/{unitIds}")
    public AjaxResult remove(@PathVariable Long[] unitIds) {
        return AjaxResult.success(sysUnitService.deleteSysUnitByUnitIds(unitIds));
    }
}
