package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.SysSupervisionItemMapper;
import com.ruoyi.system.mapper.SysSupervisionCategoryMapper;
import com.ruoyi.system.mapper.SysSupervisionLinkMapper;
import com.ruoyi.system.domain.SysSupervisionItem;
import com.ruoyi.system.domain.SysSupervisionCategory;
import com.ruoyi.system.domain.SysSupervisionLanguageLink;
import com.ruoyi.system.domain.SysSupervisionRegulationLink;
import com.ruoyi.system.service.ISysSupervisionService;

/**
 * 监管事项Service业务层处理
 *
 * @author ruoyi
 * @date 2026-04-25
 */
@Service
public class SysSupervisionServiceImpl implements ISysSupervisionService
{
    @Autowired
    private SysSupervisionItemMapper sysSupervisionItemMapper;

    @Autowired
    private SysSupervisionCategoryMapper sysSupervisionCategoryMapper;

    @Autowired
    private SysSupervisionLinkMapper sysSupervisionLinkMapper;

    // ==================== 监管事项 ====================

    /**
     * 查询监管事项
     */
    @Override
    public SysSupervisionItem selectSysSupervisionItemById(Long itemId)
    {
        return sysSupervisionItemMapper.selectSysSupervisionItemById(itemId);
    }

    /**
     * 查询监管事项列表
     */
    @Override
    public List<SysSupervisionItem> selectSupervisionItemList(SysSupervisionItem sysSupervisionItem)
    {
        return sysSupervisionItemMapper.selectSupervisionItemListWithCategory(sysSupervisionItem);
    }

    /**
     * 查询所有启用的一级监管事项（首页用）
     */
    @Override
    public List<SysSupervisionItem> selectTopLevelItems()
    {
        SysSupervisionItem query = new SysSupervisionItem();
        query.setParentId(0L);
        query.setStatus("0");
        return sysSupervisionItemMapper.selectSupervisionItemListWithCategory(query);
    }

    /**
     * 根据父级ID查询子事项
     */
    @Override
    public List<SysSupervisionItem> selectItemsByParentId(Long parentId)
    {
        return sysSupervisionItemMapper.selectSupervisionItemListByParentId(parentId);
    }

    /**
     * 根据监管类型查询事项
     */
    @Override
    public List<SysSupervisionItem> selectItemsByCategoryId(Long categoryId)
    {
        return sysSupervisionItemMapper.selectSupervisionItemListByCategoryId(categoryId);
    }

    /**
     * 搜索监管事项
     */
    @Override
    public List<SysSupervisionItem> searchItems(String keyword, Long categoryId)
    {
        return sysSupervisionItemMapper.searchSupervisionItem(keyword, categoryId);
    }

    /**
     * 新增监管事项
     */
    @Override
    public int insertSysSupervisionItem(SysSupervisionItem sysSupervisionItem)
    {
        sysSupervisionItem.setCreateTime(DateUtils.getNowDate());
        sysSupervisionItem.setCreateBy(SecurityUtils.getUsername());
        return sysSupervisionItemMapper.insertSysSupervisionItem(sysSupervisionItem);
    }

    /**
     * 修改监管事项
     */
    @Override
    public int updateSysSupervisionItem(SysSupervisionItem sysSupervisionItem)
    {
        sysSupervisionItem.setUpdateTime(DateUtils.getNowDate());
        sysSupervisionItem.setUpdateBy(SecurityUtils.getUsername());
        return sysSupervisionItemMapper.updateSysSupervisionItem(sysSupervisionItem);
    }

    /**
     * 删除监管事项
     */
    @Override
    public int deleteSysSupervisionItemById(Long itemId)
    {
        return sysSupervisionItemMapper.deleteSysSupervisionItemById(itemId);
    }

    /**
     * 批量删除监管事项
     */
    @Override
    public int deleteSysSupervisionItemByIds(Long[] itemIds)
    {
        return sysSupervisionItemMapper.deleteSysSupervisionItemByIds(itemIds);
    }

    // ==================== 监管类型 ====================

    /**
     * 查询监管类型
     */
    @Override
    public SysSupervisionCategory selectSysSupervisionCategoryById(Long categoryId)
    {
        return sysSupervisionCategoryMapper.selectSysSupervisionCategoryById(categoryId);
    }

    /**
     * 查询监管类型列表
     */
    @Override
    public List<SysSupervisionCategory> selectSysSupervisionCategoryList(SysSupervisionCategory sysSupervisionCategory)
    {
        return sysSupervisionCategoryMapper.selectSysSupervisionCategoryList(sysSupervisionCategory);
    }

    /**
     * 查询所有启用的监管类型
     */
    @Override
    public List<SysSupervisionCategory> selectAllCategories()
    {
        return sysSupervisionCategoryMapper.selectAllCategories();
    }

    /**
     * 新增监管类型
     */
    @Override
    public int insertSysSupervisionCategory(SysSupervisionCategory sysSupervisionCategory)
    {
        sysSupervisionCategory.setCreateTime(DateUtils.getNowDate());
        sysSupervisionCategory.setCreateBy(SecurityUtils.getUsername());
        return sysSupervisionCategoryMapper.insertSysSupervisionCategory(sysSupervisionCategory);
    }

    /**
     * 修改监管类型
     */
    @Override
    public int updateSysSupervisionCategory(SysSupervisionCategory sysSupervisionCategory)
    {
        sysSupervisionCategory.setUpdateTime(DateUtils.getNowDate());
        sysSupervisionCategory.setUpdateBy(SecurityUtils.getUsername());
        return sysSupervisionCategoryMapper.updateSysSupervisionCategory(sysSupervisionCategory);
    }

    /**
     * 删除监管类型
     */
    @Override
    public int deleteSysSupervisionCategoryById(Long categoryId)
    {
        return sysSupervisionCategoryMapper.deleteSysSupervisionCategoryById(categoryId);
    }

    // ==================== 关联管理 ====================

    /**
     * 查询监管事项关联的规范用语
     */
    @Override
    public List<SysSupervisionLanguageLink> selectLanguageLinksByItemId(Long itemId)
    {
        return sysSupervisionLinkMapper.selectLanguageLinksByItemId(itemId);
    }

    /**
     * 查询监管事项关联的法律法规
     */
    @Override
    public List<SysSupervisionRegulationLink> selectRegulationLinksByItemId(Long itemId)
    {
        return sysSupervisionLinkMapper.selectRegulationLinksByItemId(itemId);
    }

    /**
     * 添加规范用语关联
     */
    @Override
    @Transactional
    public int insertLanguageLink(Long itemId, Long languageId)
    {
        SysSupervisionLanguageLink link = new SysSupervisionLanguageLink();
        link.setItemId(itemId);
        link.setLanguageId(languageId);
        link.setCreateBy(SecurityUtils.getUsername());
        return sysSupervisionLinkMapper.insertLanguageLink(link);
    }

    /**
     * 添加法律法规关联
     */
    @Override
    @Transactional
    public int insertRegulationLink(Long itemId, Long regulationId)
    {
        SysSupervisionRegulationLink link = new SysSupervisionRegulationLink();
        link.setItemId(itemId);
        link.setRegulationId(regulationId);
        link.setCreateBy(SecurityUtils.getUsername());
        return sysSupervisionLinkMapper.insertRegulationLink(link);
    }

    /**
     * 删除规范用语关联
     */
    @Override
    public int deleteLanguageLink(Long linkId)
    {
        return sysSupervisionLinkMapper.deleteLanguageLinkById(linkId);
    }

    /**
     * 删除法律法规关联
     */
    @Override
    public int deleteRegulationLink(Long linkId)
    {
        return sysSupervisionLinkMapper.deleteRegulationLinkById(linkId);
    }
}
