package com.ruoyi.app.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ruoyi.app.R
import com.ruoyi.app.model.entity.BannerEntity

/**
 *  工作台适配器
 */
class WorkItemAdapter : BaseQuickAdapter<BannerEntity, BaseViewHolder>(R.layout.item_index) {

    override fun convert(holder: BaseViewHolder, item: BannerEntity) {
        holder.setText(R.id.tv_name, item.title)
        val icon = holder.getView<ImageView>(R.id.iv_icon)
        Glide.with(context).load(item.source).into(icon)
    }

}