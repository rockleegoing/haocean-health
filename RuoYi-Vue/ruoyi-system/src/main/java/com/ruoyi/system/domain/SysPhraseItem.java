package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 规范用语项对象 sys_phrase_item
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public class SysPhraseItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 项ID */
    private Long itemId;

    /** 所属书本ID */
    @Excel(name = "所属书本ID")
    private Long bookId;

    /** 项名称 */
    @Excel(name = "项名称")
    private String itemName;

    /** 项编码 */
    @Excel(name = "项编码")
    private String itemCode;

    /** 项描述 */
    @Excel(name = "项描述")
    private String itemDesc;

    /** 环节类型:CHECK_BEFORE-检查前,CHECK_ING-检查中,CHECK_AFTER-检查后 */
    @Excel(name = "环节类型")
    private String phaseType;

    /** 场景类型 */
    @Excel(name = "场景类型")
    private String sceneType;

    /** 适用行业编码 */
    @Excel(name = "行业编码")
    private String industryCode;

    /** 排序号 */
    @Excel(name = "排序号")
    private Integer sortOrder;

    /** 状态:0正常,1停用 */
    @Excel(name = "状态:0正常,1停用")
    private String status;

    /** 版本号 */
    private Integer version;

    /** 删除标志:0存在,1删除 */
    private String delFlag;

    /** 书本名称（不映射数据库列，通过JOIN查询获取） */
    @Excel(name = "书本名称")
    private String bookName;

    public void setItemId(Long itemId)
    {
        this.itemId = itemId;
    }

    public Long getItemId()
    {
        return itemId;
    }

    public void setBookId(Long bookId)
    {
        this.bookId = bookId;
    }

    public Long getBookId()
    {
        return bookId;
    }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    public String getItemName()
    {
        return itemName;
    }

    public void setItemCode(String itemCode)
    {
        this.itemCode = itemCode;
    }

    public String getItemCode()
    {
        return itemCode;
    }

    public void setItemDesc(String itemDesc)
    {
        this.itemDesc = itemDesc;
    }

    public String getItemDesc()
    {
        return itemDesc;
    }

    public void setPhaseType(String phaseType)
    {
        this.phaseType = phaseType;
    }

    public String getPhaseType()
    {
        return phaseType;
    }

    public void setSceneType(String sceneType)
    {
        this.sceneType = sceneType;
    }

    public String getSceneType()
    {
        return sceneType;
    }

    public void setIndustryCode(String industryCode)
    {
        this.industryCode = industryCode;
    }

    public String getIndustryCode()
    {
        return industryCode;
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

    public String getBookName()
    {
        return bookName;
    }

    public void setBookName(String bookName)
    {
        this.bookName = bookName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("itemId", getItemId())
            .append("bookId", getBookId())
            .append("itemName", getItemName())
            .append("itemCode", getItemCode())
            .append("itemDesc", getItemDesc())
            .append("phaseType", getPhaseType())
            .append("sceneType", getSceneType())
            .append("industryCode", getIndustryCode())
            .append("sortOrder", getSortOrder())
            .append("status", getStatus())
            .append("version", getVersion())
            .append("delFlag", getDelFlag())
            .append("bookName", getBookName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
