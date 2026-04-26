package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysDocumentTemplate;
import com.ruoyi.system.domain.SysDocumentVariable;
import com.ruoyi.system.domain.SysDocumentGroup;
import com.ruoyi.system.domain.SysDocumentRecord;

/**
 * 文书模块Service接口
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public interface ISysDocumentService {
    // 文书模板

    /**
     * 查询文书模板
     *
     * @param id 文书模板主键
     * @return 文书模板
     */
    SysDocumentTemplate selectSysDocumentTemplateById(Long id);

    /**
     * 查询文书模板列表
     *
     * @param template 文书模板
     * @return 文书模板集合
     */
    List<SysDocumentTemplate> selectSysDocumentTemplateList(SysDocumentTemplate template);

    /**
     * 查询所有启用的模板
     *
     * @return 启用的模板列表
     */
    List<SysDocumentTemplate> selectAllSysDocumentTemplates();

    /**
     * 新增文书模板
     *
     * @param template 文书模板
     * @return 结果
     */
    int insertSysDocumentTemplate(SysDocumentTemplate template);

    /**
     * 修改文书模板
     *
     * @param template 文书模板
     * @return 结果
     */
    int updateSysDocumentTemplate(SysDocumentTemplate template);

    /**
     * 删除文书模板
     *
     * @param id 文书模板主键
     * @return 结果
     */
    int deleteSysDocumentTemplateById(Long id);

    /**
     * 批量删除文书模板
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteSysDocumentTemplateByIds(Long[] ids);

    /**
     * 根据行业分类ID查询模板列表
     *
     * @param industryCategoryId 行业分类ID
     * @return 模板列表
     */
    List<SysDocumentTemplate> selectByIndustryCategoryId(Long industryCategoryId);

    /**
     * 根据模板ID列表查询模板
     *
     * @param templateIds 模板ID列表
     * @return 模板列表
     */
    List<SysDocumentTemplate> selectByTemplateIds(List<Long> templateIds);

    // 文书模板变量

    /**
     * 根据模板ID查询变量
     *
     * @param templateId 模板ID
     * @return 文书模板变量列表
     */
    List<SysDocumentVariable> selectVariablesByTemplateId(Long templateId);

    /**
     * 新增文书模板变量
     *
     * @param variable 文书模板变量
     * @return 结果
     */
    int insertSysDocumentVariable(SysDocumentVariable variable);

    /**
     * 修改文书模板变量
     *
     * @param variable 文书模板变量
     * @return 结果
     */
    int updateSysDocumentVariable(SysDocumentVariable variable);

    /**
     * 删除文书模板变量
     *
     * @param id 文书模板变量主键
     * @return 结果
     */
    int deleteSysDocumentVariableById(Long id);

    // 文书套组

    /**
     * 查询文书套组
     *
     * @param id 文书套组主键
     * @return 文书套组
     */
    SysDocumentGroup selectSysDocumentGroupById(Long id);

    /**
     * 查询文书套组列表
     *
     * @param group 文书套组
     * @return 文书套组集合
     */
    List<SysDocumentGroup> selectSysDocumentGroupList(SysDocumentGroup group);

    /**
     * 查询所有启用的套组
     *
     * @return 启用的套组列表
     */
    List<SysDocumentGroup> selectAllSysDocumentGroups();

    /**
     * 新增文书套组
     *
     * @param group 文书套组
     * @return 结果
     */
    int insertSysDocumentGroup(SysDocumentGroup group);

    /**
     * 修改文书套组
     *
     * @param group 文书套组
     * @return 结果
     */
    int updateSysDocumentGroup(SysDocumentGroup group);

    /**
     * 删除文书套组
     *
     * @param id 文书套组主键
     * @return 结果
     */
    int deleteSysDocumentGroupById(Long id);

    // 文书记录

    /**
     * 查询文书记录
     *
     * @param id 文书记录主键
     * @return 文书记录
     */
    SysDocumentRecord selectSysDocumentRecordById(Long id);

    /**
     * 查询文书记录列表
     *
     * @param record 文书记录
     * @return 文书记录集合
     */
    List<SysDocumentRecord> selectSysDocumentRecordList(SysDocumentRecord record);

    /**
     * 根据执法记录ID查询文书列表
     *
     * @param enforcementRecordId 执法记录ID
     * @return 文书记录列表
     */
    List<SysDocumentRecord> selectRecordsByEnforcementRecordId(Long enforcementRecordId);

    /**
     * 新增文书记录
     *
     * @param record 文书记录
     * @return 结果
     */
    int insertSysDocumentRecord(SysDocumentRecord record);

    /**
     * 修改文书记录
     *
     * @param record 文书记录
     * @return 结果
     */
    int updateSysDocumentRecord(SysDocumentRecord record);

    /**
     * 删除文书记录
     *
     * @param id 文书记录主键
     * @return 结果
     */
    int deleteSysDocumentRecordById(Long id);
}
