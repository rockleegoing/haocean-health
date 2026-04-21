package com.ruoyi.app.adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ruoyi.app.model.entity.BannerEntity;
import com.stx.xhb.androidx.holder.HolderCreator;
import com.stx.xhb.androidx.holder.ViewHolder;

public class BannerHolderCreator implements HolderCreator<ViewHolder> {

    @Override
    public ViewHolder createViewHolder(int viewType) {
        return new ViewHolder<BannerEntity>(){
            @Override
            public int getLayoutId() {
                return com.stx.xhb.androidx.R.layout.xbanner_item_image;
            }

            @Override
            public void onBind(View itemView, BannerEntity data, int position) {
                Glide.with(itemView.getContext())
                        .load(data.getXBannerUrl())
                        .into((ImageView) itemView);
            }
        };
    }

    @Override
    public int getViewType(int position) {
        return position;
    }
}