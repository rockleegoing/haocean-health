package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "legal_term",
    indices = [Index(value = ["lawId"]), Index(value = ["zhCode"])]
)
data class LegalTermEntity(
    @PrimaryKey val id: Long,
    val lawId: Long,
    val part: Int?,
    val partBranch: Int?,
    val chapter: Int?,
    val quarter: Int?,
    val article: Int?,
    val section: Int?,
    val subparagraph: Int?,
    val item: Int?,
    val zhCode: String?,
    val content: String?
)
