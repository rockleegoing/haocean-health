package com.ruoyi.system.domain.dict;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 监管类型对象 sys_supervision_type
 */
public class SysSupervisionType extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 类型ID */
    private Long typeId;

    /** 类型编码 */
    @Excel(name = "类型编码")
    private String typeCode;

    /** 类型名称 */
    @Excel(name = "类型名称")
    private String typeName;

    /** 排序 */
    @Excel(name = "排序")
    private Integer sortOrder;

    /** 状态：0正常 1停用 */
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("typeId", getTypeId())
                .append("typeCode", getTypeCode())
                .append("typeName", getTypeName())
                .append("sortOrder", getSortOrder())
                .append("status", getStatus())
                .toString();
    }
}