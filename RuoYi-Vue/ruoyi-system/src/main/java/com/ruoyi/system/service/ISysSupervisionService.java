package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysSupervisionItem;
import com.ruoyi.system.domain.SysSupervisionCategory;
import com.ruoyi.system.domain.SysSupervisionLanguageLink;
import com.ruoyi.system.domain.SysSupervisionRegulationLink;

/**
 * 监管事项Service接口
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public interface ISysSupervisionService
{
    // ==================== 监管事项 ====================

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
    public List<SysSupervisionItem> selectSupervisionItemList(SysSupervisionItem sysSupervisionItem);

    /**
     * 查询所有启用的一级监管事项（首页用）
     *
     * @return 监管事项列表
     */
    public List<SysSupervisionItem> selectTopLevelItems();

    /**
     * 根据父级ID查询子事项
     *
     * @param parentId 父级ID
     * @return 监管事项列表
     */
    public List<SysSupervisionItem> selectItemsByParentId(Long parentId);

    /**
     * 根据监管类型查询事项
     *
     * @param categoryId 监管类型ID
     * @return 监管事项列表
     */
    public List<SysSupervisionItem> selectItemsByCategoryId(Long categoryId);

    /**
     * 搜索监管事项
     *
     * @param keyword 关键字
     * @param categoryId 监管类型ID
     * @return 监管事项列表
     */
    public List<SysSupervisionItem> searchItems(String keyword, Long categoryId);

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

    // ==================== 监管类型 ====================

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
     * 查询所有启用的监管类型
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

    // ==================== 关联管理 ====================

    /**
     * 查询监管事项关联的规范用语
     *
     * @param itemId 监管事项ID
     * @return 规范用语列表
     */
    public List<SysSupervisionLanguageLink> selectLanguageLinksByItemId(Long itemId);

    /**
     * 查询监管事项关联的法律法规
     *
     * @param itemId 监管事项ID
     * @return 法律法规列表
     */
    public List<SysSupervisionRegulationLink> selectRegulationLinksByItemId(Long itemId);

    /**
     * 添加规范用语关联
     *
     * @param itemId 监管事项ID
     * @param languageId 规范用语ID
     * @return 结果
     */
    public int insertLanguageLink(Long itemId, Long languageId);

    /**
     * 添加法律法规关联
     *
     * @param itemId 监管事项ID
     * @param regulationId 法律法规ID
     * @return 结果
     */
    public int insertRegulationLink(Long itemId, Long regulationId);

    /**
     * 删除规范用语关联
     *
     * @param linkId 关联ID
     * @return 结果
     */
    public int deleteLanguageLink(Long linkId);

    /**
     * 删除法律法规关联
     *
     * @param linkId 关联ID
     * @return 结果
     */
    public int deleteRegulationLink(Long linkId);
}
