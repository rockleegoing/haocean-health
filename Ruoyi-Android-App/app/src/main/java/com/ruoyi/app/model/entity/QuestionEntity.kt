package com.ruoyi.app.model.entity

import android.os.Parcelable
import com.chad.library.adapter.base.entity.SectionEntity
import kotlinx.android.parcel.Parcelize

@kotlinx.serialization.Serializable
@Parcelize
data class QuestionEntity(
    val question: String = "",
    var questionContent: String = "",
    val imageResource: Int = 0,
    val headerTitle: String = "",
    val beginList: Boolean = false,
    val endList: Boolean = false,
) : SectionEntity, Parcelable {
    override val isHeader: Boolean
        get() = headerTitle.isNotBlank()
}