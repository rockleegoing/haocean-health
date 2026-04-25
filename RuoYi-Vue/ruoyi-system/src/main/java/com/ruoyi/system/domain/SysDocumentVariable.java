package com.ruoyi.system.domain;

import java.io.Serializable;

/**
 * 文书模板变量对象 sys_document_variable
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public class SysDocumentVariable implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 模板ID */
    private Long templateId;

    /** 变量名称 */
    private String variableName;

    /** 变量标签 */
    private String variableLabel;

    /** 变量类型:string,number,date,select,textarea */
    private String variableType;

    /** 是否必填:0必填,1选填 */
    private String required;

    /** 默认值 */
    private String defaultValue;

    /** 选项值(JSON格式) */
    private String options;

    /** 排序号 */
    private Integer sortOrder;

    /** 最大长度 */
    private Integer maxLength;

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

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableLabel(String variableLabel) {
        this.variableLabel = variableLabel;
    }

    public String getVariableLabel() {
        return variableLabel;
    }

    public void setVariableType(String variableType) {
        this.variableType = variableType;
    }

    public String getVariableType() {
        return variableType;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getRequired() {
        return required;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getOptions() {
        return options;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }
}
