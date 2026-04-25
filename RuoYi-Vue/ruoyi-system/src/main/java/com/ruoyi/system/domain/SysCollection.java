package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 收藏对象 sys_collection
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public class SysCollection extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 收藏ID */
    private Long collectionId;

    /** 用户ID */
    private Long userId;

    /** 目标类型：regulation/article/basis */
    private String targetType;

    /** 目标ID */
    private Long targetId;

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }

    public Long getCollectionId() {
        return collectionId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Long getTargetId() {
        return targetId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("collectionId", getCollectionId())
                .append("userId", getUserId())
                .append("targetType", getTargetType())
                .append("targetId", getTargetId())
                .append("createTime", getCreateTime())
                .toString();
    }
}
