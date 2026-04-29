package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.TreeEntity;

/**
 * 规范用语类别对象 normative_category
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
public class NormativeCategory extends TreeEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private Long code;

    /** 分类名称 */
    @Excel(name = "分类名称")
    private String name;

    /** 父级分类编号 */
    @Excel(name = "父级分类编号")
    private Long parentCode;

    public void setCode(Long code) 
    {
        this.code = code;
    }

    public Long getCode() 
    {
        return code;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setParentCode(Long parentCode) 
    {
        this.parentCode = parentCode;
    }

    public Long getParentCode() 
    {
        return parentCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("code", getCode())
            .append("name", getName())
            .append("parentCode", getParentCode())
            .toString();
    }
}
