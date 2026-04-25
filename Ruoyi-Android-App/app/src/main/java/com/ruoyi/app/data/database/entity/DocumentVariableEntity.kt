package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 文书模板变量实体
 */
@Entity(tableName = "document_variable")
data class DocumentVariableEntity(
    @PrimaryKey
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
