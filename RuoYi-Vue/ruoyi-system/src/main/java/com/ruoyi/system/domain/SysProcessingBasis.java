package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 处理依据对象 sys_processing_basis
 *
 * @author ruoyi
 * @date 2026-04-28
 */
public class SysProcessingBasis extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 处理依据ID */
    private Long basisId;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 关联法律法规ID */
    private Long regulationId;

    /** 状态：0正常 1停用 */
    @Excel(name = "状态")
    private String status;

    /** 删除标志 */
    private String delFlag;

    /** 增量同步起始时间（前端传入，非数据库字段） */
    private String updateTimeFrom;

    public void setBasisId(Long basisId) {
        this.basisId = basisId;
    }

    public Long getBasisId() {
        return basisId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setRegulationId(Long regulationId) {
        this.regulationId = regulationId;
    }

    public Long getRegulationId() {
        return regulationId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public String getUpdateTimeFrom() {
        return updateTimeFrom;
    }

    public void setUpdateTimeFrom(String updateTimeFrom) {
        this.updateTimeFrom = updateTimeFrom;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("basisId", getBasisId())
                .append("title", getTitle())
                .append("regulationId", getRegulationId())
                .append("status", getStatus())
                .append("delFlag", getDelFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
