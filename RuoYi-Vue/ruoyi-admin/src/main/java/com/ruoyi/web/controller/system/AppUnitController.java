package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysUnit;
import com.ruoyi.system.service.ISysUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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

    /**
     * 新增单位（移动端调用，需携带 Token 认证）
     */
    @Anonymous
    @PostMapping
    public AjaxResult add(@RequestBody SysUnit unit) {
        // 设置默认值
        unit.setStatus("0");
        unit.setDelFlag("0");
        unit.setCreateBy(getUsername());
        unit.setCreateTime(new Date());

        int rows = unitService.insertSysUnit(unit);
        if (rows > 0) {
            // 返回新增的单位ID，便于移动端更新本地记录
            return AjaxResult.success("添加成功", unit.getUnitId());
        }
        return AjaxResult.error("添加失败");
    }
}
