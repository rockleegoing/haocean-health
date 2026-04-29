package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 监管事项执法分类绑定关系对象 regulatory_category_bind
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
public class RegulatoryCategoryBind extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 监管事项id */
    @Excel(name = "监管事项id")
    private Long matterId;

    /** 分类id */
    @Excel(name = "分类id")
    private Long categoryId;

    /** 监管事项名称（非数据库字段） */
    private String matterName;

    /** 行业分类名称（非数据库字段） */
    private String categoryName;

    public void setMatterId(Long matterId)
    {
        this.matterId = matterId;
    }

    public Long getMatterId() 
    {
        return matterId;
    }

    public void setCategoryId(Long categoryId) 
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }

    public String getMatterName()
    {
        return matterName;
    }

    public void setMatterName(String matterName)
    {
        this.matterName = matterName;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("matterId", getMatterId())
            .append("categoryId", getCategoryId())
            .toString();
    }
}
