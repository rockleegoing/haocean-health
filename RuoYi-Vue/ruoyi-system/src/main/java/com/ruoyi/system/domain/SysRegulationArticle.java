package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 法规条款对象 sys_regulation_article
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public class SysRegulationArticle extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 条款ID */
    private Long articleId;

    /** 关联章节ID */
    private Long chapterId;

    /** 关联法律法规ID */
    private Long regulationId;

    /** 条款号（如：第一条） */
    private String articleNo;

    /** 条款内容 */
    private String content;

    /** 排序 */
    private Integer sortOrder;

    /** 法规标题（用于前端展示，非数据库字段） */
    private String regulationTitle;

    /** 章节标题（用于前端展示，非数据库字段） */
    private String chapterTitle;

    /** 增量同步起始时间（前端传入，非数据库字段） */
    private String updateTimeFrom;

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setRegulationId(Long regulationId) {
        this.regulationId = regulationId;
    }

    public Long getRegulationId() {
        return regulationId;
    }

    public void setArticleNo(String articleNo) {
        this.articleNo = articleNo;
    }

    public String getArticleNo() {
        return articleNo;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public String getRegulationTitle() {
        return regulationTitle;
    }

    public void setRegulationTitle(String regulationTitle) {
        this.regulationTitle = regulationTitle;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public String getUpdateTimeFrom() {
        return updateTimeFrom;
    }

    public void setUpdateTimeFrom(String updateTimeFrom) {
        this.updateTimeFrom = updateTimeFrom;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("articleId", getArticleId())
                .append("chapterId", getChapterId())
                .append("regulationId", getRegulationId())
                .append("articleNo", getArticleNo())
                .append("content", getContent())
                .append("sortOrder", getSortOrder())
                .append("regulationTitle", getRegulationTitle())
                .append("chapterTitle", getChapterTitle())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
