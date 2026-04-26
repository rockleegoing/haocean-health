package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 文书模板与行业分类关联对象 sys_document_template_industry
 *
 * @author ruoyi
 * @date 2026-04-26
 */
public class SysDocumentTemplateIndustry {

    private Long id;
    private Long templateId;
    private Long industryCategoryId;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setIndustryCategoryId(Long industryCategoryId) {
        this.industryCategoryId = industryCategoryId;
    }

    public Long getIndustryCategoryId() {
        return industryCategoryId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("templateId", getTemplateId())
            .append("industryCategoryId", getIndustryCategoryId())
            .toString();
    }
}
