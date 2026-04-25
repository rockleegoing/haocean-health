package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 法规章节对象 sys_regulation_chapter
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public class SysRegulationChapter extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 章节ID */
    private Long chapterId;

    /** 关联法律法规ID */
    private Long regulationId;

    /** 章节号（如：第一章） */
    private String chapterNo;

    /** 章节标题 */
    private String chapterTitle;

    /** 排序 */
    private Integer sortOrder;

    /** 法规标题（用于前端展示，非数据库字段） */
    private String regulationTitle;

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

    public void setChapterNo(String chapterNo) {
        this.chapterNo = chapterNo;
    }

    public String getChapterNo() {
        return chapterNo;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public String getChapterTitle() {
        return chapterTitle;
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
                .append("chapterId", getChapterId())
                .append("regulationId", getRegulationId())
                .append("chapterNo", getChapterNo())
                .append("chapterTitle", getChapterTitle())
                .append("sortOrder", getSortOrder())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
