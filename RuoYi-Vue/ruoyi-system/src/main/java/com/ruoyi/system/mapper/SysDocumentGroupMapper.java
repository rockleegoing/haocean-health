package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysDocumentGroup;

/**
 * 文书套组Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public interface SysDocumentGroupMapper
{
    /**
     * 查询文书套组
     *
     * @param id 文书套组主键
     * @return 文书套组
     */
    public SysDocumentGroup selectSysDocumentGroupById(Long id);

    /**
     * 查询文书套组列表
     *
     * @param sysDocumentGroup 文书套组
     * @return 文书套组集合
     */
    public List<SysDocumentGroup> selectSysDocumentGroupList(SysDocumentGroup sysDocumentGroup);

    /**
     * 查询所有启用的套组（用于App同步）
     *
     * @return 启用的套组列表
     */
    public List<SysDocumentGroup> selectAllSysDocumentGroups();

    /**
     * 新增文书套组
     *
     * @param sysDocumentGroup 文书套组
     * @return 结果
     */
    public int insertSysDocumentGroup(SysDocumentGroup sysDocumentGroup);

    /**
     * 修改文书套组
     *
     * @param sysDocumentGroup 文书套组
     * @return 结果
     */
    public int updateSysDocumentGroup(SysDocumentGroup sysDocumentGroup);

    /**
     * 删除文书套组
     *
     * @param id 文书套组主键
     * @return 结果
     */
    public int deleteSysDocumentGroupById(Long id);

    /**
     * 批量删除文书套组
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysDocumentGroupByIds(Long[] ids);
}
