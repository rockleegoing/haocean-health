package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index

/**
 * 规范用语与法律条款绑定实体
 * 对应数据库表：normative_term_bind
 */
@Entity(
    tableName = "normative_term_bind",
    primaryKeys = ["legalTermId", "normativeLanguageId"],
    indices = [Index(value = ["normativeLanguageId"]), Index(value = ["legalTermId"])]
)
data class NormativeTermBindEntity(
    val legalTermId: Long,              // 法律条款ID
    val normativeLanguageId: Long,       // 规范用语编号
    val basisType: Int?                 // 依据类型(1定性依据/0处理依据)
)
