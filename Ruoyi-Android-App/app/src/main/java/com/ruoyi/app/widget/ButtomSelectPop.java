package com.ruoyi.app.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import com.ruoyi.app.R;

/*
    作者：乐观开发者
    链接：https://juejin.cn/post/6844903504994107406
    来源：稀土掘金
    著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 */
public class ButtomSelectPop extends PopupWindow implements View.OnClickListener {

    @SuppressLint("StaticFieldLeak")
    public static View mainView;
    protected final Builder builder;

    public ButtomSelectPop(Builder builder) {
        //窗口布局
        super(builder.context);
        this.builder = builder;
        Create();
    }

    public void Create() {
        //窗口布局
        setContentView(mainView = LayoutInflater.from(builder.context).inflate(R.layout.buttom_pop_view, null));
        //设置宽度
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        //设置高度
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setTouchable(true);
        setFocusable(true);

        Window window = ((Activity) builder.context).getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.alpha = 0.6f;
        window.setAttributes(params);
        //设置显示隐藏动画
//        setAnimationStyle(R.style.AnimTools);
        //设置背景透明
        setBackgroundDrawable(new ColorDrawable());
        /* 监听窗口的焦点事件，点击窗口外面则取消显示*/
        getContentView().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                dismissPop();
            }
        });
        setOnDismissListener(() -> dismissPop());
        initView(mainView);
    }

    @Override
    public void onClick(View view) {
        dismissPop();
        if (view.getId() == R.id.tv_avatar_photograph) {
            if (builder.selectViewOnClick != null) {
                builder.selectViewOnClick.selectSelectView(0);
            }
        } else if (view.getId() == R.id.tv_avatar_photo) {
            if (builder.selectViewOnClick != null) {
                builder.selectViewOnClick.selectSelectView(1);
            }
        }
    }

    private void initView(View rootView) {
        TextView photograph = rootView.findViewById(R.id.tv_avatar_photograph);
        photograph.setOnClickListener(this);
        TextView photo = rootView.findViewById(R.id.tv_avatar_photo);
        photo.setOnClickListener(this);
        TextView tv_avatar_cancel = rootView.findViewById(R.id.tv_avatar_cancel);
        tv_avatar_cancel.setOnClickListener(v -> dismissPop());
    }

    public static class Builder {
        protected final Context context;
        protected View view;
        SelectViewOnClick selectViewOnClick;

        public Builder setPopTopOnClick(SelectViewOnClick selectViewOnClick) {
            this.selectViewOnClick = selectViewOnClick;
            return this;
        }

        public Builder setView(View view) {
            this.view = view;
            return this;
        }

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        @UiThread
        public PopupWindow build() {
            return new ButtomSelectPop(this);
        }

        @UiThread
        public PopupWindow show() {
            PopupWindow popTop = build();
            popTop.showAtLocation(view, Gravity.BOTTOM, 0, 0);
            return popTop;
        }
    }

    public interface SelectViewOnClick {
        void selectSelectView(int position);
    }

    public void dismissPop() {
        Window window = ((Activity) builder.context).getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.alpha = 1f;
        window.setAttributes(params);
        dismiss();
    }

}

