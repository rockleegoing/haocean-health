package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.SysRegulationMapper;
import com.ruoyi.system.domain.SysRegulation;
import com.ruoyi.system.domain.SysRegulationChapter;
import com.ruoyi.system.domain.SysRegulationArticle;
import com.ruoyi.system.service.ISysRegulationService;

/**
 * 法律法规Service业务层处理
 *
 * @author ruoyi
 * @date 2026-04-25
 */
@Service
public class SysRegulationServiceImpl implements ISysRegulationService {
    @Autowired
    private SysRegulationMapper sysRegulationMapper;

    /**
     * 查询法律法规
     *
     * @param regulationId 法律法规主键
     * @return 法律法规
     */
    @Override
    public SysRegulation selectSysRegulationById(Long regulationId) {
        return sysRegulationMapper.selectSysRegulationById(regulationId);
    }

    /**
     * 查询法律法规列表
     *
     * @param sysRegulation 法律法规
     * @return 法律法规
     */
    @Override
    public List<SysRegulation> selectSysRegulationList(SysRegulation sysRegulation) {
        return sysRegulationMapper.selectSysRegulationList(sysRegulation);
    }

    /**
     * 新增法律法规
     *
     * @param sysRegulation 法律法规
     * @return 结果
     */
    @Override
    public int insertSysRegulation(SysRegulation sysRegulation) {
        sysRegulation.setCreateTime(DateUtils.getNowDate());
        sysRegulation.setDelFlag("0");
        return sysRegulationMapper.insertSysRegulation(sysRegulation);
    }

    /**
     * 修改法律法规
     *
     * @param sysRegulation 法律法规
     * @return 结果
     */
    @Override
    public int updateSysRegulation(SysRegulation sysRegulation) {
        sysRegulation.setUpdateTime(DateUtils.getNowDate());
        return sysRegulationMapper.updateSysRegulation(sysRegulation);
    }

    /**
     * 批量删除法律法规
     *
     * @param regulationIds 需要删除的法律法规主键
     * @return 结果
     */
    @Override
    public int deleteSysRegulationByIds(Long[] regulationIds) {
        return sysRegulationMapper.deleteSysRegulationByIds(regulationIds);
    }

    /**
     * 删除法律法规信息
     *
     * @param regulationId 法律法规主键
     * @return 结果
     */
    @Override
    public int deleteSysRegulationById(Long regulationId) {
        return sysRegulationMapper.deleteSysRegulationById(regulationId);
    }

    /**
     * 查询章节列表
     */
    @Override
    public List<SysRegulationChapter> selectChapterListByRegulationId(Long regulationId) {
        return sysRegulationMapper.selectChapterListByRegulationId(regulationId);
    }

    /**
     * 查询条款列表
     */
    @Override
    public List<SysRegulationArticle> selectArticleListByRegulationId(Long regulationId) {
        return sysRegulationMapper.selectArticleListByRegulationId(regulationId);
    }

    /**
     * 新增章节
     */
    @Override
    public int insertSysRegulationChapter(SysRegulationChapter chapter) {
        chapter.setCreateTime(DateUtils.getNowDate());
        return sysRegulationMapper.insertSysRegulationChapter(chapter);
    }

    /**
     * 修改章节
     */
    @Override
    public int updateSysRegulationChapter(SysRegulationChapter chapter) {
        chapter.setUpdateTime(DateUtils.getNowDate());
        return sysRegulationMapper.updateSysRegulationChapter(chapter);
    }

    /**
     * 删除章节
     */
    @Override
    public int deleteSysRegulationChapterById(Long chapterId) {
        return sysRegulationMapper.deleteSysRegulationChapterById(chapterId);
    }

    /**
     * 批量删除章节
     */
    @Override
    public int deleteSysRegulationChapterByIds(Long[] chapterIds) {
        return sysRegulationMapper.deleteSysRegulationChapterByIds(chapterIds);
    }

    /**
     * 新增条款
     */
    @Override
    public int insertSysRegulationArticle(SysRegulationArticle article) {
        article.setCreateTime(DateUtils.getNowDate());
        return sysRegulationMapper.insertSysRegulationArticle(article);
    }

    /**
     * 修改条款
     */
    @Override
    public int updateSysRegulationArticle(SysRegulationArticle article) {
        article.setUpdateTime(DateUtils.getNowDate());
        return sysRegulationMapper.updateSysRegulationArticle(article);
    }

    /**
     * 删除条款
     */
    @Override
    public int deleteSysRegulationArticleById(Long articleId) {
        return sysRegulationMapper.deleteSysRegulationArticleById(articleId);
    }

    /**
     * 批量删除条款
     */
    @Override
    public int deleteSysRegulationArticleByIds(Long[] articleIds) {
        return sysRegulationMapper.deleteSysRegulationArticleByIds(articleIds);
    }
}
