package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysUnitMapper;
import com.ruoyi.system.domain.SysUnit;
import com.ruoyi.system.service.ISysUnitService;

/**
 * 执法单位Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-24
 */
@Service
public class SysUnitServiceImpl implements ISysUnitService 
{
    @Autowired
    private SysUnitMapper sysUnitMapper;

    /**
     * 查询执法单位
     * 
     * @param unitId 执法单位主键
     * @return 执法单位
     */
    @Override
    public SysUnit selectSysUnitByUnitId(Long unitId)
    {
        return sysUnitMapper.selectSysUnitByUnitId(unitId);
    }

    /**
     * 查询执法单位列表
     * 
     * @param sysUnit 执法单位
     * @return 执法单位
     */
    @Override
    public List<SysUnit> selectSysUnitList(SysUnit sysUnit)
    {
        return sysUnitMapper.selectSysUnitList(sysUnit);
    }

    /**
     * 新增执法单位
     * 
     * @param sysUnit 执法单位
     * @return 结果
     */
    @Override
    public int insertSysUnit(SysUnit sysUnit)
    {
        sysUnit.setCreateTime(DateUtils.getNowDate());
        return sysUnitMapper.insertSysUnit(sysUnit);
    }

    /**
     * 修改执法单位
     * 
     * @param sysUnit 执法单位
     * @return 结果
     */
    @Override
    public int updateSysUnit(SysUnit sysUnit)
    {
        sysUnit.setUpdateTime(DateUtils.getNowDate());
        return sysUnitMapper.updateSysUnit(sysUnit);
    }

    /**
     * 批量删除执法单位
     * 
     * @param unitIds 需要删除的执法单位主键
     * @return 结果
     */
    @Override
    public int deleteSysUnitByUnitIds(Long[] unitIds)
    {
        return sysUnitMapper.deleteSysUnitByUnitIds(unitIds);
    }

    /**
     * 删除执法单位信息
     *
     * @param unitId 执法单位主键
     * @return 结果
     */
    @Override
    public int deleteSysUnitByUnitId(Long unitId)
    {
        return sysUnitMapper.deleteSysUnitByUnitId(unitId);
    }

    /**
     * 查询执法单位列表（带行业分类名称）
     *
     * @param sysUnit 执法单位
     * @return 执法单位集合
     */
    @Override
    public List<SysUnit> selectUnitListWithCategory(SysUnit sysUnit)
    {
        return sysUnitMapper.selectUnitListWithCategory(sysUnit);
    }
}
