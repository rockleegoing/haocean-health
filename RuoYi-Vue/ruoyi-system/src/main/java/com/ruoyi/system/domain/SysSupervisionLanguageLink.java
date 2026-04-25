package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 监管事项关联规范用语对象 sys_supervision_language_link
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public class SysSupervisionLanguageLink extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 关联ID */
    private Long linkId;

    /** 监管事项ID */
    private Long itemId;

    /** 规范用语ID */
    private Long languageId;

    /** 排序 */
    private Integer sortOrder;

    /** 规范用语名称（不映射数据库列，通过JOIN查询获取） */
    private String languageName;

    /** 规范用语内容 */
    private String languageContent;

    public void setLinkId(Long linkId)
    {
        this.linkId = linkId;
    }

    public Long getLinkId()
    {
        return linkId;
    }

    public void setItemId(Long itemId)
    {
        this.itemId = itemId;
    }

    public Long getItemId()
    {
        return itemId;
    }

    public void setLanguageId(Long languageId)
    {
        this.languageId = languageId;
    }

    public Long getLanguageId()
    {
        return languageId;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public String getLanguageName()
    {
        return languageName;
    }

    public void setLanguageName(String languageName)
    {
        this.languageName = languageName;
    }

    public String getLanguageContent()
    {
        return languageContent;
    }

    public void setLanguageContent(String languageContent)
    {
        this.languageContent = languageContent;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("linkId", getLinkId())
            .append("itemId", getItemId())
            .append("languageId", getLanguageId())
            .append("sortOrder", getSortOrder())
            .append("languageName", getLanguageName())
            .append("languageContent", getLanguageContent())
            .toString();
    }
}
