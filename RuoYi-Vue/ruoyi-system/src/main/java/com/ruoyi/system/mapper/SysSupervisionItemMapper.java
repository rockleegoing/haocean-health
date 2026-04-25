package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysSupervisionItem;

/**
 * 监管事项Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public interface SysSupervisionItemMapper
{
    /**
     * 查询监管事项
     *
     * @param itemId 监管事项主键
     * @return 监管事项
     */
    public SysSupervisionItem selectSysSupervisionItemById(Long itemId);

    /**
     * 查询监管事项列表
     *
     * @param sysSupervisionItem 监管事项
     * @return 监管事项集合
     */
    public List<SysSupervisionItem> selectSysSupervisionItemList(SysSupervisionItem sysSupervisionItem);

    /**
     * 查询监管事项列表（带类型名称）
     *
     * @param sysSupervisionItem 监管事项
     * @return 监管事项集合
     */
    public List<SysSupervisionItem> selectSupervisionItemListWithCategory(SysSupervisionItem sysSupervisionItem);

    /**
     * 查询子事项列表
     *
     * @param parentId 父级事项ID
     * @return 监管事项集合
     */
    public List<SysSupervisionItem> selectSupervisionItemListByParentId(Long parentId);

    /**
     * 根据监管类型查询事项
     *
     * @param categoryId 监管类型ID
     * @return 监管事项集合
     */
    public List<SysSupervisionItem> selectSupervisionItemListByCategoryId(Long categoryId);

    /**
     * 搜索监管事项
     *
     * @param keyword 关键字
     * @param categoryId 监管类型ID
     * @return 监管事项集合
     */
    public List<SysSupervisionItem> searchSupervisionItem(String keyword, Long categoryId);

    /**
     * 新增监管事项
     *
     * @param sysSupervisionItem 监管事项
     * @return 结果
     */
    public int insertSysSupervisionItem(SysSupervisionItem sysSupervisionItem);

    /**
     * 修改监管事项
     *
     * @param sysSupervisionItem 监管事项
     * @return 结果
     */
    public int updateSysSupervisionItem(SysSupervisionItem sysSupervisionItem);

    /**
     * 删除监管事项
     *
     * @param itemId 监管事项主键
     * @return 结果
     */
    public int deleteSysSupervisionItemById(Long itemId);

    /**
     * 批量删除监管事项
     *
     * @param itemIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysSupervisionItemByIds(Long[] itemIds);
}
