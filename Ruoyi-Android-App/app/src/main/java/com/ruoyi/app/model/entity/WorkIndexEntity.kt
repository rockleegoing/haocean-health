package com.ruoyi.app.model.entity

import com.stx.xhb.androidx.entity.BaseBannerInfo

@kotlinx.serialization.Serializable
data class WorkIndexEntity(
    val msg: String,
    val code: Int,
    val data: WorkIndexDataEntity
)

@kotlinx.serialization.Serializable
data class WorkIndexDataEntity(
    val function: FunctionEntity,
    val banner: List<BannerEntity>
)

@kotlinx.serialization.Serializable
data class BannerEntity(
    val id: Long,
    val source: String,
    val page: String,
    val title: String
) : BaseBannerInfo {
    override fun getXBannerUrl(): Any {
        return source
    }

    override fun getXBannerTitle(): String {
        return title
    }
}

@kotlinx.serialization.Serializable
data class FunctionEntity(
    val plug: BasicEntity,
    val basic: BasicEntity
)

@kotlinx.serialization.Serializable
data class BasicEntity(
    val data: List<BannerEntity>,
    val title: String
)
