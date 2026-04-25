package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysDocumentTemplate;

/**
 * 文书模板Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public interface SysDocumentTemplateMapper
{
    /**
     * 查询文书模板
     *
     * @param id 文书模板主键
     * @return 文书模板
     */
    public SysDocumentTemplate selectSysDocumentTemplateById(Long id);

    /**
     * 查询文书模板列表
     *
     * @param sysDocumentTemplate 文书模板
     * @return 文书模板集合
     */
    public List<SysDocumentTemplate> selectSysDocumentTemplateList(SysDocumentTemplate sysDocumentTemplate);

    /**
     * 查询所有启用的模板（用于App同步，含categoryId和sort）
     *
     * @return 启用的模板列表
     */
    public List<SysDocumentTemplate> selectAllSysDocumentTemplates();

    /**
     * 新增文书模板
     *
     * @param sysDocumentTemplate 文书模板
     * @return 结果
     */
    public int insertSysDocumentTemplate(SysDocumentTemplate sysDocumentTemplate);

    /**
     * 修改文书模板
     *
     * @param sysDocumentTemplate 文书模板
     * @return 结果
     */
    public int updateSysDocumentTemplate(SysDocumentTemplate sysDocumentTemplate);

    /**
     * 删除文书模板（逻辑删除）
     *
     * @param id 文书模板主键
     * @return 结果
     */
    public int deleteSysDocumentTemplateById(Long id);

    /**
     * 批量删除文书模板（逻辑删除）
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysDocumentTemplateByIds(Long[] ids);
}
