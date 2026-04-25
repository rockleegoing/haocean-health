package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysSupervisionCategory;

/**
 * 监管类型Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public interface SysSupervisionCategoryMapper
{
    /**
     * 查询监管类型
     *
     * @param categoryId 监管类型主键
     * @return 监管类型
     */
    public SysSupervisionCategory selectSysSupervisionCategoryById(Long categoryId);

    /**
     * 查询监管类型列表
     *
     * @param sysSupervisionCategory 监管类型
     * @return 监管类型集合
     */
    public List<SysSupervisionCategory> selectSysSupervisionCategoryList(SysSupervisionCategory sysSupervisionCategory);

    /**
     * 查询所有监管类型
     *
     * @return 监管类型集合
     */
    public List<SysSupervisionCategory> selectAllCategories();

    /**
     * 新增监管类型
     *
     * @param sysSupervisionCategory 监管类型
     * @return 结果
     */
    public int insertSysSupervisionCategory(SysSupervisionCategory sysSupervisionCategory);

    /**
     * 修改监管类型
     *
     * @param sysSupervisionCategory 监管类型
     * @return 结果
     */
    public int updateSysSupervisionCategory(SysSupervisionCategory sysSupervisionCategory);

    /**
     * 删除监管类型
     *
     * @param categoryId 监管类型主键
     * @return 结果
     */
    public int deleteSysSupervisionCategoryById(Long categoryId);

    /**
     * 批量删除监管类型
     *
     * @param categoryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysSupervisionCategoryByIds(Long[] categoryIds);
}
