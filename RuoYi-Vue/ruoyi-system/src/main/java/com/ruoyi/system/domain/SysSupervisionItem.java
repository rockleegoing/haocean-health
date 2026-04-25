package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 监管事项对象 sys_supervision_item
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public class SysSupervisionItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 事项ID */
    private Long itemId;

    /** 事项编码 */
    @Excel(name = "事项编码")
    private String itemNo;

    /** 事项名称 */
    @Excel(name = "事项名称")
    private String name;

    /** 父级事项ID */
    private Long parentId;

    /** 监管类型ID */
    @Excel(name = "监管类型ID")
    private Long categoryId;

    /** 监管类型名称 */
    @Excel(name = "监管类型名称")
    private String categoryName;

    /** 监管要求描述 */
    @Excel(name = "监管要求描述")
    private String description;

    /** 法律依据 */
    @Excel(name = "法律依据")
    private String legalBasis;

    /** 排序 */
    @Excel(name = "排序")
    private Integer sortOrder;

    /** 状态:0正常,1停用 */
    @Excel(name = "状态:0正常,1停用")
    private String status;

    public void setItemId(Long itemId)
    {
        this.itemId = itemId;
    }

    public Long getItemId()
    {
        return itemId;
    }

    public void setItemNo(String itemNo)
    {
        this.itemNo = itemNo;
    }

    public String getItemNo()
    {
        return itemNo;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public void setLegalBasis(String legalBasis)
    {
        this.legalBasis = legalBasis;
    }

    public String getLegalBasis()
    {
        return legalBasis;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("itemId", getItemId())
            .append("itemNo", getItemNo())
            .append("name", getName())
            .append("parentId", getParentId())
            .append("categoryId", getCategoryId())
            .append("categoryName", getCategoryName())
            .append("description", getDescription())
            .append("legalBasis", getLegalBasis())
            .append("sortOrder", getSortOrder())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
