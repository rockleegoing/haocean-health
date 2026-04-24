package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 规范用语书本对象 sys_phrase_book
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public class SysPhraseBook extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 书本ID */
    private Long bookId;

    /** 书本名称 */
    @Excel(name = "书本名称")
    private String bookName;

    /** 书本编码 */
    @Excel(name = "书本编码")
    private String bookCode;

    /** 书本描述 */
    @Excel(name = "书本描述")
    private String bookDesc;

    /** 适用行业编码 */
    @Excel(name = "行业编码")
    private String industryCode;

    /** 适用行业名称 */
    @Excel(name = "行业名称")
    private String industryName;

    /** 封面图片URL */
    @Excel(name = "封面图片")
    private String coverUrl;

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

    public void setBookId(Long bookId)
    {
        this.bookId = bookId;
    }

    public Long getBookId()
    {
        return bookId;
    }

    public void setBookName(String bookName)
    {
        this.bookName = bookName;
    }

    public String getBookName()
    {
        return bookName;
    }

    public void setBookCode(String bookCode)
    {
        this.bookCode = bookCode;
    }

    public String getBookCode()
    {
        return bookCode;
    }

    public void setBookDesc(String bookDesc)
    {
        this.bookDesc = bookDesc;
    }

    public String getBookDesc()
    {
        return bookDesc;
    }

    public void setIndustryCode(String industryCode)
    {
        this.industryCode = industryCode;
    }

    public String getIndustryCode()
    {
        return industryCode;
    }

    public void setIndustryName(String industryName)
    {
        this.industryName = industryName;
    }

    public String getIndustryName()
    {
        return industryName;
    }

    public void setCoverUrl(String coverUrl)
    {
        this.coverUrl = coverUrl;
    }

    public String getCoverUrl()
    {
        return coverUrl;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("bookId", getBookId())
            .append("bookName", getBookName())
            .append("bookCode", getBookCode())
            .append("bookDesc", getBookDesc())
            .append("industryCode", getIndustryCode())
            .append("industryName", getIndustryName())
            .append("coverUrl", getCoverUrl())
            .append("sortOrder", getSortOrder())
            .append("status", getStatus())
            .append("version", getVersion())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
