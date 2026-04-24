package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysUnit;

/**
 * 执法单位Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-24
 */
public interface SysUnitMapper 
{
    /**
     * 查询执法单位
     * 
     * @param unitId 执法单位主键
     * @return 执法单位
     */
    public SysUnit selectSysUnitByUnitId(Long unitId);

    /**
     * 查询执法单位列表
     * 
     * @param sysUnit 执法单位
     * @return 执法单位集合
     */
    public List<SysUnit> selectSysUnitList(SysUnit sysUnit);

    /**
     * 新增执法单位
     * 
     * @param sysUnit 执法单位
     * @return 结果
     */
    public int insertSysUnit(SysUnit sysUnit);

    /**
     * 修改执法单位
     * 
     * @param sysUnit 执法单位
     * @return 结果
     */
    public int updateSysUnit(SysUnit sysUnit);

    /**
     * 删除执法单位
     * 
     * @param unitId 执法单位主键
     * @return 结果
     */
    public int deleteSysUnitByUnitId(Long unitId);

    /**
     * 批量删除执法单位
     *
     * @param unitIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysUnitByUnitIds(Long[] unitIds);

    /**
     * 查询执法单位列表（带行业分类名称）
     *
     * @param sysUnit 执法单位
     * @return 执法单位集合
     */
    public List<SysUnit> selectUnitListWithCategory(SysUnit sysUnit);
}
