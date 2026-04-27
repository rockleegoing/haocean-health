package com.ruoyi.web.controller.system;

import java.util.List;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.system.domain.dict.SysLegalType;
import com.ruoyi.system.service.ISysLegalTypeService;

/**
 * 法律类型管理
 */
@RestController
@RequestMapping("/system/legal/type")
public class SysLegalTypeController extends BaseController {

    @Autowired
    private ISysLegalTypeService legalTypeService;

    /**
     * 获取法律类型列表
     */
    @Anonymous
    @GetMapping("/list")
    public TableDataInfo list(SysLegalType legalType) {
        startPage();
        List<SysLegalType> list = legalTypeService.selectLegalTypeList(legalType);
        return getDataTable(list);
    }

    /**
     * 获取所有正常状态的法律类型
     */
    @Anonymous
    @GetMapping("/all")
    public List<SysLegalType> all() {
        return legalTypeService.selectLegalTypeAll();
    }

    /**
     * 获取法律类型详情
     */
    @Anonymous
    @GetMapping("/{typeId}")
    public com.ruoyi.common.core.domain.AjaxResult getInfo(@PathVariable("typeId") Long typeId) {
        return success(legalTypeService.selectLegalTypeById(typeId));
    }

    /**
     * 新增法律类型
     */
    @Anonymous
    @PostMapping
    public com.ruoyi.common.core.domain.AjaxResult add(@RequestBody SysLegalType legalType) {
        legalType.setCreateBy(getUsername());
        return toAjax(legalTypeService.insertLegalType(legalType));
    }

    /**
     * 修改法律类型
     */
    @Anonymous
    @PutMapping
    public com.ruoyi.common.core.domain.AjaxResult edit(@RequestBody SysLegalType legalType) {
        legalType.setUpdateBy(getUsername());
        return toAjax(legalTypeService.updateLegalType(legalType));
    }

    /**
     * 删除法律类型
     */
    @Anonymous
    @DeleteMapping("/{typeIds}")
    public com.ruoyi.common.core.domain.AjaxResult remove(@PathVariable Long[] typeIds) {
        return toAjax(legalTypeService.deleteLegalTypeByIds(typeIds));
    }
}
