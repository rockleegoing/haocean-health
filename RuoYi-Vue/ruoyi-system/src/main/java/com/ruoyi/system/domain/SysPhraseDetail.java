package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 规范用语项明细对象 sys_phrase_detail
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public class SysPhraseDetail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 明细ID */
    private Long detailId;

    /** 所属项ID */
    @Excel(name = "所属项ID")
    private Long itemId;

    /** 明细标题 */
    @Excel(name = "明细标题")
    private String detailTitle;

    /** 用语内容 */
    @Excel(name = "用语内容")
    private String detailContent;

    /** 明细类型:TEXT-文本,HTML-HTML内容 */
    @Excel(name = "明细类型")
    private String detailType;

    /** 排序号 */
    @Excel(name = "排序号")
    private Integer sortOrder;

    /** 版本号 */
    private Integer version;

    /** 删除标志:0存在,1删除 */
    private String delFlag;

    /** 项名称（不映射数据库列，通过JOIN查询获取） */
    @Excel(name = "项名称")
    private String itemName;

    public void setDetailId(Long detailId)
    {
        this.detailId = detailId;
    }

    public Long getDetailId()
    {
        return detailId;
    }

    public void setItemId(Long itemId)
    {
        this.itemId = itemId;
    }

    public Long getItemId()
    {
        return itemId;
    }

    public void setDetailTitle(String detailTitle)
    {
        this.detailTitle = detailTitle;
    }

    public String getDetailTitle()
    {
        return detailTitle;
    }

    public void setDetailContent(String detailContent)
    {
        this.detailContent = detailContent;
    }

    public String getDetailContent()
    {
        return detailContent;
    }

    public void setDetailType(String detailType)
    {
        this.detailType = detailType;
    }

    public String getDetailType()
    {
        return detailType;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }

    public Integer getVersion()
    {
        return version;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public String getItemName()
    {
        return itemName;
    }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("detailId", getDetailId())
            .append("itemId", getItemId())
            .append("detailTitle", getDetailTitle())
            .append("detailContent", getDetailContent())
            .append("detailType", getDetailType())
            .append("sortOrder", getSortOrder())
            .append("version", getVersion())
            .append("delFlag", getDelFlag())
            .append("itemName", getItemName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
