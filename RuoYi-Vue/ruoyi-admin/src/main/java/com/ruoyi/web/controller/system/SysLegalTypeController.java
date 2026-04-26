package com.ruoyi.web.controller.system;

import java.util.List;
import com.ruoyi.common.annotation.Anonymous;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.system.domain.dict.SysLegalType;
import com.ruoyi.system.service.ISysLegalTypeService;

/**
 * 法律类型管理
 */
@RestController
@RequestMapping("/system/legal/type")
public class SysLegalTypeController {

    @Autowired
    private ISysLegalTypeService legalTypeService;

    /**
     * 获取法律类型列表
     */
    @Anonymous
    @GetMapping("/list")
    public List<SysLegalType> list(SysLegalType legalType) {
        return legalTypeService.selectLegalTypeList(legalType);
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
    public SysLegalType getInfo(@PathVariable("typeId") Long typeId) {
        return legalTypeService.selectLegalTypeById(typeId);
    }

    /**
     * 新增法律类型
     */
    @Anonymous
    @PostMapping
    public int add(@RequestBody SysLegalType legalType) {
        return legalTypeService.insertLegalType(legalType);
    }

    /**
     * 修改法律类型
     */
    @Anonymous
    @PutMapping
    public int edit(@RequestBody SysLegalType legalType) {
        return legalTypeService.updateLegalType(legalType);
    }

    /**
     * 删除法律类型
     */
    @Anonymous
    @DeleteMapping("/{typeIds}")
    public int remove(@PathVariable Long[] typeIds) {
        return legalTypeService.deleteLegalTypeByIds(typeIds);
    }
}
