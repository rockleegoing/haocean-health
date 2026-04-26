package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 法律法规对象 sys_regulation
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public class SysRegulation extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 法律法规ID */
    private Long regulationId;

    /** 法律名称 */
    @Excel(name = "法律名称")
    private String title;

    /** 法律类型：法律/法规/规章/规范性文件/批复文件/标准 */
    @Excel(name = "法律类型")
    private String legalType;

    /** 监管类型列表（JSON数组） */
    @Excel(name = "监管类型")
    private String supervisionTypes;

    /** 发布日期 */
    @Excel(name = "发布日期")
    private String publishDate;

    /** 实施日期 */
    @Excel(name = "实施日期")
    private String effectiveDate;

    /** 颁发机构 */
    @Excel(name = "颁发机构")
    private String issuingAuthority;

    /** 完整内容 */
    private String content;

    /** 版本号 */
    private String version;

    /** 状态：0正常 1停用 */
    @Excel(name = "状态")
    private String status;

    /** 删除标志：0存在 1删除 */
    private String delFlag;

    /** 增量同步起始时间（前端传入，非数据库字段） */
    private String updateTimeFrom;

    public void setRegulationId(Long regulationId) {
        this.regulationId = regulationId;
    }

    public Long getRegulationId() {
        return regulationId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setLegalType(String legalType) {
        this.legalType = legalType;
    }

    public String getLegalType() {
        return legalType;
    }

    public void setSupervisionTypes(String supervisionTypes) {
        this.supervisionTypes = supervisionTypes;
    }

    public String getSupervisionTypes() {
        return supervisionTypes;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
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
                .append("regulationId", getRegulationId())
                .append("title", getTitle())
                .append("legalType", getLegalType())
                .append("supervisionTypes", getSupervisionTypes())
                .append("publishDate", getPublishDate())
                .append("effectiveDate", getEffectiveDate())
                .append("issuingAuthority", getIssuingAuthority())
                .append("content", getContent())
                .append("version", getVersion())
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
