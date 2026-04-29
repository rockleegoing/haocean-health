package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 法律法规类型对象 law_type
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
public class LawType extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 父类型ID（0为顶级） */
    @Excel(name = "父类型ID", readConverterExp = "0=为顶级")
    private Long parentId;

    /** 祖先路径（如：0,1,2） */
    @Excel(name = "祖先路径", readConverterExp = "如=：0,1,2")
    private String ancestors;

    /** 类型名称 */
    @Excel(name = "类型名称")
    private String name;

    /** 图标（用于Android显示） */
    @Excel(name = "图标", readConverterExp = "用=于Android显示")
    private String icon;

    /** 排序 */
    @Excel(name = "排序")
    private Long sort;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setParentId(Long parentId) 
    {
        this.parentId = parentId;
    }

    public Long getParentId() 
    {
        return parentId;
    }

    public void setAncestors(String ancestors) 
    {
        this.ancestors = ancestors;
    }

    public String getAncestors() 
    {
        return ancestors;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setIcon(String icon) 
    {
        this.icon = icon;
    }

    public String getIcon() 
    {
        return icon;
    }

    public void setSort(Long sort) 
    {
        this.sort = sort;
    }

    public Long getSort() 
    {
        return sort;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    /**
     * 计算祖先路径
     * @return 祖先路径字符串，格式：0,1,2
     */
    public String calcAncestors() {
        if (this.parentId == null || this.parentId == 0) {
            return "0";
        }
        // 如果父节点存在，需要在 Service 层查询父节点的 ancestors
        return String.valueOf(this.parentId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("parentId", getParentId())
            .append("ancestors", getAncestors())
            .append("name", getName())
            .append("icon", getIcon())
            .append("sort", getSort())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
