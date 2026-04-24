package com.ruoyi.app.feature.lawenforcement.model

object RecordStatus {
    const val DRAFT = "DRAFT"           // 草稿/待上报
    const val SUBMITTED = "SUBMITTED"   // 已上报
    const val APPROVED = "APPROVED"    // 已审核
    const val REJECTED = "REJECTED"    // 已驳回
}

object EvidenceType {
    const val PHOTO = "photo"
    const val AUDIO = "audio"
    const val VIDEO = "video"
}

object SyncStatus {
    const val PENDING = "PENDING"
    const val SYNCING = "SYNCING"
    const val SUCCESS = "SUCCESS"
    const val FAILED = "FAILED"
}