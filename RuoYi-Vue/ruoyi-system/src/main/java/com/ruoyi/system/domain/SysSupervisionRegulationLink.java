package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 监管事项关联法律法规对象 sys_supervision_regulation_link
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public class SysSupervisionRegulationLink extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 关联ID */
    private Long linkId;

    /** 监管事项ID */
    private Long itemId;

    /** 法律法规ID */
    private Long regulationId;

    /** 排序 */
    private Integer sortOrder;

    /** 法律法规名称（不映射数据库列，通过JOIN查询获取） */
    private String regulationName;

    /** 法律法规文号 */
    private String lawCode;

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

    public void setRegulationId(Long regulationId)
    {
        this.regulationId = regulationId;
    }

    public Long getRegulationId()
    {
        return regulationId;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public String getRegulationName()
    {
        return regulationName;
    }

    public void setRegulationName(String regulationName)
    {
        this.regulationName = regulationName;
    }

    public String getLawCode()
    {
        return lawCode;
    }

    public void setLawCode(String lawCode)
    {
        this.lawCode = lawCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("linkId", getLinkId())
            .append("itemId", getItemId())
            .append("regulationId", getRegulationId())
            .append("sortOrder", getSortOrder())
            .append("regulationName", getRegulationName())
            .append("lawCode", getLawCode())
            .toString();
    }
}
