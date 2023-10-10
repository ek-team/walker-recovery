package com.pharos.walker.customview.rxdialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.pharos.walker.R;

public class RxDialogLogin extends RxDialog {
    private TextView mTvSure;
    private EditText etPassword;
    private String password;

    public String getPassword() {
        if (TextUtils.isEmpty(etPassword.getText().toString())){
            return "";
        }
        return etPassword.getText().toString();
    }

    public RxDialogLogin(Context context, int themeResId) {
        super(context, themeResId);
    }

    public RxDialogLogin(Context context) {
        super(context);
        initView();
    }
    public void setSureListener(View.OnClickListener listener) {
        mTvSure.setOnClickListener(listener);
    }
    private void initView(){
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_login, null);
        mTvSure = dialogView.findViewById(R.id.tv_sure);
        etPassword = dialogView.findViewById(R.id.et_password);
        setContentView(dialogView);
    }
}
