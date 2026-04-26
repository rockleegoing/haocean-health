package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.SysRegulation;
import com.ruoyi.system.domain.SysRegulationChapter;
import com.ruoyi.system.domain.SysRegulationArticle;

/**
 * 法律法规Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public interface SysRegulationMapper {
    /**
     * 查询法律法规
     *
     * @param regulationId 法律法规主键
     * @return 法律法规
     */
    public SysRegulation selectSysRegulationById(Long regulationId);

    /**
     * 查询法律法规列表
     *
     * @param sysRegulation 法律法规
     * @return 法律法规集合
     */
    public List<SysRegulation> selectSysRegulationList(SysRegulation sysRegulation);

    /**
     * 新增法律法规
     *
     * @param sysRegulation 法律法规
     * @return 结果
     */
    public int insertSysRegulation(SysRegulation sysRegulation);

    /**
     * 修改法律法规
     *
     * @param sysRegulation 法律法规
     * @return 结果
     */
    public int updateSysRegulation(SysRegulation sysRegulation);

    /**
     * 删除法律法规
     *
     * @param regulationId 法律法规主键
     * @return 结果
     */
    public int deleteSysRegulationById(Long regulationId);

    /**
     * 批量删除法律法规
     *
     * @param regulationIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysRegulationByIds(Long[] regulationIds);

    /**
     * 查询章节列表
     *
     * @param params 包含regulationId和updateTimeFrom的Map
     * @return 章节列表
     */
    public List<SysRegulationChapter> selectChapterListByRegulationId(Map<String, Object> params);

    /**
     * 查询章节详情
     *
     * @param chapterId 章节ID
     * @return 章节
     */
    public SysRegulationChapter selectSysRegulationChapterById(Long chapterId);

    /**
     * 查询条款列表
     *
     * @param params 包含regulationId和updateTimeFrom的Map
     * @return 条款列表
     */
    public List<SysRegulationArticle> selectArticleListByRegulationId(Map<String, Object> params);

    /**
     * 查询条款详情
     *
     * @param articleId 条款ID
     * @return 条款
     */
    public SysRegulationArticle selectSysRegulationArticleById(Long articleId);

    /**
     * 新增章节
     *
     * @param chapter 章节
     * @return 结果
     */
    public int insertSysRegulationChapter(SysRegulationChapter chapter);

    /**
     * 修改章节
     *
     * @param chapter 章节
     * @return 结果
     */
    public int updateSysRegulationChapter(SysRegulationChapter chapter);

    /**
     * 删除章节
     *
     * @param chapterId 章节ID
     * @return 结果
     */
    public int deleteSysRegulationChapterById(Long chapterId);

    /**
     * 批量删除章节
     *
     * @param chapterIds 章节ID数组
     * @return 结果
     */
    public int deleteSysRegulationChapterByIds(Long[] chapterIds);

    /**
     * 新增条款
     *
     * @param article 条款
     * @return 结果
     */
    public int insertSysRegulationArticle(SysRegulationArticle article);

    /**
     * 修改条款
     *
     * @param article 条款
     * @return 结果
     */
    public int updateSysRegulationArticle(SysRegulationArticle article);

    /**
     * 删除条款
     *
     * @param articleId 条款ID
     * @return 结果
     */
    public int deleteSysRegulationArticleById(Long articleId);

    /**
     * 批量删除条款
     *
     * @param articleIds 条款ID数组
     * @return 结果
     */
    public int deleteSysRegulationArticleByIds(Long[] articleIds);
}
