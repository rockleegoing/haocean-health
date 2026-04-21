package com.ruoyi.app.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ruoyi.app.R
import com.ruoyi.app.model.entity.LogsItemEntity

class OperationLogAdapter : BaseQuickAdapter<LogsItemEntity, BaseViewHolder>(R.layout.item_logs),
    LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: LogsItemEntity) {
        holder.setText(R.id.title, item.title)
        holder.setText(R.id.operUrl, item.operUrl)
        holder.setText(R.id.operLocation, item.operLocation)
        holder.setText(R.id.operTime, item.operTime)
    }
}