package com.ruoyi.app.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ruoyi.app.R
import com.ruoyi.app.model.entity.NoticeItemEntity

class NoticeAdapter : BaseQuickAdapter<NoticeItemEntity, BaseViewHolder>(R.layout.item_message),
    LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: NoticeItemEntity) {
        holder.setText(R.id.tv_time, item.createTime)
        holder.setText(R.id.tv_title, item.noticeTitle)
        holder.setText(R.id.tv_introduction, item.noticeContent)
    }
}