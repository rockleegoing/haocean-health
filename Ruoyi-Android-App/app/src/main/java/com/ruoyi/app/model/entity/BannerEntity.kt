package com.ruoyi.app.model.entity

import com.stx.xhb.androidx.entity.BaseBannerInfo

/**
 * 横幅实体
 */
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
