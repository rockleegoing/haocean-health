package com.ruoyi.app.model.entity

import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi /**
 * 工作台数据实体
 */
@kotlinx.serialization.Serializable
data class WorkIndexDataEntity(
    val function: FunctionEntity,
    val banner: List<BannerEntity>
)
