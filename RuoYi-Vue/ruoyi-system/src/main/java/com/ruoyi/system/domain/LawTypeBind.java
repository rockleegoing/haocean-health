package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 法律类型关联对象 law_type_bind
 *
 * @author ruoyi
 * @date 2026-04-30
 */
public class LawTypeBind extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 法律ID */
    private Long lawId;

    /** 类型ID */
    private Long typeId;

    public void setLawId(Long lawId)
    {
        this.lawId = lawId;
    }

    public Long getLawId()
    {
        return lawId;
    }

    public void setTypeId(Long typeId)
    {
        this.typeId = typeId;
    }

    public Long getTypeId()
    {
        return typeId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("lawId", getLawId())
            .append("typeId", getTypeId())
            .toString();
    }
}