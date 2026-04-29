package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "normative_language",
    indices = [Index(value = ["primaryCategory"])]
)
data class NormativeLanguageEntity(
    @PrimaryKey val id: Long,
    val standardCode: String?,
    val standardPhrase: String,
    val supervisoryOpinion: String?,
    val basisType: Int?,
    val primaryCategory: Long?
)
