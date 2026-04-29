package com.ruoyi.app.feature.document.model

/**
 * 文书变量数据模型
 */
data class DocumentVariable(
    val id: Long,                    // 变量ID
    val templateId: Long,            // 所属模板ID
    val variableName: String,       // 变量名称（字段名）
    val variableLabel: String?,     // 变量显示标签
    val variableType: String = "TEXT",  // 变量类型：TEXT/DATE/SELECT/SIGNATURE/PHOTO等
    val required: String = "1",     // 是否必填：1-必填，0-选填
    val defaultValue: String?,      // 默认值
    val options: String?,           // 选项列表（SELECT/RADIO/CHECKBOX时使用）
    val sortOrder: Int = 0,         // 排序序号
    val maxLength: Int?             // 最大长度限制
)

/**
 * 变量类型常量
 */
object VariableType {
    const val TEXT = "TEXT"
    const val DATE = "DATE"
    const val DATETIME = "DATETIME"
    const val SELECT = "SELECT"
    const val RADIO = "RADIO"
    const val CHECKBOX = "CHECKBOX"
    const val SIGNATURE = "SIGNATURE"
    const val PHOTO = "PHOTO"
}
