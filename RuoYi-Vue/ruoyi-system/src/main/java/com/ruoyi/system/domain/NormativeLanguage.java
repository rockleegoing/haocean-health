package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 规范用语对象 normative_language
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
public class NormativeLanguage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 专业类别 */
    @Excel(name = "专业类别")
    private Long professionalCategory;

    /** 一级分类名称 */
    @Excel(name = "一级分类名称")
    private Long primaryCategory;

    /** 二级分类名称 */
    @Excel(name = "二级分类名称")
    private Long secondaryCategory;

    /** 三级分类名称 */
    @Excel(name = "三级分类名称")
    private Long tertiaryCategory;

    /** 规范用语代码 */
    @Excel(name = "规范用语代码")
    private String standardCode;

    /** 违法事实编码 */
    @Excel(name = "违法事实编码")
    private String violationCode;

    /** 信息报告违法事实 */
    @Excel(name = "信息报告违法事实")
    private String informationReport;

    /** 检查内容 */
    @Excel(name = "检查内容")
    private String inspectionContent;

    /** 定性依据 */
    @Excel(name = "定性依据")
    private String qualitativeBasis;

    /** 处理依据 */
    @Excel(name = "处理依据")
    private String handlingBasis;

    /** 规范用语（违法事实） */
    @Excel(name = "规范用语", readConverterExp = "违=法事实")
    private String standardPhrase;

    /** 监督意见 */
    @Excel(name = "监督意见")
    private String supervisoryOpinion;

    /** 处理内容 */
    @Excel(name = "处理内容")
    private String handlingContent;

    /** 处理程序 */
    @Excel(name = "处理程序")
    private String handlingProcedure;

    /** 行政处罚决定 */
    @Excel(name = "行政处罚决定")
    private String administrativePenalty;

    /** 行政强制及其他措施 */
    @Excel(name = "行政强制及其他措施")
    private String administrativeForcedMeasures;

    /** 其他处理情况 */
    @Excel(name = "其他处理情况")
    private String otherHandling;

    /** 逻辑删除 */
    @Excel(name = "逻辑删除")
    private Integer isDeleted;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setProfessionalCategory(Long professionalCategory) 
    {
        this.professionalCategory = professionalCategory;
    }

    public Long getProfessionalCategory() 
    {
        return professionalCategory;
    }

    public void setPrimaryCategory(Long primaryCategory) 
    {
        this.primaryCategory = primaryCategory;
    }

    public Long getPrimaryCategory() 
    {
        return primaryCategory;
    }

    public void setSecondaryCategory(Long secondaryCategory) 
    {
        this.secondaryCategory = secondaryCategory;
    }

    public Long getSecondaryCategory() 
    {
        return secondaryCategory;
    }

    public void setTertiaryCategory(Long tertiaryCategory) 
    {
        this.tertiaryCategory = tertiaryCategory;
    }

    public Long getTertiaryCategory() 
    {
        return tertiaryCategory;
    }

    public void setStandardCode(String standardCode) 
    {
        this.standardCode = standardCode;
    }

    public String getStandardCode() 
    {
        return standardCode;
    }

    public void setViolationCode(String violationCode) 
    {
        this.violationCode = violationCode;
    }

    public String getViolationCode() 
    {
        return violationCode;
    }

    public void setInformationReport(String informationReport) 
    {
        this.informationReport = informationReport;
    }

    public String getInformationReport() 
    {
        return informationReport;
    }

    public void setInspectionContent(String inspectionContent) 
    {
        this.inspectionContent = inspectionContent;
    }

    public String getInspectionContent() 
    {
        return inspectionContent;
    }

    public void setQualitativeBasis(String qualitativeBasis) 
    {
        this.qualitativeBasis = qualitativeBasis;
    }

    public String getQualitativeBasis() 
    {
        return qualitativeBasis;
    }

    public void setHandlingBasis(String handlingBasis) 
    {
        this.handlingBasis = handlingBasis;
    }

    public String getHandlingBasis() 
    {
        return handlingBasis;
    }

    public void setStandardPhrase(String standardPhrase) 
    {
        this.standardPhrase = standardPhrase;
    }

    public String getStandardPhrase() 
    {
        return standardPhrase;
    }

    public void setSupervisoryOpinion(String supervisoryOpinion) 
    {
        this.supervisoryOpinion = supervisoryOpinion;
    }

    public String getSupervisoryOpinion() 
    {
        return supervisoryOpinion;
    }

    public void setHandlingContent(String handlingContent) 
    {
        this.handlingContent = handlingContent;
    }

    public String getHandlingContent() 
    {
        return handlingContent;
    }

    public void setHandlingProcedure(String handlingProcedure) 
    {
        this.handlingProcedure = handlingProcedure;
    }

    public String getHandlingProcedure() 
    {
        return handlingProcedure;
    }

    public void setAdministrativePenalty(String administrativePenalty) 
    {
        this.administrativePenalty = administrativePenalty;
    }

    public String getAdministrativePenalty() 
    {
        return administrativePenalty;
    }

    public void setAdministrativeForcedMeasures(String administrativeForcedMeasures) 
    {
        this.administrativeForcedMeasures = administrativeForcedMeasures;
    }

    public String getAdministrativeForcedMeasures() 
    {
        return administrativeForcedMeasures;
    }

    public void setOtherHandling(String otherHandling) 
    {
        this.otherHandling = otherHandling;
    }

    public String getOtherHandling() 
    {
        return otherHandling;
    }

    public void setIsDeleted(Integer isDeleted) 
    {
        this.isDeleted = isDeleted;
    }

    public Integer getIsDeleted() 
    {
        return isDeleted;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("professionalCategory", getProfessionalCategory())
            .append("primaryCategory", getPrimaryCategory())
            .append("secondaryCategory", getSecondaryCategory())
            .append("tertiaryCategory", getTertiaryCategory())
            .append("standardCode", getStandardCode())
            .append("violationCode", getViolationCode())
            .append("informationReport", getInformationReport())
            .append("inspectionContent", getInspectionContent())
            .append("qualitativeBasis", getQualitativeBasis())
            .append("handlingBasis", getHandlingBasis())
            .append("standardPhrase", getStandardPhrase())
            .append("supervisoryOpinion", getSupervisoryOpinion())
            .append("handlingContent", getHandlingContent())
            .append("handlingProcedure", getHandlingProcedure())
            .append("administrativePenalty", getAdministrativePenalty())
            .append("administrativeForcedMeasures", getAdministrativeForcedMeasures())
            .append("otherHandling", getOtherHandling())
            .append("createTime", getCreateTime())
            .append("isDeleted", getIsDeleted())
            .toString();
    }
}
