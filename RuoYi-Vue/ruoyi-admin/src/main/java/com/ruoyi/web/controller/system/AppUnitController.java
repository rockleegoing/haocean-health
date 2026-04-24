package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysUnit;
import com.ruoyi.system.service.ISysUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Android 端执法单位接口（匿名可访问）
 */
@RestController
@RequestMapping("/app/unit")
public class AppUnitController extends BaseController {

    @Autowired
    private ISysUnitService unitService;

    /**
     * 获取单位列表（带行业分类名称）
     */
    @Anonymous
    @GetMapping("/list")
    public AjaxResult list() {
        List<SysUnit> list = unitService.selectUnitListWithCategory(new SysUnit());
        return AjaxResult.success(list);
    }

    /**
     * 获取单位详情
     */
    @Anonymous
    @GetMapping("/{unitId}")
    public AjaxResult getInfo(@PathVariable Long unitId) {
        SysUnit unit = unitService.selectSysUnitByUnitId(unitId);
        return AjaxResult.success(unit);
    }
}
