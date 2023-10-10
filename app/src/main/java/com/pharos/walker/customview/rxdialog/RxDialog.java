package com.pharos.walker.customview.rxdialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.pharos.walker.R;


/**
 * @author vondear
 */
public class RxDialog extends Dialog {

    protected Context mContext;

    protected LayoutParams mLayoutParams;

    public LayoutParams getLayoutParams() {
        return mLayoutParams;
    }

    public RxDialog(Context context, int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    public RxDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView(context);
    }

    public RxDialog(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = context;
        Window window = this.getWindow();
        mLayoutParams = window.getAttributes();
        mLayoutParams.alpha = 1f;
        mLayoutParams.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        window.setAttributes(mLayoutParams);
        this.getWindow().setBackgroundDrawableResource(R.mipmap.transparent_bg);
        if (mLayoutParams != null) {
            mLayoutParams.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
            mLayoutParams.gravity = Gravity.CENTER;
        }
    }

    /**
     * @param context
     * @param alpha   透明度 0.0f--1f(不透明)
     * @param gravity 方向(Gravity.BOTTOM,Gravity.TOP,Gravity.LEFT,Gravity.RIGHT)
     */
    public RxDialog(Context context, float alpha, int gravity) {
        super(context);
        // TODO Auto-generated constructor stub
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawableResource(R.mipmap.transparent_bg);
        mContext = context;
        Window window = this.getWindow();
        mLayoutParams = window.getAttributes();
        mLayoutParams.alpha = 1f;
        window.setAttributes(mLayoutParams);
        if (mLayoutParams != null) {
            mLayoutParams.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
            mLayoutParams.gravity = gravity;
        }
    }

    /**
     * 隐藏头部导航栏状态栏
     */
    public void skipTools() {
        if (Build.VERSION.SDK_INT < 19) {
            return;
        }
        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 设置全屏显示
     */
    public void setFullScreen() {
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        LayoutParams lp = window.getAttributes();
        lp.width = LayoutParams.FILL_PARENT;
        lp.height = LayoutParams.FILL_PARENT;
        window.setAttributes(lp);
    }

    /**
     * 设置宽度match_parent
     */
    public void setFullScreenWidth() {
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        LayoutParams lp = window.getAttributes();
        lp.width = LayoutParams.FILL_PARENT;
        lp.height = LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    /**
     * 设置高度为match_parent
     */
    public void setFullScreenHeight() {
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        LayoutParams lp = window.getAttributes();
        lp.width = LayoutParams.WRAP_CONTENT;
        lp.height = LayoutParams.FILL_PARENT;
        window.setAttributes(lp);
    }

    public void setOnWhole() {
        getWindow().setType(LayoutParams.TYPE_SYSTEM_ALERT);
    }
    private void fullScreenImmersive(View view) {
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        view.setSystemUiVisibility(uiOptions);
    }
    @Override
    public void show() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        super.show();
        fullScreenImmersive(getWindow().getDecorView());
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }
}
