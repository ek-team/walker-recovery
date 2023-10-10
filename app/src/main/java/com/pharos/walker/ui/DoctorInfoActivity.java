package com.pharos.walker.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.tablayout.SegmentTabLayout;
import com.google.gson.Gson;
import com.pharos.walker.MainActivity;
import com.pharos.walker.R;
import com.pharos.walker.beans.ActivationCodeBean;
import com.pharos.walker.beans.Battery;
import com.pharos.walker.beans.TokenBean;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.constants.Api;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharos.walker.database.ActivationCodeManager;
import com.pharos.walker.database.UserManager;
import com.pharos.walker.utils.NetworkUtils;
import com.pharos.walker.utils.OkHttpUtils;
import com.pharos.walker.utils.QrUtil;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

/**
 * Created by zhanglun on 2021/6/3
 * Describe:
 */
public class DoctorInfoActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tab)
    SegmentTabLayout tab;
    @BindView(R.id.img_wx)
    ImageView imgWx;
    @BindView(R.id.ll_wx)
    LinearLayout llWx;
    @BindView(R.id.ll_doctor_info)
    LinearLayout llDoctorInfo;
    @BindView(R.id.tv_doctor_name)
    TextView tvDoctorName;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.tv_level)
    TextView tvLevel;
    @BindView(R.id.tv_work_years)
    TextView tvWorkYears;
    @BindView(R.id.tv_hospital_name)
    TextView tvHospitalName;
    @BindView(R.id.tv_hospital_tel)
    TextView tvHospitalTel;
    private UserBean userBean;

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        initView();
//        if (NetworkUtils.isConnected()){
//            syncUser();
//        }else {
//            ToastUtils.showShort("网络不可用");
//        }
        UserBean userBean = UserManager.getInstance().loadByUserId(SPHelper.getUserId());
        if (!SPHelper.getUser().getCaseHistoryNo().equals("123456") && userBean != null && userBean.getIsUpload() != Global.UploadNetStatus ){
            uploadUserDialog();
        }else {
            generateQrCode();
        }
    }

    private void uploadUserDialog(){
        RxDialogSureCancel dialog = new RxDialogSureCancel(this);
        dialog.setContent("用户信息未上传到云端，是否上传");
        dialog.setCancel("取消");
        dialog.setSure("开始上传");
        dialog.setSureListener(v -> {
            dialog.dismiss();
            if (NetworkUtils.isConnected()) {
                showWaiting("提示", "正在上传...");
                syncUser();
            } else {
                ToastUtils.showShort("网络不可用");
                uploadUserDialog();
            }
        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
            startTargetActivity(MainActivity.class, true);
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }
    private void initData() {
        userBean = SPHelper.getUser();
    }

    private void initView() {
        if (!Global.USER_MODE){
            llWx.setVisibility(View.GONE);
            llDoctorInfo.setVisibility(View.GONE);
            return;

        }
//        if (!TextUtils.isEmpty(userBean.getDoctor())){
//            tvDoctorName.setText(userBean.getDoctor());
//            tvHospitalName.setText(userBean.getHospitalName());
//            llDoctorInfo.setVisibility(View.VISIBLE);
//            llWx.setVisibility(View.VISIBLE);
//        }else {
//            llWx.setVisibility(View.VISIBLE);
//            llDoctorInfo.setVisibility(View.GONE);
////            imgWx.setImageResource(R.mipmap.ic_wx_app);
//        }
        llWx.setVisibility(View.VISIBLE);
        llDoctorInfo.setVisibility(View.GONE);

    }
    private void generateQrCode(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float density = metrics.density;
        int qrWidth = (int) (300*density);
//        Log.e("doctorInfo", "generateQrCode: " + density );
//        Log.e("doctorInfo", "generateQrCode: " + metrics.densityDpi);
        ActivationCodeBean codeBean = ActivationCodeManager.getInstance().getCodeBean();
        Resources res = getResources();
        Bitmap logoBitmap= BitmapFactory.decodeResource(res,R.mipmap.ic_launcher);
//        String content = Api.qrUrl + SPHelper.getUserId();
        String content = Api.qrUrl + SPHelper.getUserId() + "/" + codeBean.getMacAddress();

        Bitmap qrBitmap = QrUtil.createQRCodeBitmap(content, qrWidth, qrWidth,"UTF-8","H", "1", Color.BLACK, Color.WHITE,logoBitmap,0.15F);
        imgWx.setImageBitmap(qrBitmap);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getAction()) {
            case MessageEvent.ACTION_GATT_CONNECTED:
                setPoint(true);
                break;
            case MessageEvent.ACTION_GATT_DISCONNECTED:
                setPoint(false);
                break;
            case MessageEvent.BATTERY_REFRESH:
                Battery battery = (Battery) event.getData();
                setBattery(battery.getBatteryVolume(),battery.getBatteryStatus());
                break;
            default:
                break;
        }
    }
    private void syncUser() {
        List<UserBean> userBeans = UserManager.getInstance().loadNoNetUploadUser();
        String data = new Gson().toJson(userBeans);
        OkHttpUtils.postJsonAsync(Api.uploadUser, data, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                ToastUtils.showShort("服务器访问错误");
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Setting Activity", "--->用户同步结果" + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0) {
                    UserManager.getInstance().updateUserUploadStatus(userBeans, Global.UploadNetStatus);
                    generateQrCode();
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                } else if (code == 401) {
                    getToken();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        ToastUtils.showShort("数据访问错误 code=" + code);
                    }
                }
            }
        });

    }

    private void getToken() {
        OkHttpUtils.getAsyncToken(Api.tokenUrl + "?grant_type=client_credentials", true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                ToastUtils.showShort("数据访问错误");

            }
            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("MainActivity", "requestSuccess: " + result);
                TokenBean tokenBean = new Gson().fromJson(result, TokenBean.class);
                if (tokenBean.getCode() == 0) {
                    SPHelper.saveToken(tokenBean.getData().getAccess_token());
                    syncUser();
                }else {
                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    ToastUtils.showShort("数据访问错误");
                }
            }
        });

    }
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_doctor_info;
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
