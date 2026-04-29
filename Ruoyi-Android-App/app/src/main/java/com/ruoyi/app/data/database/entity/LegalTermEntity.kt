package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 法律条款实体
 * 对应数据库表：legal_term
 */
@Entity(
    tableName = "legal_term",
    indices = [Index(value = ["lawId"]), Index(value = ["zhCode"])]
)
data class LegalTermEntity(
    @PrimaryKey val id: Long,           // 条款ID
    val lawId: Long,                    // 法律编号
    val part: Int?,                    // 编
    val partBranch: Int?,               // 分编
    val chapter: Int?,                  // 章
    val quarter: Int?,                 // 节
    val article: Int?,                  // 条
    val section: Int?,                  // 款
    val subparagraph: Int?,             // 项
    val item: Int?,                    // 目
    val zhCode: String?,               // 中文条款编码
    val content: String?                // 条款内容
)
