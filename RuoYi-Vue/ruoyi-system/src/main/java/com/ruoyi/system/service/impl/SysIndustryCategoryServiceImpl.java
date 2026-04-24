package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysIndustryCategoryMapper;
import com.ruoyi.system.domain.SysIndustryCategory;
import com.ruoyi.system.service.ISysIndustryCategoryService;

/**
 * 行业分类Service业务层处理
 * 
 * @author liru
 * @date 2026-04-24
 */
@Service
public class SysIndustryCategoryServiceImpl implements ISysIndustryCategoryService 
{
    @Autowired
    private SysIndustryCategoryMapper sysIndustryCategoryMapper;

    /**
     * 查询行业分类
     * 
     * @param categoryId 行业分类主键
     * @return 行业分类
     */
    @Override
    public SysIndustryCategory selectSysIndustryCategoryByCategoryId(Long categoryId)
    {
        return sysIndustryCategoryMapper.selectSysIndustryCategoryByCategoryId(categoryId);
    }

    /**
     * 查询行业分类列表
     * 
     * @param sysIndustryCategory 行业分类
     * @return 行业分类
     */
    @Override
    public List<SysIndustryCategory> selectSysIndustryCategoryList(SysIndustryCategory sysIndustryCategory)
    {
        return sysIndustryCategoryMapper.selectSysIndustryCategoryList(sysIndustryCategory);
    }

    /**
     * 新增行业分类
     * 
     * @param sysIndustryCategory 行业分类
     * @return 结果
     */
    @Override
    public int insertSysIndustryCategory(SysIndustryCategory sysIndustryCategory)
    {
        sysIndustryCategory.setCreateTime(DateUtils.getNowDate());
        return sysIndustryCategoryMapper.insertSysIndustryCategory(sysIndustryCategory);
    }

    /**
     * 修改行业分类
     * 
     * @param sysIndustryCategory 行业分类
     * @return 结果
     */
    @Override
    public int updateSysIndustryCategory(SysIndustryCategory sysIndustryCategory)
    {
        sysIndustryCategory.setUpdateTime(DateUtils.getNowDate());
        return sysIndustryCategoryMapper.updateSysIndustryCategory(sysIndustryCategory);
    }

    /**
     * 批量删除行业分类
     * 
     * @param categoryIds 需要删除的行业分类主键
     * @return 结果
     */
    @Override
    public int deleteSysIndustryCategoryByCategoryIds(Long[] categoryIds)
    {
        return sysIndustryCategoryMapper.deleteSysIndustryCategoryByCategoryIds(categoryIds);
    }

    /**
     * 删除行业分类信息
     * 
     * @param categoryId 行业分类主键
     * @return 结果
     */
    @Override
    public int deleteSysIndustryCategoryByCategoryId(Long categoryId)
    {
        return sysIndustryCategoryMapper.deleteSysIndustryCategoryByCategoryId(categoryId);
    }
}
