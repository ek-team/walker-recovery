package com.pharos.walker.customview.rxdialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.pharos.walker.R;

public class RxDialogSettingPassword extends RxDialog{
    private TextView mTvCancel;
    private TextView mTvSure;
    private EditText etInitPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private String initPassword;
    private String newPassword;
    private String confirmPassword;

    public String getInitPassword() {
        if (TextUtils.isEmpty(etInitPassword.getText().toString()))
            return "";
        return etInitPassword.getText().toString().trim();
    }

    public String getNewPassword() {
        if (TextUtils.isEmpty(etNewPassword.getText().toString()))
            return "";
        return etNewPassword.getText().toString().trim();
    }

    public String getConfirmPassword() {
        if (TextUtils.isEmpty(etConfirmPassword.getText().toString()))
            return "";
        return etConfirmPassword.getText().toString().trim();
    }

    public RxDialogSettingPassword(Context context, int themeResId) {
        super(context, themeResId);
    }

    public RxDialogSettingPassword(Context context) {
        super(context);
        initView();
    }

    public RxDialogSettingPassword(Context context, float alpha, int gravity) {
        super(context, alpha, gravity);
    }
    public void setSureListener(View.OnClickListener listener) {
        mTvSure.setOnClickListener(listener);
    }
    public void setCancelListener(View.OnClickListener listener) {
        mTvCancel.setOnClickListener(listener);
    }
    private void initView(){
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_setting_password, null);
        mTvSure = dialogView.findViewById(R.id.tv_sure);
        mTvCancel = dialogView.findViewById(R.id.tv_cancel);
        etInitPassword = dialogView.findViewById(R.id.et_init_password);
        etNewPassword = dialogView.findViewById(R.id.et_new_password);
        etConfirmPassword = dialogView.findViewById(R.id.et_confirm_password);
        mTvCancel.setOnClickListener(v -> dismiss());
        setContentView(dialogView);
    }
}
