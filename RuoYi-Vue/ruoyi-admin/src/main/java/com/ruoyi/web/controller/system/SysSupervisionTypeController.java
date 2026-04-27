package com.ruoyi.web.controller.system;

import java.util.List;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.system.domain.dict.SysSupervisionType;
import com.ruoyi.system.service.ISysSupervisionTypeService;

/**
 * 监管类型管理
 */
@RestController
@RequestMapping("/system/supervision/type")
public class SysSupervisionTypeController extends BaseController {

    @Autowired
    private ISysSupervisionTypeService supervisionTypeService;

    /**
     * 获取监管类型列表
     */
    @Anonymous
    @GetMapping("/list")
    public TableDataInfo list(SysSupervisionType supervisionType) {
        startPage();
        List<SysSupervisionType> list = supervisionTypeService.selectSupervisionTypeList(supervisionType);
        return getDataTable(list);
    }

    /**
     * 获取所有正常状态的监管类型
     */
    @Anonymous
    @GetMapping("/all")
    public List<SysSupervisionType> all() {
        return supervisionTypeService.selectSupervisionTypeAll();
    }

    /**
     * 获取监管类型详情
     */
    @Anonymous
    @GetMapping("/{typeId}")
    public com.ruoyi.common.core.domain.AjaxResult getInfo(@PathVariable("typeId") Long typeId) {
        return success(supervisionTypeService.selectSupervisionTypeById(typeId));
    }

    /**
     * 新增监管类型
     */
    @Anonymous
    @PostMapping
    public com.ruoyi.common.core.domain.AjaxResult add(@RequestBody SysSupervisionType supervisionType) {
        supervisionType.setCreateBy(getUsername());
        return toAjax(supervisionTypeService.insertSupervisionType(supervisionType));
    }

    /**
     * 修改监管类型
     */
    @Anonymous
    @PutMapping
    public com.ruoyi.common.core.domain.AjaxResult edit(@RequestBody SysSupervisionType supervisionType) {
        supervisionType.setUpdateBy(getUsername());
        return toAjax(supervisionTypeService.updateSupervisionType(supervisionType));
    }

    /**
     * 删除监管类型
     */
    @Anonymous
    @DeleteMapping("/{typeIds}")
    public com.ruoyi.common.core.domain.AjaxResult remove(@PathVariable Long[] typeIds) {
        return toAjax(supervisionTypeService.deleteSupervisionTypeByIds(typeIds));
    }
}
