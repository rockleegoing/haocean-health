package com.ruoyi.web.controller.system;

import java.util.List;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
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
    public SysSupervisionType getInfo(@PathVariable("typeId") Long typeId) {
        return supervisionTypeService.selectSupervisionTypeById(typeId);
    }

    /**
     * 新增监管类型
     */
    @Anonymous
    @PostMapping
    public int add(@RequestBody SysSupervisionType supervisionType) {
        return supervisionTypeService.insertSupervisionType(supervisionType);
    }

    /**
     * 修改监管类型
     */
    @Anonymous
    @PutMapping
    public int edit(@RequestBody SysSupervisionType supervisionType) {
        return supervisionTypeService.updateSupervisionType(supervisionType);
    }

    /**
     * 删除监管类型
     */
    @Anonymous
    @DeleteMapping("/{typeIds}")
    public int remove(@PathVariable Long[] typeIds) {
        return supervisionTypeService.deleteSupervisionTypeByIds(typeIds);
    }
}
