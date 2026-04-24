package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysIndustryCategory;

/**
 * 行业分类Mapper接口
 * 
 * @author liru
 * @date 2026-04-24
 */
public interface SysIndustryCategoryMapper 
{
    /**
     * 查询行业分类
     * 
     * @param categoryId 行业分类主键
     * @return 行业分类
     */
    public SysIndustryCategory selectSysIndustryCategoryByCategoryId(Long categoryId);

    /**
     * 查询行业分类列表
     * 
     * @param sysIndustryCategory 行业分类
     * @return 行业分类集合
     */
    public List<SysIndustryCategory> selectSysIndustryCategoryList(SysIndustryCategory sysIndustryCategory);

    /**
     * 新增行业分类
     * 
     * @param sysIndustryCategory 行业分类
     * @return 结果
     */
    public int insertSysIndustryCategory(SysIndustryCategory sysIndustryCategory);

    /**
     * 修改行业分类
     * 
     * @param sysIndustryCategory 行业分类
     * @return 结果
     */
    public int updateSysIndustryCategory(SysIndustryCategory sysIndustryCategory);

    /**
     * 删除行业分类
     * 
     * @param categoryId 行业分类主键
     * @return 结果
     */
    public int deleteSysIndustryCategoryByCategoryId(Long categoryId);

    /**
     * 批量删除行业分类
     * 
     * @param categoryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysIndustryCategoryByCategoryIds(Long[] categoryIds);
}
