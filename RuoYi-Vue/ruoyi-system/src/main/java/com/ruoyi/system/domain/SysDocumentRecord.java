package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 文书记录对象 sys_document_record
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public class SysDocumentRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 文书编号 */
    @Excel(name = "文书编号")
    private String documentNo;

    /** 模板ID */
    private Long templateId;

    /** 模板编码 */
    @Excel(name = "模板编码")
    private String templateCode;

    /** 执法记录ID */
    private Long enforcementRecordId;

    /** 单位ID */
    private Long unitId;

    /** 变量值(JSON格式) */
    private String variables;

    /** 文件路径 */
    private String filePath;

    /** 文件URL */
    @Excel(name = "文件URL")
    private String fileUrl;

    /** 签名信息(JSON格式) */
    private String signatures;

    /** 状态:0草稿,1已完成,2已签发 */
    @Excel(name = "状态:0草稿,1已完成,2已签发")
    private String status;

    /** 同步状态:0未同步,1已同步,2同步失败 */
    @Excel(name = "同步状态:0未同步,1已同步,2同步失败")
    private String syncStatus;

    /** 删除标志:0存在,1删除 */
    private String delFlag;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    public String getDocumentNo() {
        return documentNo;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setEnforcementRecordId(Long enforcementRecordId) {
        this.enforcementRecordId = enforcementRecordId;
    }

    public Long getEnforcementRecordId() {
        return enforcementRecordId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setVariables(String variables) {
        this.variables = variables;
    }

    public String getVariables() {
        return variables;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setSignatures(String signatures) {
        this.signatures = signatures;
    }

    public String getSignatures() {
        return signatures;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("documentNo", getDocumentNo())
            .append("templateId", getTemplateId())
            .append("templateCode", getTemplateCode())
            .append("enforcementRecordId", getEnforcementRecordId())
            .append("unitId", getUnitId())
            .append("variables", getVariables())
            .append("filePath", getFilePath())
            .append("fileUrl", getFileUrl())
            .append("signatures", getSignatures())
            .append("status", getStatus())
            .append("syncStatus", getSyncStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
