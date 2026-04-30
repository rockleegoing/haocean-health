package com.ruoyi.app.feature.law.model

/**
 * 法律法规列表数据项
 */
sealed class LawListItem {
    /**
     * 分组标题
     */
    data class GroupHeader(val name: String) : LawListItem()

    /**
     * 法律类型项
     */
    data class LawItem(
        val id: Long,
        val name: String,
        val type: LawType
    ) : LawListItem()
}

/**
 * 法律类型枚举
 */
enum class LawType {
    /** 综合法律条例 */
    COMPREHENSIVE,
    /** 监管类型 */
    SUPERVISION
}
