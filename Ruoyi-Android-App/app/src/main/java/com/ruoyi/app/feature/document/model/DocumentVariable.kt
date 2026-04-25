package com.ruoyi.app.feature.document.model

/**
 * 文书变量数据模型
 */
data class DocumentVariable(
    val id: Long,
    val templateId: Long,
    val variableName: String,
    val variableLabel: String?,
    val variableType: String = "TEXT",
    val required: String = "1",
    val defaultValue: String?,
    val options: String?,
    val sortOrder: Int = 0,
    val maxLength: Int?
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
