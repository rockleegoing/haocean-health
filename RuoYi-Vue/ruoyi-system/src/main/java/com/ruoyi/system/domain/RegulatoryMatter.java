package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 监管事项对象 regulatory_matter
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
public class RegulatoryMatter extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 数据源id */
    @Excel(name = "数据源id")
    private String countyId;

    /** mainCode */
    @Excel(name = "mainCode")
    private String mainCode;

    /** 监管事项名称 */
    @Excel(name = "监管事项名称")
    private String mainName;

    /** 是否删除,flag 0否，1是 */
    @Excel(name = "是否删除,flag 0否，1是")
    private Integer isDeleted;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setCountyId(String countyId) 
    {
        this.countyId = countyId;
    }

    public String getCountyId() 
    {
        return countyId;
    }

    public void setMainCode(String mainCode) 
    {
        this.mainCode = mainCode;
    }

    public String getMainCode() 
    {
        return mainCode;
    }

    public void setMainName(String mainName) 
    {
        this.mainName = mainName;
    }

    public String getMainName() 
    {
        return mainName;
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
            .append("countyId", getCountyId())
            .append("mainCode", getMainCode())
            .append("mainName", getMainName())
            .append("isDeleted", getIsDeleted())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
