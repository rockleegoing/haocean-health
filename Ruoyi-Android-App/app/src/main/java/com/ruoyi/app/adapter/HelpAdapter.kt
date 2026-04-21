package com.ruoyi.app.adapter

import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ruoyi.app.R
import com.ruoyi.app.model.entity.QuestionEntity

/**
 * 帮助适配器
 */
class HelpAdapter(data: MutableList<QuestionEntity>) :
    BaseSectionQuickAdapter<QuestionEntity, BaseViewHolder>(
        R.layout.item_help,
        R.layout.item_help_child,
        data
    ) {

    override fun convertHeader(helper: BaseViewHolder, item: QuestionEntity) {
        helper.setImageResource(R.id.ivIcon, item.imageResource)
        helper.setText(R.id.tv_top_name, item.headerTitle)
    }

    override fun convert(holder: BaseViewHolder, item: QuestionEntity) {
        if (item.beginList) {
            holder.itemView.setBackgroundResource(R.drawable.shape_bg_mine_top)
        } else if (item.endList) {
            holder.itemView.setBackgroundResource(R.drawable.shape_bg_mine_buttom)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.shape_bg_mine_defalut)
        }
        holder.setText(R.id.tv_title, item.question)
    }

}