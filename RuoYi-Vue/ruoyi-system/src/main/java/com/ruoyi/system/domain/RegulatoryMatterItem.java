package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 监管事项详情对象 regulatory_matter_item
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
public class RegulatoryMatterItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 主表id */
    @Excel(name = "主表id")
    private Long regulatoryMatterId;

    /** 主表名称（非数据库字段） */
    private String mainName;

    /** 数据源id */
    @Excel(name = "数据源id")
    private String countyItemId;

    /** 类型 */
    @Excel(name = "类型")
    private String codeType;

    /** 监管子项编码 */
    @Excel(name = "监管子项编码")
    private String itemCode;

    /** 详情名称 */
    @Excel(name = "详情名称")
    private String itemName;

    /** 描述 */
    @Excel(name = "描述")
    private String according;

    /** 是否标记删除，flag 0否 1是 */
    @Excel(name = "是否标记删除，flag 0否 1是")
    private Integer isDeleted;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setRegulatoryMatterId(Long regulatoryMatterId) 
    {
        this.regulatoryMatterId = regulatoryMatterId;
    }

    public Long getRegulatoryMatterId()
    {
        return regulatoryMatterId;
    }

    public void setRegulatoryMatterId(Long regulatoryMatterId)
    {
        this.regulatoryMatterId = regulatoryMatterId;
    }

    public String getMainName()
    {
        return mainName;
    }

    public void setMainName(String mainName)
    {
        this.mainName = mainName;
    }

    public void setCountyItemId(String countyItemId) 
    {
        this.countyItemId = countyItemId;
    }

    public String getCountyItemId() 
    {
        return countyItemId;
    }

    public void setCodeType(String codeType) 
    {
        this.codeType = codeType;
    }

    public String getCodeType() 
    {
        return codeType;
    }

    public void setItemCode(String itemCode) 
    {
        this.itemCode = itemCode;
    }

    public String getItemCode() 
    {
        return itemCode;
    }

    public void setItemName(String itemName) 
    {
        this.itemName = itemName;
    }

    public String getItemName() 
    {
        return itemName;
    }

    public void setAccording(String according) 
    {
        this.according = according;
    }

    public String getAccording() 
    {
        return according;
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
            .append("regulatoryMatterId", getRegulatoryMatterId())
            .append("countyItemId", getCountyItemId())
            .append("codeType", getCodeType())
            .append("itemCode", getItemCode())
            .append("itemName", getItemName())
            .append("according", getAccording())
            .append("isDeleted", getIsDeleted())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
