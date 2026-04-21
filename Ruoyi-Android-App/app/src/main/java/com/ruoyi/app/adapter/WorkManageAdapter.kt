package com.ruoyi.app.adapter

import android.text.TextUtils
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.hjq.toast.ToastUtils
import com.ruoyi.app.R
import com.ruoyi.app.model.entity.BannerEntity
import com.ruoyi.app.model.entity.BasicEntity
import com.ruoyi.code.Frame
import com.therouter.TheRouter

/**
 *  工作台适配器
 */
class WorkManageAdapter : BaseQuickAdapter<BasicEntity, BaseViewHolder>(R.layout.item_work_index) {

    override fun convert(holder: BaseViewHolder, item: BasicEntity) {
        holder.setText(R.id.tv_title, item.title)
        val adapter = WorkItemAdapter()
        val list: MutableList<BannerEntity> = ArrayList(item.data)
        adapter.setNewInstance(list)
        val recyclerview = holder.getView<RecyclerView>(R.id.recyclerview)
        recyclerview.adapter = adapter
        adapter.setOnItemClickListener { _, _, positon ->
            val item = adapter.getItem(positon)
            val page = item.page
            if (!TextUtils.isEmpty(page)) {
                TheRouter.build(page).navigation()
            } else {
                ToastUtils.show(Frame.getString(R.string.mine_construction))
            }
        }
    }

}