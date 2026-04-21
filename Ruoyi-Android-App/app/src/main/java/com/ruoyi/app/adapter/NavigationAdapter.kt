package com.ruoyi.app.adapter;

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.hjq.shape.view.ShapeTextView
import com.ruoyi.app.R
import com.ruoyi.app.model.Constant
import com.ruoyi.app.model.entity.ButtomItemEntity
import com.ruoyi.code.Frame
import com.ruoyi.code.utils.clickDelay

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2021/02/28
 *    desc   : 导航栏适配器
 */
class NavigationAdapter(val list: MutableList<ButtomItemEntity>) :
    BaseQuickAdapter<ButtomItemEntity, BaseViewHolder>(
        R.layout.main_navigation_item, list
    ) {
    /** 当前选中条目位置 */
    private var selectedPosition: Int = 0

    /** 导航栏点击监听 */
    private var listener: OnNavigationListener? = null

    fun getSelectedPosition(): Int {
        return selectedPosition
    }

    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }

    /**
     * 设置导航栏监听
     */
    fun setOnNavigationListener(listener: OnNavigationListener?) {
        this.listener = listener
    }

    interface OnNavigationListener {
        fun onNavigationItemSelected(position: Int): Boolean
    }

    override fun convert(holder: BaseViewHolder, item: ButtomItemEntity) {

        val iconView: AppCompatImageView = holder.getView(R.id.iv_home_navigation_icon)
        val titleView: ShapeTextView = holder.getView(R.id.tv_home_navigation_title)

        val position = list.indexOf(item)
        iconView.isSelected = (selectedPosition == position)
        titleView.isSelected = (selectedPosition == position)

        val selectColor: Int = Color.parseColor(item.selectColor)
        val defaultColor: Int = Color.parseColor(item.defaultColor)

        titleView.textColorBuilder
            .setTextColor(defaultColor)
            .setTextSelectedColor(selectColor)
            .intoTextColor()

        // 主题2 仿照 支付宝 首页图片放大
        if (TextUtils.equals(Constant.mainStyle, Constant.mainStyleAlipay)) {
            if (position == 0 && selectedPosition == 0){
                titleView.visibility = View.GONE
            } else {
                titleView.visibility = View.VISIBLE
            }
        }

        val drawable = item.drawable
        if (drawable == null) {
            Glide.with(Frame.getContext())
                .load(item.selectIcon)
                .into(iconView)
        } else {
            iconView.setImageDrawable(drawable)
        }

        titleView.text = item.name

        holder.itemView.clickDelay {
            if (selectedPosition == position) {
                return@clickDelay
            }
            if (listener?.onNavigationItemSelected(position) == true) {
                selectedPosition = position
                notifyDataSetChanged()
            }
        }

    }

}