package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysDocumentRecord;

/**
 * 文书记录Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public interface SysDocumentRecordMapper
{
    /**
     * 查询文书记录
     *
     * @param id 文书记录主键
     * @return 文书记录
     */
    public SysDocumentRecord selectSysDocumentRecordById(Long id);

    /**
     * 查询文书记录列表
     *
     * @param sysDocumentRecord 文书记录
     * @return 文书记录集合
     */
    public List<SysDocumentRecord> selectSysDocumentRecordList(SysDocumentRecord sysDocumentRecord);

    /**
     * 根据执法记录ID查询文书列表
     *
     * @param enforcementRecordId 执法记录ID
     * @return 文书记录列表
     */
    public List<SysDocumentRecord> selectRecordsByEnforcementRecordId(Long enforcementRecordId);

    /**
     * 新增文书记录
     *
     * @param sysDocumentRecord 文书记录
     * @return 结果
     */
    public int insertSysDocumentRecord(SysDocumentRecord sysDocumentRecord);

    /**
     * 修改文书记录
     *
     * @param sysDocumentRecord 文书记录
     * @return 结果
     */
    public int updateSysDocumentRecord(SysDocumentRecord sysDocumentRecord);

    /**
     * 删除文书记录
     *
     * @param id 文书记录主键
     * @return 结果
     */
    public int deleteSysDocumentRecordById(Long id);

    /**
     * 批量删除文书记录
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysDocumentRecordByIds(Long[] ids);
}
