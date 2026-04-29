package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 法律条款对象 legal_term
 * 
 * @author ruoyi
 * @date 2026-04-29
 */
public class LegalTerm extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 法律编号 */
    @Excel(name = "法律编号")
    private Long lawId;

    /** 编 */
    @Excel(name = "编")
    private Integer part;

    /** 分编 */
    @Excel(name = "分编")
    private Integer partBranch;

    /** 章 */
    @Excel(name = "章")
    private Integer chapter;

    /** 节 */
    @Excel(name = "节")
    private Integer quarter;

    /** 条 */
    @Excel(name = "条")
    private Integer article;

    /** 款 */
    @Excel(name = "款")
    private Integer section;

    /** 项 */
    @Excel(name = "项")
    private Integer subparagraph;

    /** 目 */
    @Excel(name = "目")
    private Integer item;

    /** 中文条款编码 */
    @Excel(name = "中文条款编码")
    private String zhCode;

    /** 条款内容 */
    @Excel(name = "条款内容")
    private String content;

    /** 远程库id */
    @Excel(name = "远程库id")
    private Long stashTermId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setLawId(Long lawId) 
    {
        this.lawId = lawId;
    }

    public Long getLawId() 
    {
        return lawId;
    }

    public void setPart(Integer part) 
    {
        this.part = part;
    }

    public Integer getPart() 
    {
        return part;
    }

    public void setPartBranch(Integer partBranch) 
    {
        this.partBranch = partBranch;
    }

    public Integer getPartBranch() 
    {
        return partBranch;
    }

    public void setChapter(Integer chapter) 
    {
        this.chapter = chapter;
    }

    public Integer getChapter() 
    {
        return chapter;
    }

    public void setQuarter(Integer quarter) 
    {
        this.quarter = quarter;
    }

    public Integer getQuarter() 
    {
        return quarter;
    }

    public void setArticle(Integer article) 
    {
        this.article = article;
    }

    public Integer getArticle() 
    {
        return article;
    }

    public void setSection(Integer section) 
    {
        this.section = section;
    }

    public Integer getSection() 
    {
        return section;
    }

    public void setSubparagraph(Integer subparagraph) 
    {
        this.subparagraph = subparagraph;
    }

    public Integer getSubparagraph() 
    {
        return subparagraph;
    }

    public void setItem(Integer item) 
    {
        this.item = item;
    }

    public Integer getItem() 
    {
        return item;
    }

    public void setZhCode(String zhCode) 
    {
        this.zhCode = zhCode;
    }

    public String getZhCode() 
    {
        return zhCode;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setStashTermId(Long stashTermId) 
    {
        this.stashTermId = stashTermId;
    }

    public Long getStashTermId() 
    {
        return stashTermId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("lawId", getLawId())
            .append("part", getPart())
            .append("partBranch", getPartBranch())
            .append("chapter", getChapter())
            .append("quarter", getQuarter())
            .append("article", getArticle())
            .append("section", getSection())
            .append("subparagraph", getSubparagraph())
            .append("item", getItem())
            .append("zhCode", getZhCode())
            .append("content", getContent())
            .append("updateTime", getUpdateTime())
            .append("stashTermId", getStashTermId())
            .toString();
    }
}
