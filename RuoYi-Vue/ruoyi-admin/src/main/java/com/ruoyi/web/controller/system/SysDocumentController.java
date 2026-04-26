package com.ruoyi.web.controller.system;

import java.util.List;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysDocumentTemplate;
import com.ruoyi.system.domain.SysDocumentVariable;
import com.ruoyi.system.domain.SysDocumentGroup;
import com.ruoyi.system.domain.SysDocumentRecord;
import com.ruoyi.system.service.ISysDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 文书模块管理
 */
@RestController
@RequestMapping("/api/admin/document")
public class SysDocumentController extends BaseController {

    @Autowired
    private ISysDocumentService sysDocumentService;

    // ==================== 文书模板管理 ====================

    /**
     * 获取模板列表
     */
    @GetMapping("/template/list")
    public AjaxResult listTemplate(SysDocumentTemplate template) {
        startPage();
        List<SysDocumentTemplate> list = sysDocumentService.selectSysDocumentTemplateList(template);
        return AjaxResult.success(getDataTable(list));
    }

    /**
     * 获取模板详情
     */
    @GetMapping("/template/{id}")
    public AjaxResult getTemplateInfo(@PathVariable("id") Long id) {
        SysDocumentTemplate template = sysDocumentService.selectSysDocumentTemplateById(id);
        // 获取关联的行业分类ID列表
        List<Long> industryCategoryIds = sysDocumentService.selectIndustryCategoryIdsByTemplateId(id);
        return AjaxResult.success(template).put("industryCategoryIds", industryCategoryIds);
    }

    /**
     * 新增模板
     */
    @PostMapping("/template")
    public AjaxResult addTemplate(@RequestBody SysDocumentTemplate template) {
        return AjaxResult.success(sysDocumentService.insertSysDocumentTemplate(template));
    }

    /**
     * 修改模板
     */
    @PutMapping("/template")
    public AjaxResult editTemplate(@RequestBody SysDocumentTemplate template) {
        return AjaxResult.success(sysDocumentService.updateSysDocumentTemplate(template));
    }

    /**
     * 删除模板
     */
    @DeleteMapping("/template/{id}")
    public AjaxResult removeTemplate(@PathVariable("id") Long id) {
        return AjaxResult.success(sysDocumentService.deleteSysDocumentTemplateById(id));
    }

    // ==================== 文书模板变量管理 ====================

    /**
     * 新增变量
     */
    @PostMapping("/variable")
    public AjaxResult addVariable(@RequestBody SysDocumentVariable variable) {
        return AjaxResult.success(sysDocumentService.insertSysDocumentVariable(variable));
    }

    /**
     * 修改变量
     */
    @PutMapping("/variable")
    public AjaxResult editVariable(@RequestBody SysDocumentVariable variable) {
        return AjaxResult.success(sysDocumentService.updateSysDocumentVariable(variable));
    }

    /**
     * 删除变量
     */
    @DeleteMapping("/variable/{id}")
    public AjaxResult removeVariable(@PathVariable("id") Long id) {
        return AjaxResult.success(sysDocumentService.deleteSysDocumentVariableById(id));
    }

    // ==================== 文书套组管理 ====================

    /**
     * 获取套组列表
     */
    @GetMapping("/group/list")
    public AjaxResult listGroup(SysDocumentGroup group) {
        startPage();
        List<SysDocumentGroup> list = sysDocumentService.selectSysDocumentGroupList(group);
        return AjaxResult.success(getDataTable(list));
    }

    /**
     * 获取套组详情
     */
    @GetMapping("/group/{id}")
    public AjaxResult getGroupInfo(@PathVariable("id") Long id) {
        SysDocumentGroup group = sysDocumentService.selectSysDocumentGroupById(id);
        return AjaxResult.success(group);
    }

    /**
     * 新增套组
     */
    @PostMapping("/group")
    public AjaxResult addGroup(@RequestBody SysDocumentGroup group) {
        return AjaxResult.success(sysDocumentService.insertSysDocumentGroup(group));
    }

    /**
     * 修改套组
     */
    @PutMapping("/group")
    public AjaxResult editGroup(@RequestBody SysDocumentGroup group) {
        return AjaxResult.success(sysDocumentService.updateSysDocumentGroup(group));
    }

    /**
     * 删除套组
     */
    @DeleteMapping("/group/{id}")
    public AjaxResult removeGroup(@PathVariable("id") Long id) {
        return AjaxResult.success(sysDocumentService.deleteSysDocumentGroupById(id));
    }

    // ==================== 文书记录管理 ====================

    /**
     * 获取记录列表
     */
    @GetMapping("/record/list")
    public AjaxResult listRecord(SysDocumentRecord record) {
        startPage();
        List<SysDocumentRecord> list = sysDocumentService.selectSysDocumentRecordList(record);
        return AjaxResult.success(getDataTable(list));
    }

    /**
     * 获取记录详情
     */
    @GetMapping("/record/{id}")
    public AjaxResult getRecordInfo(@PathVariable("id") Long id) {
        SysDocumentRecord record = sysDocumentService.selectSysDocumentRecordById(id);
        return AjaxResult.success(record);
    }

    /**
     * 新增记录
     */
    @PostMapping("/record")
    public AjaxResult addRecord(@RequestBody SysDocumentRecord record) {
        return AjaxResult.success(sysDocumentService.insertSysDocumentRecord(record));
    }

    /**
     * 修改记录
     */
    @PutMapping("/record")
    public AjaxResult editRecord(@RequestBody SysDocumentRecord record) {
        return AjaxResult.success(sysDocumentService.updateSysDocumentRecord(record));
    }

    /**
     * 删除记录
     */
    @DeleteMapping("/record/{id}")
    public AjaxResult removeRecord(@PathVariable("id") Long id) {
        return AjaxResult.success(sysDocumentService.deleteSysDocumentRecordById(id));
    }

    // ==================== 移动端API ====================

    /**
     * 同步模板（下行的文书模板）
     */
    @Anonymous
    @GetMapping("/sync/template")
    public AjaxResult syncTemplate() {
        List<SysDocumentTemplate> list = sysDocumentService.selectAllSysDocumentTemplates();
        return AjaxResult.success(list);
    }

    /**
     * 同步套组
     */
    @Anonymous
    @GetMapping("/sync/group")
    public AjaxResult syncGroup() {
        List<SysDocumentGroup> list = sysDocumentService.selectAllSysDocumentGroups();
        return AjaxResult.success(list);
    }

    /**
     * 获取模板详情（含变量）
     */
    @Anonymous
    @GetMapping("/mobile/template/{id}")
    public AjaxResult getMobileTemplateDetail(@PathVariable("id") Long id) {
        SysDocumentTemplate template = sysDocumentService.selectSysDocumentTemplateById(id);
        List<SysDocumentVariable> variables = sysDocumentService.selectVariablesByTemplateId(id);
        return AjaxResult.success()
                .put("template", template)
                .put("variables", variables);
    }

    /**
     * 上传生成的文书
     */
    @Anonymous
    @PostMapping("/mobile/upload")
    public AjaxResult uploadDocument(@RequestBody SysDocumentRecord record) {
        return AjaxResult.success(sysDocumentService.insertSysDocumentRecord(record));
    }
}
