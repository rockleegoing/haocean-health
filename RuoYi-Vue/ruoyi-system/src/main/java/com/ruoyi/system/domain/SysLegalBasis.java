package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 定性依据对象 sys_legal_basis
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public class SysLegalBasis extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 定性依据ID */
    private Long basisId;

    /** 编号（如：001） */
    @Excel(name = "编号")
    private String basisNo;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 违法类型 */
    @Excel(name = "违法类型")
    private String violationType;

    /** 颁发机构 */
    @Excel(name = "颁发机构")
    private String issuingAuthority;

    /** 实施时间 */
    @Excel(name = "实施时间")
    private String effectiveDate;

    /** 效级 */
    @Excel(name = "效级")
    private String legalLevel;

    /** 条款内容（JSON数组） */
    private String clauses;

    /** 法律责任 */
    private String legalLiability;

    /** 裁量标准 */
    private String discretionStandard;

    /** 关联法律法规ID */
    private Long regulationId;

    /** 状态：0正常 1停用 */
    @Excel(name = "状态")
    private String status;

    /** 删除标志 */
    private String delFlag;

    /** 增量同步起始时间（前端传入，非数据库字段） */
    private String updateTimeFrom;

    public void setBasisId(Long basisId) {
        this.basisId = basisId;
    }

    public Long getBasisId() {
        return basisId;
    }

    public void setBasisNo(String basisNo) {
        this.basisNo = basisNo;
    }

    public String getBasisNo() {
        return basisNo;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }

    public String getViolationType() {
        return violationType;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setLegalLevel(String legalLevel) {
        this.legalLevel = legalLevel;
    }

    public String getLegalLevel() {
        return legalLevel;
    }

    public void setClauses(String clauses) {
        this.clauses = clauses;
    }

    public String getClauses() {
        return clauses;
    }

    public void setLegalLiability(String legalLiability) {
        this.legalLiability = legalLiability;
    }

    public String getLegalLiability() {
        return legalLiability;
    }

    public void setDiscretionStandard(String discretionStandard) {
        this.discretionStandard = discretionStandard;
    }

    public String getDiscretionStandard() {
        return discretionStandard;
    }

    public void setRegulationId(Long regulationId) {
        this.regulationId = regulationId;
    }

    public Long getRegulationId() {
        return regulationId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public String getUpdateTimeFrom() {
        return updateTimeFrom;
    }

    public void setUpdateTimeFrom(String updateTimeFrom) {
        this.updateTimeFrom = updateTimeFrom;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("basisId", getBasisId())
                .append("basisNo", getBasisNo())
                .append("title", getTitle())
                .append("violationType", getViolationType())
                .append("issuingAuthority", getIssuingAuthority())
                .append("effectiveDate", getEffectiveDate())
                .append("legalLevel", getLegalLevel())
                .append("clauses", getClauses())
                .append("legalLiability", getLegalLiability())
                .append("discretionStandard", getDiscretionStandard())
                .append("regulationId", getRegulationId())
                .append("status", getStatus())
                .append("delFlag", getDelFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
