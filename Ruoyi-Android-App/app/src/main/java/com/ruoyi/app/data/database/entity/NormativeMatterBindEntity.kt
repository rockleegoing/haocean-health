package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "normative_matter_bind",
    primaryKeys = ["normativeLanguageId", "regulatoryMatterId"],
    indices = [Index(value = ["normativeLanguageId"]), Index(value = ["regulatoryMatterId"])]
)
data class NormativeMatterBindEntity(
    val normativeLanguageId: Long,
    val regulatoryMatterId: Long,
    val basisType: Int?
)
