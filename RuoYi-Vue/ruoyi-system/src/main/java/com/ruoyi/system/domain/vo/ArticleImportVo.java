package com.ruoyi.system.domain.vo;

/**
 * 法规条款导入VO
 *
 * @author ruoyi
 * @date 2026-04-26
 */
public class ArticleImportVo {
    /** 条款号 */
    private String articleNo;

    /** 条款内容 */
    private String content;

    /** 排序 */
    private Integer sortOrder;

    public String getArticleNo() {
        return articleNo;
    }

    public void setArticleNo(String articleNo) {
        this.articleNo = articleNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}