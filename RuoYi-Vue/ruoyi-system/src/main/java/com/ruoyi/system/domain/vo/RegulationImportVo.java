package com.ruoyi.system.domain.vo;

import java.util.List;

/**
 * 法律法规导入VO
 *
 * @author ruoyi
 * @date 2026-04-26
 */
public class RegulationImportVo {
    /** 法律名称 */
    private String title;

    /** 法律类型 */
    private String legalType;

    /** 监管类型 */
    private String supervisionTypes;

    /** 发布日期 */
    private String publishDate;

    /** 实施日期 */
    private String effectiveDate;

    /** 颁发机构 */
    private String issuingAuthority;

    /** 完整内容 */
    private String content;

    /** 章节列表 */
    private List<ChapterImportVo> chapters;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLegalType() {
        return legalType;
    }

    public void setLegalType(String legalType) {
        this.legalType = legalType;
    }

    public String getSupervisionTypes() {
        return supervisionTypes;
    }

    public void setSupervisionTypes(String supervisionTypes) {
        this.supervisionTypes = supervisionTypes;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ChapterImportVo> getChapters() {
        return chapters;
    }

    public void setChapters(List<ChapterImportVo> chapters) {
        this.chapters = chapters;
    }
}