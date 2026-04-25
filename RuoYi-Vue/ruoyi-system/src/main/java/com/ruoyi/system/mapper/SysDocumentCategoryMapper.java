package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysDocumentCategory;

/**
 * 文书分类Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public interface SysDocumentCategoryMapper
{
    /**
     * 查询文书分类
     *
     * @param categoryId 文书分类主键
     * @return 文书分类
     */
    public SysDocumentCategory selectSysDocumentCategoryById(Long categoryId);

    /**
     * 查询文书分类列表
     *
     * @param category 文书分类
     * @return 文书分类集合
     */
    public List<SysDocumentCategory> selectSysDocumentCategoryList(SysDocumentCategory category);

    /**
     * 查询所有文书分类（用于App同步）
     *
     * @return 文书分类列表
     */
    public List<SysDocumentCategory> selectAllSysDocumentCategories();

    /**
     * 新增文书分类
     *
     * @param category 文书分类
     * @return 结果
     */
    public int insertSysDocumentCategory(SysDocumentCategory category);

    /**
     * 修改文书分类
     *
     * @param category 文书分类
     * @return 结果
     */
    public int updateSysDocumentCategory(SysDocumentCategory category);

    /**
     * 删除文书分类
     *
     * @param categoryId 文书分类主键
     * @return 结果
     */
    public int deleteSysDocumentCategoryById(Long categoryId);

    /**
     * 批量删除文书分类
     *
     * @param categoryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysDocumentCategoryByIds(Long[] categoryIds);
}
