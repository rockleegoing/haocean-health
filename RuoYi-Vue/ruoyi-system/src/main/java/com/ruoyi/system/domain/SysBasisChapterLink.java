package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 章节依据关联对象 sys_basis_chapter_link
 *
 * @author ruoyi
 * @date 2026-04-28
 */
public class SysBasisChapterLink extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 关联ID */
    private Long linkId;

    /** 章节ID */
    @Excel(name = "章节ID")
    private Long chapterId;

    /** 条款ID（可为NULL表示关联整个章节） */
    @Excel(name = "条款ID")
    private Long articleId;

    /** 依据类型：legal定性/processing处理 */
    @Excel(name = "依据类型")
    private String basisType;

    /** 依据ID */
    @Excel(name = "依据ID")
    private Long basisId;

    /** 章节标题（用于显示，非数据库字段） */
    private String chapterTitle;

    /** 条款号（用于显示，非数据库字段） */
    private String articleNo;

    /** 依据标题（用于显示，非数据库字段） */
    private String basisTitle;

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    public Long getLinkId() {
        return linkId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setBasisType(String basisType) {
        this.basisType = basisType;
    }

    public String getBasisType() {
        return basisType;
    }

    public void setBasisId(Long basisId) {
        this.basisId = basisId;
    }

    public Long getBasisId() {
        return basisId;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public String getArticleNo() {
        return articleNo;
    }

    public void setArticleNo(String articleNo) {
        this.articleNo = articleNo;
    }

    public String getBasisTitle() {
        return basisTitle;
    }

    public void setBasisTitle(String basisTitle) {
        this.basisTitle = basisTitle;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("linkId", getLinkId())
                .append("chapterId", getChapterId())
                .append("articleId", getArticleId())
                .append("basisType", getBasisType())
                .append("basisId", getBasisId())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
