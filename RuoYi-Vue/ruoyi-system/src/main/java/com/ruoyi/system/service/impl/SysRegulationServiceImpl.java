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
        List<SysRegulationChapter> chapters = sysRegulationMapper.selectChapterListByRegulationId(regulationId);
        // 填充法规标题
        SysRegulation regulation = sysRegulationMapper.selectSysRegulationById(regulationId);
        String regulationTitle = regulation != null ? regulation.getTitle() : "";
        for (SysRegulationChapter chapter : chapters) {
            chapter.setRegulationTitle(regulationTitle);
        }
        return chapters;
    }

    /**
     * 查询章节详情
     */
    @Override
    public SysRegulationChapter selectSysRegulationChapterById(Long chapterId) {
        SysRegulationChapter chapter = sysRegulationMapper.selectSysRegulationChapterById(chapterId);
        if (chapter != null && chapter.getRegulationId() != null) {
            SysRegulation regulation = sysRegulationMapper.selectSysRegulationById(chapter.getRegulationId());
            if (regulation != null) {
                chapter.setRegulationTitle(regulation.getTitle());
            }
        }
        return chapter;
    }

    /**
     * 查询条款列表
     */
    @Override
    public List<SysRegulationArticle> selectArticleListByRegulationId(Long regulationId) {
        return selectArticleListByRegulationId(regulationId, null);
    }

    /**
     * 查询条款列表（可选按章节筛选）
     */
    @Override
    public List<SysRegulationArticle> selectArticleListByRegulationId(Long regulationId, Long chapterId) {
        List<SysRegulationArticle> articles = sysRegulationMapper.selectArticleListByRegulationId(regulationId);
        // 填充法规标题
        SysRegulation regulation = sysRegulationMapper.selectSysRegulationById(regulationId);
        String regulationTitle = regulation != null ? regulation.getTitle() : "";
        // 填充章节标题
        List<SysRegulationChapter> chapters = sysRegulationMapper.selectChapterListByRegulationId(regulationId);
        for (SysRegulationArticle article : articles) {
            article.setRegulationTitle(regulationTitle);
            if (article.getChapterId() != null) {
                for (SysRegulationChapter chapter : chapters) {
                    if (chapter.getChapterId().equals(article.getChapterId())) {
                        article.setChapterTitle(chapter.getChapterTitle());
                        break;
                    }
                }
            }
        }
        // 如果指定了chapterId，进行过滤
        if (chapterId != null) {
            articles = articles.stream()
                .filter(a -> chapterId.equals(a.getChapterId()))
                .collect(java.util.stream.Collectors.toList());
        }
        return articles;
    }

    /**
     * 查询条款详情
     */
    @Override
    public SysRegulationArticle selectSysRegulationArticleById(Long articleId) {
        SysRegulationArticle article = sysRegulationMapper.selectSysRegulationArticleById(articleId);
        if (article != null) {
            // 填充法规标题
            if (article.getRegulationId() != null) {
                SysRegulation regulation = sysRegulationMapper.selectSysRegulationById(article.getRegulationId());
                if (regulation != null) {
                    article.setRegulationTitle(regulation.getTitle());
                }
            }
            // 填充章节标题
            if (article.getChapterId() != null) {
                SysRegulationChapter chapter = sysRegulationMapper.selectSysRegulationChapterById(article.getChapterId());
                if (chapter != null) {
                    article.setChapterTitle(chapter.getChapterTitle());
                }
            }
        }
        return article;
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
