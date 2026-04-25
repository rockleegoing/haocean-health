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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("articleId", getArticleId())
                .append("chapterId", getChapterId())
                .append("regulationId", getRegulationId())
                .append("articleNo", getArticleNo())
                .append("content", getContent())
                .append("sortOrder", getSortOrder())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
