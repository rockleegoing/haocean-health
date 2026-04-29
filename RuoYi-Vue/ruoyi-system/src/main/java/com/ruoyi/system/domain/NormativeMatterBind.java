package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 规范用语与监管事项绑定关系对象 normative_matter_bind
 *
 * @author ruoyi
 * @date 2026-04-29
 */
public class NormativeMatterBind extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 监管事项id */
    private Long matterId;

    /** 规范用语id */
    private Long normativeId;

    /** 监管事项名称（非数据库字段） */
    private String matterName;

    /** 规范用语名称（非数据库字段） */
    private String normativeName;

    /** 规范用语代码（非数据库字段） */
    private String standardCode;

    /** 监督意见（非数据库字段） */
    private String supervisoryOpinion;

    /** 处理程序（非数据库字段） */
    private String handlingProcedure;

    /** 行政处罚决定（非数据库字段） */
    private String administrativePenalty;

    public void setMatterId(Long matterId)
    {
        this.matterId = matterId;
    }

    public Long getMatterId()
    {
        return matterId;
    }

    public void setNormativeId(Long normativeId)
    {
        this.normativeId = normativeId;
    }

    public Long getNormativeId()
    {
        return normativeId;
    }

    public String getMatterName()
    {
        return matterName;
    }

    public void setMatterName(String matterName)
    {
        this.matterName = matterName;
    }

    public String getNormativeName()
    {
        return normativeName;
    }

    public void setNormativeName(String normativeName)
    {
        this.normativeName = normativeName;
    }

    public String getStandardCode()
    {
        return standardCode;
    }

    public void setStandardCode(String standardCode)
    {
        this.standardCode = standardCode;
    }

    public String getSupervisoryOpinion()
    {
        return supervisoryOpinion;
    }

    public void setSupervisoryOpinion(String supervisoryOpinion)
    {
        this.supervisoryOpinion = supervisoryOpinion;
    }

    public String getHandlingProcedure()
    {
        return handlingProcedure;
    }

    public void setHandlingProcedure(String handlingProcedure)
    {
        this.handlingProcedure = handlingProcedure;
    }

    public String getAdministrativePenalty()
    {
        return administrativePenalty;
    }

    public void setAdministrativePenalty(String administrativePenalty)
    {
        this.administrativePenalty = administrativePenalty;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("matterId", getMatterId())
            .append("normativeId", getNormativeId())
            .toString();
    }
}
