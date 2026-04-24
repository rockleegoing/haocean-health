package com.ruoyi.app.model.entity

/**
 * 基础实体
 */
@kotlinx.serialization.Serializable
data class BasicEntity(
    val data: List<BannerEntity>,
    val title: String
)
