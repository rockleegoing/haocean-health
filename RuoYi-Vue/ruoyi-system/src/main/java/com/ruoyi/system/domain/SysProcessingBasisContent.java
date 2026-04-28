package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 处理依据内容表 sys_processing_basis_content
 */
public class SysProcessingBasisContent extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 内容ID */
    private Long contentId;

    /** 关联处理依据ID */
    private Long basisId;

    /** 标签 */
    private String label;

    /** 内容 */
    private String content;

    /** 排序 */
    private Integer sortOrder;

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setBasisId(Long basisId) {
        this.basisId = basisId;
    }

    public Long getBasisId() {
        return basisId;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
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
                .append("contentId", getContentId())
                .append("basisId", getBasisId())
                .append("label", getLabel())
                .append("content", getContent())
                .append("sortOrder", getSortOrder())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
