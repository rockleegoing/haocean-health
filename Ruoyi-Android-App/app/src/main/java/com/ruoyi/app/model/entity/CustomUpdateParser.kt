package com.ruoyi.app.model.entity

import com.xuexiang.xupdate.entity.UpdateEntity
import com.xuexiang.xupdate.listener.IUpdateParseCallback
import com.xuexiang.xupdate.proxy.IUpdateParser
import kotlinx.serialization.json.Json


class CustomUpdateParser(var versionCode: Int) : IUpdateParser {

    override fun parseJson(json: String): UpdateEntity {
        val result = Json.decodeFromString<com.ruoyi.app.model.entity.UpdateEntity>(json)
        return run {
            val data = result.data
            val hasUpdate = data.VersionCode > versionCode
            val UpdateStatus = data.UpdateStatus
            var isIgnorable = false
            if (UpdateStatus == 2) {
                isIgnorable = true
            }

            UpdateEntity()
                .setHasUpdate(hasUpdate)
                .setIsIgnorable(isIgnorable)
                .setVersionCode(result.data.VersionCode)
                .setVersionName(result.data.VersionName)
                .setUpdateContent(result.data.ModifyContent)
                .setDownloadUrl(result.data.DownloadUrl)
                .setSize(result.data.ApkSize)
        }
    }

    override fun parseJson(json: String, callback: IUpdateParseCallback) {

    }

    override fun isAsyncParser(): Boolean {
        return false
    }
}
