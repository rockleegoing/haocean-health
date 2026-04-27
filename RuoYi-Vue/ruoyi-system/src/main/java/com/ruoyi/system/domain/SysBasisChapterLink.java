package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 依据章节关联对象 sys_basis_chapter_link
 *
 * @author ruoyi
 * @date 2026-04-28
 */
public class SysBasisChapterLink extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 关联ID */
    private Long linkId;

    /** 关联类型：legal/processing */
    private String basisType;

    /** 依据ID */
    private Long basisId;

    /** 章节ID */
    private Long chapterId;

    /** 条款ID */
    private Long articleId;

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    public Long getLinkId() {
        return linkId;
    }

    public void setBasisType(String basisType) {
        this.basisType = basisType;
    }

    public String getBasisType() {
        return basisType;
    }

    public void setBasisId(Long basisId) {
        this.basisId = basisId;
    }

    public Long getBasisId() {
        return basisId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getArticleId() {
        return articleId;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("linkId=").append(getLinkId())
                .append(", basisType=").append(getBasisType())
                .append(", basisId=").append(getBasisId())
                .append(", chapterId=").append(getChapterId())
                .append(", articleId=").append(getArticleId())
                .toString();
    }
}
