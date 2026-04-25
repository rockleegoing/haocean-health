package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 文书套组对象 sys_document_group
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public class SysDocumentGroup extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 套组编码 */
    @Excel(name = "套组编码")
    private String groupCode;

    /** 套组名称 */
    @Excel(name = "套组名称")
    private String groupName;

    /** 套组类型 */
    @Excel(name = "套组类型")
    private String groupType;

    /** 包含的模板(JSON数组) */
    private String templates;

    /** 是否启用:0启用,1停用 */
    @Excel(name = "是否启用:0启用,1停用")
    private String isActive;

    /** 删除标志:0存在,1删除 */
    private String delFlag;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setTemplates(String templates) {
        this.templates = templates;
    }

    public String getTemplates() {
        return templates;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("groupCode", getGroupCode())
            .append("groupName", getGroupName())
            .append("groupType", getGroupType())
            .append("templates", getTemplates())
            .append("isActive", getIsActive())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
