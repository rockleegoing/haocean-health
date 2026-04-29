package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 规范用语实体
 * 对应数据库表：normative_language
 */
@Entity(
    tableName = "normative_language",
    indices = [Index(value = ["primaryCategory"])]
)
data class NormativeLanguageEntity(
    @PrimaryKey val id: Long,                     // 主键ID
    val standardCode: String?,                   // 规范用语代码
    val standardPhrase: String,                  // 规范用语（违法事实）
    val supervisoryOpinion: String?,             // 监督意见
    val basisType: Int?,                        // 依据类型(1定性依据/0处理依据)
    val primaryCategory: Long?                   // 一级分类编号
)
