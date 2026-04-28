package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysBasisChapterLink;

/**
 * 章节依据关联Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-28
 */
public interface SysBasisChapterLinkMapper {
    /**
     * 查询章节依据关联
     *
     * @param linkId 关联ID
     * @return 章节依据关联
     */
    public SysBasisChapterLink selectSysBasisChapterLinkById(Long linkId);

    /**
     * 查询章节依据关联列表
     *
     * @param sysBasisChapterLink 章节依据关联
     * @return 章节依据关联集合
     */
    public List<SysBasisChapterLink> selectSysBasisChapterLinkList(SysBasisChapterLink sysBasisChapterLink);

    /**
     * 根据章节ID查询关联列表
     *
     * @param chapterId 章节ID
     * @return 关联列表
     */
    public List<SysBasisChapterLink> selectSysBasisChapterLinkByChapterId(Long chapterId);

    /**
     * 根据章节ID和依据类型查询关联列表
     *
     * @param chapterId 章节ID
     * @param basisType 依据类型：legal/processing
     * @return 关联列表
     */
    public List<SysBasisChapterLink> selectSysBasisChapterLinkByChapterIdAndType(Long chapterId, String basisType);

    /**
     * 新增章节依据关联
     *
     * @param sysBasisChapterLink 章节依据关联
     * @return 结果
     */
    public int insertSysBasisChapterLink(SysBasisChapterLink sysBasisChapterLink);

    /**
     * 修改章节依据关联
     *
     * @param sysBasisChapterLink 章节依据关联
     * @return 结果
     */
    public int updateSysBasisChapterLink(SysBasisChapterLink sysBasisChapterLink);

    /**
     * 删除章节依据关联
     *
     * @param linkId 关联ID
     * @return 结果
     */
    public int deleteSysBasisChapterLinkById(Long linkId);

    /**
     * 批量删除章节依据关联
     *
     * @param linkIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysBasisChapterLinkByIds(Long[] linkIds);

    /**
     * 根据章节ID删除所有关联
     *
     * @param chapterId 章节ID
     * @return 结果
     */
    public int deleteSysBasisChapterLinkByChapterId(Long chapterId);

    /**
     * 根据章节ID和依据类型删除关联
     *
     * @param chapterId 章节ID
     * @param basisType 依据类型
     * @return 结果
     */
    public int deleteSysBasisChapterLinkByChapterIdAndType(Long chapterId, String basisType);
}
