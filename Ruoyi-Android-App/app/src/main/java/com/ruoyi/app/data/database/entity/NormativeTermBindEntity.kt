package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "normative_term_bind",
    primaryKeys = ["legalTermId", "normativeLanguageId"],
    indices = [Index(value = ["normativeLanguageId"]), Index(value = ["legalTermId"])]
)
data class NormativeTermBindEntity(
    val legalTermId: Long,
    val normativeLanguageId: Long,
    val basisType: Int?
)
