package com.ruoyi.app.model.entity

import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi /**
 * 更新数据实体
 */
@kotlinx.serialization.Serializable
data class UpdateDataEntity(
    val ApkMd5: String = "",
    val ApkSize: Long = 1,
    val DownloadUrl: String = "",
    val HotUpdateMd5: String = "",
    val HotUpdateSize: Int = 1,
    val HotUpdateUrl: String = "",
    val ModifyContent: String = "",
    val UpdateStatus: Int = 1,
    val UpdateType: Int = 1,
    val UploadTime: String = "",
    val VersionCode: Int = 1,
    val VersionName: String = "1.0.0",
)
