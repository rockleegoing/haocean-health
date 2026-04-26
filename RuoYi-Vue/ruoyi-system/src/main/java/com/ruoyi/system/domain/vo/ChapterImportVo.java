package com.ruoyi.system.domain.vo;

import java.util.List;

/**
 * 法规章节导入VO
 *
 * @author ruoyi
 * @date 2026-04-26
 */
public class ChapterImportVo {
    /** 章节号 */
    private String chapterNo;

    /** 章节标题 */
    private String chapterTitle;

    /** 排序 */
    private Integer sortOrder;

    /** 条款列表 */
    private List<ArticleImportVo> articles;

    public String getChapterNo() {
        return chapterNo;
    }

    public void setChapterNo(String chapterNo) {
        this.chapterNo = chapterNo;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public List<ArticleImportVo> getArticles() {
        return articles;
    }

    public void setArticles(List<ArticleImportVo> articles) {
        this.articles = articles;
    }
}