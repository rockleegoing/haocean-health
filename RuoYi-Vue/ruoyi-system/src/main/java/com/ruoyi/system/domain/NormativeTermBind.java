package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 规范用语法律条款关联对象 normative_term_bind
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
public class NormativeTermBind extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 法律条款ID */
    @Excel(name = "法律条款ID")
    private Long legalTermId;

    /** 规范用语表编号 */
    @Excel(name = "规范用语表编号")
    private Long normativeLanguageId;

    /** 依据类型,1定性依据qualitative_basis，0处理依据handling_basis */
    @Excel(name = "依据类型,1定性依据qualitative_basis，0处理依据handling_basis")
    private Integer basisType;

    /** 法律条款编码（非数据库字段） */
    private String legalTermZhCode;

    /** 法律条款内容摘要（非数据库字段） */
    private String legalTermContent;

    /** 规范用语名称（非数据库字段） */
    private String normativeName;

    /** 规范用语代码（非数据库字段） */
    private String standardCode;

    /** 监督意见（非数据库字段） */
    private String supervisoryOpinion;

    public void setLegalTermId(Long legalTermId)
    {
        this.legalTermId = legalTermId;
    }

    public Long getLegalTermId() 
    {
        return legalTermId;
    }

    public void setNormativeLanguageId(Long normativeLanguageId) 
    {
        this.normativeLanguageId = normativeLanguageId;
    }

    public Long getNormativeLanguageId() 
    {
        return normativeLanguageId;
    }

    public void setBasisType(Integer basisType) 
    {
        this.basisType = basisType;
    }

    public Integer getBasisType()
    {
        return basisType;
    }

    public String getLegalTermZhCode()
    {
        return legalTermZhCode;
    }

    public void setLegalTermZhCode(String legalTermZhCode)
    {
        this.legalTermZhCode = legalTermZhCode;
    }

    public String getLegalTermContent()
    {
        return legalTermContent;
    }

    public void setLegalTermContent(String legalTermContent)
    {
        this.legalTermContent = legalTermContent;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("legalTermId", getLegalTermId())
            .append("normativeLanguageId", getNormativeLanguageId())
            .append("basisType", getBasisType())
            .toString();
    }
}
