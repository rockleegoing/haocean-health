package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index

/**
 * 规范用语与监管事项绑定实体
 * 对应数据库表：normative_matter_bind
 */
@Entity(
    tableName = "normative_matter_bind",
    primaryKeys = ["normativeLanguageId", "regulatoryMatterId"],
    indices = [Index(value = ["normativeLanguageId"]), Index(value = ["regulatoryMatterId"])]
)
data class NormativeMatterBindEntity(
    val normativeLanguageId: Long,       // 规范用语编号
    val regulatoryMatterId: Long,      // 监管事项编号
    val basisType: Int?                // 依据类型(1定性依据/0处理依据)
)
