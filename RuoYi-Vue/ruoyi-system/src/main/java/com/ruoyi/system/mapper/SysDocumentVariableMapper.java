package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysDocumentVariable;

/**
 * 文书模板变量Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public interface SysDocumentVariableMapper
{
    /**
     * 查询文书模板变量
     *
     * @param id 文书模板变量主键
     * @return 文书模板变量
     */
    public SysDocumentVariable selectSysDocumentVariableById(Long id);

    /**
     * 查询文书模板变量列表
     *
     * @param sysDocumentVariable 文书模板变量
     * @return 文书模板变量集合
     */
    public List<SysDocumentVariable> selectSysDocumentVariableList(SysDocumentVariable sysDocumentVariable);

    /**
     * 根据模板ID查询变量
     *
     * @param templateId 模板ID
     * @return 文书模板变量列表
     */
    public List<SysDocumentVariable> selectVariablesByTemplateId(Long templateId);

    /**
     * 新增文书模板变量
     *
     * @param sysDocumentVariable 文书模板变量
     * @return 结果
     */
    public int insertSysDocumentVariable(SysDocumentVariable sysDocumentVariable);

    /**
     * 修改文书模板变量
     *
     * @param sysDocumentVariable 文书模板变量
     * @return 结果
     */
    public int updateSysDocumentVariable(SysDocumentVariable sysDocumentVariable);

    /**
     * 删除文书模板变量
     *
     * @param id 文书模板变量主键
     * @return 结果
     */
    public int deleteSysDocumentVariableById(Long id);

    /**
     * 批量删除文书模板变量
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysDocumentVariableByIds(Long[] ids);

    /**
     * 根据模板ID删除所有变量
     *
     * @param templateId 模板ID
     * @return 结果
     */
    public int deleteVariablesByTemplateId(Long templateId);
}
