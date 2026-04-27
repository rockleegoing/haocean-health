package com.ruoyi.system.domain.vo;

import com.ruoyi.common.annotation.Excel;

/**
 * 法律类型 VO - API传输对象
 */
public class LegalTypeVo {

    @Excel(name = "类型ID")
    private Long typeId;

    @Excel(name = "类型编码")
    private String typeCode;

    @Excel(name = "类型名称")
    private String typeName;

    @Excel(name = "排序")
    private Integer sortOrder;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    // Getters and Setters
    public Long getTypeId() { return typeId; }
    public void setTypeId(Long typeId) { this.typeId = typeId; }
    public String getTypeCode() { return typeCode; }
    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}