package com.pharos.walker.customview.rxdialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.speech.RecognitionListener;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pharos.walker.R;

public class RxImageDialog extends RxDialog {
    private TextView mTvTitle;
    private TextView mTvContent;
    private TextView mTvCancel;
    private TextView mTvSure;
    private TextView tvMsg;
    private View mViewLine;
    private ImageView img;
    private RadioButton rbSelect;
    private RelativeLayout rlRefreshQrCode;
    private ImageView imgRefreshQrCode;
    public boolean isSelect = true;
    public RxImageDialog(Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    public RxImageDialog(Context context) {
        super(context);
        initView();
    }

    public RxImageDialog(Context context, float alpha, int gravity) {
        super(context, alpha, gravity);
        initView();
    }
    public TextView getSureView() {
        return mTvSure;
    }

    public void setSureListener(View.OnClickListener listener) {
        mTvSure.setOnClickListener(listener);
    }
    public void setRefreshListener(View.OnClickListener listener) {
        imgRefreshQrCode.setOnClickListener(listener);
    }

    public TextView getCancelView() {
        return mTvCancel;
    }

    public void setCancelListener(View.OnClickListener listener) {
        mTvCancel.setOnClickListener(listener);
    }
    public void setRefreshVisiable(int visible){
        rlRefreshQrCode.setVisibility(visible);
    }
    public void setRadiobuttonListener(View.OnClickListener listener) {
        rbSelect.setOnClickListener(listener);
    }
    private void setRadiobuttonSelect() {
        if (isSelect){
            rbSelect.setChecked(false);
            isSelect = false;
        }else {
            rbSelect.setChecked(true);
            isSelect = true;
        }
    }
    public boolean isChecked(){
        return isSelect;
    }

    public TextView getContentView() {
        return mTvContent;
    }

    public void setSure(String content) {
        mTvSure.setText(content);
    }
    public void setImage(Bitmap bitmap){
        img.setImageBitmap(bitmap);
    }

    public void setCancel(String content) {
        if (!TextUtils.isEmpty(content)){
            mTvCancel.setText(content);
        }else {
            mTvCancel.setVisibility(View.GONE);
            mViewLine.setVisibility(View.GONE);
        }
    }

    public void setContent(String str) {
        mTvContent.setText(str);
    }

    private void initView() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_image, null);
        mTvSure = dialogView.findViewById(R.id.tv_sure);
        mTvCancel = dialogView.findViewById(R.id.tv_cancel);
        mTvContent = dialogView.findViewById(R.id.tv_content);
        tvMsg = dialogView.findViewById(R.id.tv_msg);
        mTvTitle = dialogView.findViewById(R.id.tv_title);
        mViewLine = dialogView.findViewById(R.id.view_line);
        img = dialogView.findViewById(R.id.img);
        rbSelect = dialogView.findViewById(R.id.rb_select);
        rlRefreshQrCode = dialogView.findViewById(R.id.rl_refresh_qr_code);
        imgRefreshQrCode = dialogView.findViewById(R.id.img_refresh_qr_code);
        mTvCancel.setOnClickListener(v -> dismiss());
        rbSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRadiobuttonSelect();
            }
        });
        mTvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvContent.setTextIsSelectable(true);
        tvMsg.setMovementMethod(ScrollingMovementMethod.getInstance());
        setContentView(dialogView);
    }
}
