package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.SysDocumentTemplateMapper;
import com.ruoyi.system.mapper.SysDocumentVariableMapper;
import com.ruoyi.system.mapper.SysDocumentGroupMapper;
import com.ruoyi.system.mapper.SysDocumentRecordMapper;
import com.ruoyi.system.mapper.SysDocumentTemplateIndustryMapper;
import com.ruoyi.system.domain.SysDocumentTemplate;
import com.ruoyi.system.domain.SysDocumentVariable;
import com.ruoyi.system.domain.SysDocumentGroup;
import com.ruoyi.system.domain.SysDocumentRecord;
import com.ruoyi.system.domain.SysDocumentTemplateIndustry;
import com.ruoyi.system.service.ISysDocumentService;

/**
 * 文书模块Service业务层处理
 *
 * @author ruoyi
 * @date 2026-04-25
 */
@Service
public class SysDocumentServiceImpl implements ISysDocumentService {
    @Autowired
    private SysDocumentTemplateMapper sysDocumentTemplateMapper;

    @Autowired
    private SysDocumentVariableMapper sysDocumentVariableMapper;

    @Autowired
    private SysDocumentGroupMapper sysDocumentGroupMapper;

    @Autowired
    private SysDocumentRecordMapper sysDocumentRecordMapper;

    @Autowired
    private SysDocumentTemplateIndustryMapper sysDocumentTemplateIndustryMapper;

    // ==================== 文书模板 ====================

    /**
     * 查询文书模板
     */
    @Override
    public SysDocumentTemplate selectSysDocumentTemplateById(Long id) {
        return sysDocumentTemplateMapper.selectSysDocumentTemplateById(id);
    }

    /**
     * 查询文书模板列表
     */
    @Override
    public List<SysDocumentTemplate> selectSysDocumentTemplateList(SysDocumentTemplate template) {
        return sysDocumentTemplateMapper.selectSysDocumentTemplateList(template);
    }

    /**
     * 查询所有启用的模板
     */
    @Override
    public List<SysDocumentTemplate> selectAllSysDocumentTemplates() {
        return sysDocumentTemplateMapper.selectAllSysDocumentTemplates();
    }

    /**
     * 新增文书模板
     */
    @Override
    @Transactional
    public int insertSysDocumentTemplate(SysDocumentTemplate template) {
        template.setCreateTime(DateUtils.getNowDate());
        template.setCreateBy(SecurityUtils.getUsername());
        int rows = sysDocumentTemplateMapper.insertSysDocumentTemplate(template);
        // 保存行业分类关联
        if (template.getIndustryCategoryIds() != null && !template.getIndustryCategoryIds().isEmpty()) {
            saveIndustryRelations(template.getId(), template.getIndustryCategoryIds());
        }
        return rows;
    }

    /**
     * 修改文书模板
     */
    @Override
    @Transactional
    public int updateSysDocumentTemplate(SysDocumentTemplate template) {
        template.setUpdateTime(DateUtils.getNowDate());
        template.setUpdateBy(SecurityUtils.getUsername());
        // 先删除旧的行业关联
        sysDocumentTemplateIndustryMapper.deleteByTemplateId(template.getId());
        // 再插入新的行业关联
        if (template.getIndustryCategoryIds() != null && !template.getIndustryCategoryIds().isEmpty()) {
            saveIndustryRelations(template.getId(), template.getIndustryCategoryIds());
        }
        return sysDocumentTemplateMapper.updateSysDocumentTemplate(template);
    }

    /**
     * 保存行业分类关联
     */
    private void saveIndustryRelations(Long templateId, List<Long> industryCategoryIds) {
        List<SysDocumentTemplateIndustry> relations = industryCategoryIds.stream()
            .map(industryId -> {
                SysDocumentTemplateIndustry relation = new SysDocumentTemplateIndustry();
                relation.setTemplateId(templateId);
                relation.setIndustryCategoryId(industryId);
                return relation;
            })
            .collect(Collectors.toList());
        sysDocumentTemplateIndustryMapper.insertBatch(relations);
    }

    /**
     * 根据行业分类ID查询模板列表
     */
    @Override
    public List<SysDocumentTemplate> selectByIndustryCategoryId(Long industryCategoryId) {
        return sysDocumentTemplateMapper.selectByIndustryCategoryId(industryCategoryId);
    }

    /**
     * 根据模板ID列表查询模板
     */
    @Override
    public List<SysDocumentTemplate> selectByTemplateIds(List<Long> templateIds) {
        if (templateIds == null || templateIds.isEmpty()) {
            return new ArrayList<>();
        }
        return sysDocumentTemplateMapper.selectByTemplateIds(templateIds);
    }

    /**
     * 删除文书模板
     */
    @Override
    public int deleteSysDocumentTemplateById(Long id) {
        return sysDocumentTemplateMapper.deleteSysDocumentTemplateById(id);
    }

    /**
     * 批量删除文书模板
     */
    @Override
    public int deleteSysDocumentTemplateByIds(Long[] ids) {
        return sysDocumentTemplateMapper.deleteSysDocumentTemplateByIds(ids);
    }

    // ==================== 文书模板变量 ====================

    /**
     * 根据模板ID查询变量
     */
    @Override
    public List<SysDocumentVariable> selectVariablesByTemplateId(Long templateId) {
        return sysDocumentVariableMapper.selectVariablesByTemplateId(templateId);
    }

    /**
     * 新增文书模板变量
     */
    @Override
    public int insertSysDocumentVariable(SysDocumentVariable variable) {
        variable.setCreateTime(DateUtils.getNowDate());
        variable.setCreateBy(SecurityUtils.getUsername());
        return sysDocumentVariableMapper.insertSysDocumentVariable(variable);
    }

    /**
     * 修改文书模板变量
     */
    @Override
    public int updateSysDocumentVariable(SysDocumentVariable variable) {
        variable.setUpdateTime(DateUtils.getNowDate());
        variable.setUpdateBy(SecurityUtils.getUsername());
        return sysDocumentVariableMapper.updateSysDocumentVariable(variable);
    }

    /**
     * 删除文书模板变量
     */
    @Override
    public int deleteSysDocumentVariableById(Long id) {
        return sysDocumentVariableMapper.deleteSysDocumentVariableById(id);
    }

    // ==================== 文书套组 ====================

    /**
     * 查询文书套组
     */
    @Override
    public SysDocumentGroup selectSysDocumentGroupById(Long id) {
        return sysDocumentGroupMapper.selectSysDocumentGroupById(id);
    }

    /**
     * 查询文书套组列表
     */
    @Override
    public List<SysDocumentGroup> selectSysDocumentGroupList(SysDocumentGroup group) {
        return sysDocumentGroupMapper.selectSysDocumentGroupList(group);
    }

    /**
     * 查询所有启用的套组
     */
    @Override
    public List<SysDocumentGroup> selectAllSysDocumentGroups() {
        return sysDocumentGroupMapper.selectAllSysDocumentGroups();
    }

    /**
     * 新增文书套组
     */
    @Override
    public int insertSysDocumentGroup(SysDocumentGroup group) {
        group.setCreateTime(DateUtils.getNowDate());
        group.setCreateBy(SecurityUtils.getUsername());
        return sysDocumentGroupMapper.insertSysDocumentGroup(group);
    }

    /**
     * 修改文书套组
     */
    @Override
    public int updateSysDocumentGroup(SysDocumentGroup group) {
        group.setUpdateTime(DateUtils.getNowDate());
        group.setUpdateBy(SecurityUtils.getUsername());
        return sysDocumentGroupMapper.updateSysDocumentGroup(group);
    }

    /**
     * 删除文书套组
     */
    @Override
    public int deleteSysDocumentGroupById(Long id) {
        return sysDocumentGroupMapper.deleteSysDocumentGroupById(id);
    }

    // ==================== 文书记录 ====================

    /**
     * 查询文书记录
     */
    @Override
    public SysDocumentRecord selectSysDocumentRecordById(Long id) {
        return sysDocumentRecordMapper.selectSysDocumentRecordById(id);
    }

    /**
     * 查询文书记录列表
     */
    @Override
    public List<SysDocumentRecord> selectSysDocumentRecordList(SysDocumentRecord record) {
        return sysDocumentRecordMapper.selectSysDocumentRecordList(record);
    }

    /**
     * 根据执法记录ID查询文书列表
     */
    @Override
    public List<SysDocumentRecord> selectRecordsByEnforcementRecordId(Long enforcementRecordId) {
        return sysDocumentRecordMapper.selectRecordsByEnforcementRecordId(enforcementRecordId);
    }

    /**
     * 新增文书记录
     */
    @Override
    public int insertSysDocumentRecord(SysDocumentRecord record) {
        record.setCreateTime(DateUtils.getNowDate());
        record.setCreateBy(SecurityUtils.getUsername());
        return sysDocumentRecordMapper.insertSysDocumentRecord(record);
    }

    /**
     * 修改文书记录
     */
    @Override
    public int updateSysDocumentRecord(SysDocumentRecord record) {
        record.setUpdateTime(DateUtils.getNowDate());
        record.setUpdateBy(SecurityUtils.getUsername());
        return sysDocumentRecordMapper.updateSysDocumentRecord(record);
    }

    /**
     * 删除文书记录
     */
    @Override
    public int deleteSysDocumentRecordById(Long id) {
        return sysDocumentRecordMapper.deleteSysDocumentRecordById(id);
    }
}
