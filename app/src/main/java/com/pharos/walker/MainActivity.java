package com.pharos.walker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.pharos.walker.application.MyApplication;
import com.pharos.walker.beans.ActivationCodeBean;
import com.pharos.walker.beans.Battery;
import com.pharos.walker.beans.OriginalPlanEntity;
import com.pharos.walker.beans.PlanEntity;
import com.pharos.walker.beans.ProductInfoBean;
import com.pharos.walker.beans.ProductStockInfoBean;
import com.pharos.walker.beans.ServerActivationCodeBean;
import com.pharos.walker.beans.ServerPlanEntity;
import com.pharos.walker.beans.TokenBean;
import com.pharos.walker.beans.VersionInfoBean;
import com.pharos.walker.constants.Api;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.customview.rxdialog.RxDialogLogin;
import com.pharos.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharos.walker.database.ActivationCodeManager;
import com.pharos.walker.database.OriginalPlanManager;
import com.pharos.walker.database.OriginalSubPlanManager;
import com.pharos.walker.database.SubPlanManager;
import com.pharos.walker.database.TrainPlanManager;
import com.pharos.walker.error.CrashHandler;
import com.pharos.walker.services.ForegroundWorkService;
import com.pharos.walker.ui.ActivationCodeActivity;
import com.pharos.walker.ui.BaseActivity;
import com.pharos.walker.ui.CommitResultActivity;
import com.pharos.walker.ui.ConnectDeviceActivity;
import com.pharos.walker.ui.DoctorInfoActivity;
import com.pharos.walker.ui.NewsActivity;
import com.pharos.walker.ui.PlanActivity;
import com.pharos.walker.ui.SettingActivity;
import com.pharos.walker.ui.UserActivity;
import com.pharos.walker.ui.UserInfoActivity;
import com.pharos.walker.ui.VideoPlayerActivity;
import com.pharos.walker.utils.AppUtils;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.DesUtil;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.NetworkUtils;
import com.pharos.walker.utils.OkHttpUtils;
import com.pharos.walker.utils.PackageManagerCompatP;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.ShellUtils;
import com.pharos.walker.utils.SimInfoUtils;
import com.pharos.walker.utils.ToastUtils;
import com.tencent.mars.xlog.Log;
import com.tencent.mars.xlog.Xlog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

/**
 * Created by zhanglun on 2021/4/13
 * Describe:
 */
public class MainActivity extends BaseActivity implements View.OnTouchListener {
    @BindView(R.id.iv_power)
    ImageView ivPower;
    @BindView(R.id.tv_battery)
    TextView tvBattery;
    @BindView(R.id.iv_notification)
    ImageView ivNotification;
    @BindView(R.id.iv_red)
    ImageView ivRed;
    @BindView(R.id.iv_voice)
    ImageView ivVoice;
    @BindView(R.id.iv_point)
    ImageView ivPoint;
    @BindView(R.id.iv_bt)
    ImageView ivBt;
    @BindView(R.id.layout_bluetooth)
    LinearLayout layoutBluetooth;
    @BindView(R.id.iv_header)
    ImageView ivHeader;
    @BindView(R.id.layout_header)
    RelativeLayout layoutHeader;
    @BindView(R.id.tv_header)
    TextView tvHeader;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_am_pm)
    TextView tvAmPm;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.iv_video)
    ImageView ivVideo;
    @BindView(R.id.tv_help)
    TextView tvHelp;
    @BindView(R.id.layout_help)
    RelativeLayout layoutHelp;
    @BindView(R.id.iv_training)
    ImageView ivTraining;
    @BindView(R.id.iv_plan)
    TextView ivPlan;
    @BindView(R.id.iv_doctor)
    TextView ivDoctor;
    @BindView(R.id.iv_news)
    TextView ivNews;
    @BindView(R.id.iv_user_center)
    TextView ivUserCenter;
    @BindView(R.id.iv_setting)
    TextView ivSetting;
    @BindView(R.id.iv_training_1)
    ImageView ivTraining1;
    @BindView(R.id.tv_more_operation)
    TextView tvMoreOperation;
    @BindView(R.id.rl_sample)
    RelativeLayout rlSample;
    @BindView(R.id.rl_complete)
    RelativeLayout rlComplete;
    @BindView(R.id.re_root)
    RelativeLayout reRoot;
    @BindView(R.id.tv_ble_battery)
    TextView tvBleBattery;
    @BindView(R.id.tv_time_1)
    TextView tvTime1;
    @BindView(R.id.tv_am_pm_1)
    TextView tvAmPm1;
    @BindView(R.id.tv_date_1)
    TextView tvDate1;
    @BindView(R.id.iv_video_1)
    ImageView ivVideo1;
    @BindView(R.id.tv_help_1)
    TextView tvHelp1;
    @BindView(R.id.layout_help_1)
    RelativeLayout layoutHelp1;
    private boolean touchFlag;
    private int TOKEN_REQ = 0;
    private int TIMESTAMP_REQ = 1;
    private int ACTIVATION_CODE_REQ = 2;
    private int PLAN_REQ = 3;
    private int UPLOAD_VERSION_INFO_REQ = 4;
    private static final int REQUEST_CODE_WRITE_SETTINGS = 2;
    private static final int REQUEST_CODE_MANAGER_FILES = 3;
    private int CHECK_VERSION__REQ = 5;
    private int REQUEST_QRCODE_LINK = 6;
    private int QUERY_DEVICE = 7;
    private int queryVersion = Global.ChangSha;
    private AlphaAnimation alphaAniShow, alphaAniHide;
    private TranslateAnimation translateAniShow, translateAniHide;
    private String RED_LED_DEV = "/sys/class/leds/red/brightness";
    private String GREEN_LED_DEV = "/sys/class/leds/green/brightness";
    private String BLUE_LED_DEV = "/sys/class/leds/blue/brightness";
    private  ActivationCodeBean localCodeBean;
    private String deviceIp;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0){
                startTargetActivity(UserActivity.class,false);
            }
        }
    };
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
//        disableButton();
//        initData();
//        initActivationCode();
        localCodeBean = ActivationCodeManager.getInstance().getCodeBean();
        initView();
        if (TextUtils.isEmpty(SPHelper.getToken()) && NetworkUtils.isConnected()) {
            getToken(TOKEN_REQ);
        }
        if (NetworkUtils.isConnected()) {
            new Thread(() ->{
                deviceIp =  NetworkUtils.GetNetIp();
                android.util.Log.e("MainActivity", "initialize: " + deviceIp);
                uploadVersionInfo();
            } ).start();
            getQrCodeLink();
//            checkVersion();
            deviceStatusQuery();
        }
//        // 红灯关
//        setColor("0", RED_LED_DEV);
//        // 蓝灯关
//        setColor("0", BLUE_LED_DEV);
//        // 绿灯开
//        setColor("255", GREEN_LED_DEV);
        // Android 动态请求权限 Android 10 需要单独处理
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE};
                ActivityCompat.requestPermissions(this, strings, 1);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        "android.permission.ACCESS_BACKGROUND_LOCATION",
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(this, strings, 2);
            }

        }

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE_MANAGER_FILES);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
            }
        }
        if(SPHelper.getHomeSetting() == Global.HomeUser){
            mHandler.sendEmptyMessageDelayed(0,1000);
        }
//        WakeUtil.getInstance(this).startWake();
//        OneShotUtil.getInstance(this).startOneShot();
    }

    protected void setColor(String value, String path) {
        java.lang.Process process = null;
        DataOutputStream dos = null;
        try {
            process = Runtime.getRuntime().exec("su");
//            process = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(process.getOutputStream());
//            dos.writeBytes("echo " + value + " >" + path + "\n");
            dos.writeBytes("reboot -p" + "\n");
            dos.flush();
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void getToken(int status) {
        OkHttpUtils.getAsyncToken(Api.tokenUrl + "?grant_type=client_credentials", true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                ToastUtils.showShort(e.getMessage());

            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("MainActivity", "requestSuccess: " + result);

                TokenBean tokenBean = new Gson().fromJson(result, TokenBean.class);
                if (tokenBean.getCode() == 0) {
                    SPHelper.saveToken(tokenBean.getData().getAccess_token());
                    if (status == TIMESTAMP_REQ) {
                        getServerTimestamp();
                    } else if (status == ACTIVATION_CODE_REQ) {
                        getActivationCode(localCodeBean.getMacAddress());
                    } else if (status == PLAN_REQ) {
                        syncPlan();
                    }else if (status == UPLOAD_VERSION_INFO_REQ){
                        uploadVersionInfo();
                    }else if (status == CHECK_VERSION__REQ){
                        checkVersion();
                    }else if (status == REQUEST_QRCODE_LINK){
                        getQrCodeLink();
                    }else if (status == QUERY_DEVICE){
                        deviceStatusQuery();
                    }
                } else {
                    ToastUtils.showShort("Token 获取失败");
                }

            }
        });

    }
    private void deviceStatusQuery(){
        OkHttpUtils.getAsync(Api.getCloudDeviceStatus + "?macAddress=" + localCodeBean.getMacAddress() , true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                android.util.Log.e("foreground", "requestSuccess: " + e);
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                android.util.Log.e("foreground", "requestSuccess: " + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if(code == 0){
                    ProductStockInfoBean productStockInfoBean = new Gson().fromJson(result,ProductStockInfoBean.class);
                    Global.isUploadAllData = productStockInfoBean.getData().getTag() != 0;
                    Global.downloadSourceSerial =productStockInfoBean.getData().getSourceProductSn();
                    if(!TextUtils.isEmpty(Global.downloadSourceSerial)){
                        Global.isDownloadAllData = true;
                        showWaiting("提示", "开始下载数据...");
                    }
                }else if (code == 401){
                    getToken(QUERY_DEVICE);
                }


            }
        });
    }
    private void getActivationCode(String macAddress) {
        OkHttpUtils.getAsync(Api.getActivationCode + macAddress, true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                netErrorDialog(macAddress);
                enableButton();
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("MainActivity", "code request success: " + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                ActivationCodeBean localCodeBean = ActivationCodeManager.getInstance().getCodeBean();
                if (code == 0) {
                    try {
                        ServerActivationCodeBean codeBean = new Gson().fromJson(result, ServerActivationCodeBean.class);
                        localCodeBean.setActivationCode(codeBean.getData().getActivationCode());
                        localCodeBean.setRecordDate(System.currentTimeMillis());
                        ActivationCodeManager.getInstance().insertCodeBean(localCodeBean);
                        if (verifyActivationCode(localCodeBean.getPublicKey(), codeBean.getData().getActivationCode(), localCodeBean.getMacAddress())) {
                            enableButton();
                        } else {
                            activationCodeDialog("软件需要更新，请联系厂家维修", "退出系统", false);
                        }
                    } catch (Exception e) {
                        activationCodeDialog("软件需要更新，请联系厂家维修", "退出系统", false);
                    }
                } else if (code == 401) {
                    getToken(ACTIVATION_CODE_REQ);
                } else {
                    netErrorDialog(macAddress);
                    enableButton();
                }

            }
        });

    }

    private void getServerTimestamp() {
        OkHttpUtils.getAsync(Api.getServerTimestamp, true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {

            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("MainActivity", "code request success: " + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0) {
                    long timestamp = toJsonObj.getInt("data");
                    ActivationCodeBean codeBean = ActivationCodeManager.getInstance().getCodeBean();
                    if (timestamp / 1000 >= System.currentTimeMillis() / 1000) {
                        codeBean.setRecordDate(timestamp);
                        ActivationCodeManager.getInstance().insertCodeBean(codeBean);
                    } else {
                        codeBean.setRecordDate(timestamp);
                        ActivationCodeManager.getInstance().insertCodeBean(codeBean);
                    }

                } else if (code == 401) {
                    getToken(TIMESTAMP_REQ);
                }

            }
        });
    }

    private void initActivationCode() {
        ActivationCodeBean codeBean = ActivationCodeManager.getInstance().getCodeBean();
        if (NetworkUtils.isConnected()) {
            getServerTimestamp();
        } else {
            if (codeBean != null && codeBean.getRecordDate() > System.currentTimeMillis()) {
                RxDialogSureCancel rxDialog = new RxDialogSureCancel(this);
                rxDialog.setContent("请把设备联网或者手动同步时间到最新，重启设备");
                rxDialog.setCancel("");
                rxDialog.setSure("退出系统");
                rxDialog.setCancelable(false);
                rxDialog.setCanceledOnTouchOutside(false);
                rxDialog.setSureListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Process.killProcess(Process.myPid());
                        System.exit(1);
                    }
                });
                rxDialog.show();
                return;
            } else if (codeBean != null) {
                codeBean.setRecordDate(System.currentTimeMillis());
                ActivationCodeManager.getInstance().insertCodeBean(codeBean);
            }
        }
        if (!TextUtils.isEmpty(codeBean.getMacAddress())) {
            if (NetworkUtils.isConnected()) {
                getActivationCode(codeBean.getMacAddress());
            } else if (codeBean.getActivationCode() != null) {
                boolean isAuthPass = verifyActivationCode(codeBean.getPublicKey(), codeBean.getActivationCode(), codeBean.getMacAddress());
                if (isAuthPass) {
                    enableButton();
                } else {
                    activationCodeDialog("软件需要更新，请联系厂家维修", "退出系统", false);
                }
            } else {
                activationCodeDialog("网络未连接请设置网络，重启软件", "退出系统", false);
            }

        } else {
            activationCodeDialog("未获取到mac地址,请打开WiFi然后重启软件", "退出系统", false);
        }
//        if (codeBean.getActivationCode() != null) {
//            boolean isAuthPass = verifyActivationCode(codeBean.getPublicKey(), codeBean.getActivationCode(), codeBean.getMacAddress());
//            if (isAuthPass) {
//                enableButton();
//            } else {
//                activationCodeDialog("软件需要更新，请联系厂家维修", "退出系统", false);
//            }
//        } else if (!TextUtils.isEmpty(codeBean.getMacAddress()) && TextUtils.isEmpty(codeBean.getActivationCode())) {
//            if (NetworkUtils.isConnected()) {
//                getActivationCode(codeBean.getMacAddress());
//            } else {
//                activationCodeDialog("网络未连接请设置网络，重启软件", "退出系统", false);
//            }
//        } else {
//            activationCodeDialog("未获取到mac地址,请打开WiFi然后重启软件", "退出系统", false);
//        }

    }

    private boolean verifyActivationCode(String publicKey, String activationCode, String mac) {
        String decryptCode = null;
        try {
            decryptCode = new DesUtil(publicKey).decrypt(activationCode);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        String[] strings;
        if (decryptCode != null) {
            strings = decryptCode.split("-");
        } else {
            return false;
        }
        String macAddress = strings[0];
        long endDate = DateFormatUtil.getSpecialString2Date(strings[1]);
        if (!mac.replaceAll(":", "").endsWith(macAddress)) {
            return false;
        }
        return System.currentTimeMillis() <= endDate;
    }

    private void activationCodeDialog(String content, String sureText, boolean isGoActivate) {
        RxDialogSureCancel rxDialog = new RxDialogSureCancel(this);
        rxDialog.setContent(content);
        rxDialog.setCancel("");
        rxDialog.setSure(sureText);
        rxDialog.setCancelable(false);
        rxDialog.setCanceledOnTouchOutside(false);
        rxDialog.setSureListener(v -> {
            if (isGoActivate) {
                startTargetActivity(ActivationCodeActivity.class, false);

            } else {
                Process.killProcess(Process.myPid());
                System.exit(1);
            }
            rxDialog.dismiss();
        });
        rxDialog.show();
    }

    private void netErrorDialog(String mac) {
        RxDialogSureCancel rxDialog = new RxDialogSureCancel(this);
        rxDialog.setContent("数据获取失败，请尝试重试");
        rxDialog.setCancel("");
        rxDialog.setSure("重试");
        rxDialog.setCancelable(false);
        rxDialog.setCanceledOnTouchOutside(false);
        rxDialog.setSureListener(v -> {
            showWaiting("获取数据", "正在请求……");
            getActivationCode(mac);
            rxDialog.dismiss();
        });
        rxDialog.show();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        if (Global.isChangSha){
            queryVersion = Global.ChangSha;
        }else {
            queryVersion = Global.AnHui;
        }
        ivTraining.setOnTouchListener(this);
        ivTraining1.setOnTouchListener(this);
        tvMoreOperation.setOnTouchListener(this);
        ivPlan.setOnTouchListener(this);
        ivNews.setOnTouchListener(this);
        ivDoctor.setOnTouchListener(this);
        ivUserCenter.setOnTouchListener(this);
        ivSetting.setOnTouchListener(this);
        alphaAnimation();
        translateAnimation();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getAction()) {
            case MessageEvent.ACTION_GATT_CONNECTED:
                setPoint(true);
                break;
            case MessageEvent.UPDATE_TOP_TIME:
                updateTime();
                break;
            case MessageEvent.ACTION_GATT_DISCONNECTED:
                setPoint(false);
                break;
            case MessageEvent.BATTERY_REFRESH:
                Battery battery = (Battery) event.getData();
                setBattery(battery.getBatteryVolume(), battery.getBatteryStatus());
                break;
            case MessageEvent.ACTION_READ_DEVICE:
                int bleBattery = (int) event.getData();
                setTvBleBattery(bleBattery);
                break;
            case MessageEvent.ACTION_REQ_FAIL:
                ToastUtils.showShort("同步计划请求失败");
                break;
            case MessageEvent.ACTION_SYNC_PLAN_RESULT:

                break;
            case MessageEvent.ACTION_DOWNLOAD_PROGRESS:
                if (waitDialog != null && waitDialog.isShowing()){
                    waitDialog.setProgress((int) event.getData());
                }
                break;
            case MessageEvent.DOWNLOAD_FINISH:
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                ToastUtils.showShort("下载成功");
                break;
            case MessageEvent.DOWNLOAD_FAIL:
                String msg = (String) event.getData();
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                ToastUtils.showShort(msg);
                break;
            default:
                break;
        }
    }

    @OnClick({R.id.iv_notification, R.id.iv_red, R.id.iv_voice, R.id.iv_video, R.id.tv_help, R.id.layout_help, R.id.iv_training, R.id.iv_plan, R.id.iv_doctor, R.id.iv_news,
            R.id.iv_user_center, R.id.iv_setting, R.id.tv_header, R.id.iv_training_1, R.id.tv_more_operation, R.id.img_more_function})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_notification:
                break;
            case R.id.iv_red:
                break;
            case R.id.iv_voice:
                break;
            case R.id.iv_training:
            case R.id.iv_training_1:
//                startTargetActivity(CommitResultActivity.class,false);
                if (isShouldEvaluate()){
                    goEvaluateDialog();
                } else if (Global.USER_MODE) {
                    selectModeDialog();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectEvaluateMode);
                    startTargetActivity(bundle, ConnectDeviceActivity.class, false);
                }
//                if (DateFormatUtil.avoidFastClick(1000)){
//                    Bundle bundle = new Bundle();
//                    bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE,Global.ConnectMainMode);
//                    startTargetActivity(bundle,ConnectDeviceActivity.class,false);
//                }
                break;
            case R.id.iv_doctor:
                if (DateFormatUtil.avoidFastClick(1000)) {
                    startTargetActivity(DoctorInfoActivity.class, false);
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("ConnectMode",1);
//                    startTargetActivity(bundle,ConnectDeviceActivity.class,false);
                }
                break;
            case R.id.iv_video:
            case R.id.tv_help:
            case R.id.layout_help:
            case R.id.iv_video_1:
            case R.id.tv_help_1:
            case R.id.layout_help_1:
//                String desDir = Environment.getExternalStorageDirectory() + File.separator + "app_release_sign.apk";
//                String desDir = getPath(this) + File.separator + "app_release_sign.apk";
//                String url = "file:///android_assets/app_release_sign.apk";
////                AppUtils.installAppSilent(desDir);
//                PackageManager pm = this.getPackageManager();
//                PackageManagerCompatP.install(this,url,pm);
//                ShellUtils.execCmd("pm install -r "+desDir,false);
//                shutdown();
//                reset();
//                int a = 10;//测试崩溃日志
//                int b = 0;
//                int c = a/b;
                Intent intent = new Intent(this, VideoPlayerActivity.class);
                String videoUrl;
                if (SPHelper.getDoctorVideoStatus()){
                    videoUrl = "asset:///video_user_help.mp4";
                }else {
                    videoUrl = "asset:///video_user_help_1.mp4";
                }
                intent.putExtra(AppKeyManager.EXTRA_VIDEO_FILE, videoUrl);
                startActivity(intent);
                break;
            case R.id.iv_plan:
                if (DateFormatUtil.avoidFastClick(1500)) {
                    startTargetActivity(PlanActivity.class, false);
                }
//                String desDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+ File.separator + "app_release_sign.apk";
//                PackageManager pm1 = this.getPackageManager();
//                PackageManagerCompatP.install(this,desDir,pm1);
                break;
            case R.id.iv_news:
//                shutdown();
                if (DateFormatUtil.avoidFastClick(1500)) {
                    startTargetActivity(NewsActivity.class, false);
                }
                break;
            case R.id.iv_user_center:
                startTargetActivity(UserInfoActivity.class, false);
                break;
            case R.id.iv_setting:
                startTargetActivity(SettingActivity.class, false);
                break;
            case R.id.tv_more_operation:
            case R.id.img_more_function:
                if (Global.ReleaseVersion == Global.HomeVersion && DateFormatUtil.avoidFastClick(1500)) {
                    startTargetActivity(PlanActivity.class, false);
                }
//                if (rlComplete.getVisibility() == View.GONE && DateFormatUtil.avoidFastClick(1500)){
//                    rlComplete.startAnimation(translateAniShow);
//                    rlComplete.setVisibility(View.VISIBLE);
////                rlSample.startAnimation(alphaAniHide);
//                    rlSample.setVisibility(View.GONE);
//                }

                break;
        }
    }

    public String getPath(Context context) {
        File dir = null;
        boolean state = Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED );
        if (state) {
            if (Build.VERSION.SDK_INT >= 29) {
                //Android10之后
                dir = context.getExternalFilesDir( null );
            } else {
                dir = Environment.getExternalStorageDirectory();
            }
        } else {
            dir = Environment.getRootDirectory();
        }
        return dir.toString();
    }
    private void reset() {//重启
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        pm.reboot(null);
    }
    private void shutdown() {//关机
        Intent intent = new Intent("com.android.shutdown");
        sendBroadcast(intent);
    }
    private boolean isShouldEvaluate(){
        if (Global.USER_MODE && MyUtil.isNoPlanUser() && TrainPlanManager.getInstance().isPlanEmpty(SPHelper.getUserId()) && ((System.currentTimeMillis() - SPHelper.getEvaluateDate(SPHelper.getUserId())) > Global.NoPlanUserEvaluateInterval*24*3600*1000)){
            return true;
        }else if (TrainPlanManager.getInstance().isPlanEmpty(SPHelper.getUserId()) && Global.USER_MODE && !MyUtil.isNoPlanUser() && (System.currentTimeMillis() > DateFormatUtil.getString2Date(SPHelper.getUser().getDate()))){
            return true;
        }else {
            return false;
        }
    }
    private void disableButton() {
        tvHelp.setClickable(false);
        ivVideo.setClickable(false);
        layoutHelp.setClickable(false);
        ivDoctor.setClickable(false);
        ivUserCenter.setClickable(false);
        ivSetting.setClickable(false);
        ivNews.setClickable(false);
        ivPlan.setClickable(false);
        ivTraining.setClickable(false);
        ivHeader.setClickable(false);
        tvHeader.setClickable(false);

    }

    private void enableButton() {
        tvHelp.setClickable(true);
        ivVideo.setClickable(true);
        layoutHelp.setClickable(true);
        ivDoctor.setClickable(true);
        ivUserCenter.setClickable(true);
        ivSetting.setClickable(true);
        ivNews.setClickable(true);
        ivPlan.setClickable(true);
        ivTraining.setClickable(true);
        ivHeader.setClickable(true);
        tvHeader.setClickable(true);
    }
    private void getQrCodeLink(){
        OkHttpUtils.getAsync(Api.getQrCodeLink  + "?macAdd=" +  localCodeBean.getMacAddress() ,true,new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                ToastUtils.showShort("数据请求失败");
            }
            @Override
            public void requestSuccess(String result) throws Exception {
                com.tencent.mars.xlog.Log.e("User Activity", "获取二维码链接返回结果: " + result);
                JSONObject toJsonObj= new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                if (code == 0){
                    String qrcodeLink = toJsonObj.getString("data");
                    localCodeBean.setQrcodeLink(qrcodeLink);
                    ActivationCodeManager.getInstance().insertCodeBean(localCodeBean);
                }else if (code == 401){
                    getToken(REQUEST_QRCODE_LINK);
                }
            }
        });
    }

    private void uploadVersionInfo() {
        ActivationCodeBean activationCodeBean = ActivationCodeManager.getInstance().getCodeBean();
        ProductInfoBean.DataBean dataBean = new ProductInfoBean.DataBean();
        dataBean.setIccId(new SimInfoUtils(this).getIccid());
        dataBean.setMacAddress(activationCodeBean.getMacAddress());
        dataBean.setProductDeviceType("下肢医用版");
        dataBean.setSystemVersion(Build.DISPLAY);
        dataBean.setVersionStr(AppUtils.getAppVersionCode()+"");
        dataBean.setProductSn(SPHelper.getSerialNumber());
        dataBean.setIpAdd(deviceIp);
//        StringBuilder sb = new StringBuilder();
//        sb.append("{");
//        sb.append("\"versionStr\"");
//        sb.append(":");
//        sb.append(AppUtils.getAppVersionCode());
//        sb.append(",");
//        sb.append("\"macAddress\"");
//        sb.append(":");
//        sb.append("\"");
//        sb.append(activationCodeBean.getMacAddress());
//        sb.append("\"");
//        sb.append(",");
//        sb.append("\"systemVersion\"");
//        sb.append(":");
//        sb.append("\"");
//        sb.append(Build.DISPLAY);
//        sb.append("\"");
//        sb.append(",");
//        sb.append("\"iccId\"");
//        sb.append(":");
//        sb.append("\"");
//        sb.append(new SimInfoUtils(this).getIccid());
//        sb.append("\"");
//        sb.append("}");
        OkHttpUtils.putJsonAsync(Api.uploadVersionInfo, new Gson().toJson(dataBean), new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }


            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Setting Activity", "--->上传版本信息返回结果" + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0){
                    ProductInfoBean productInfoBean = new Gson().fromJson(result, ProductInfoBean.class);
                    if (TextUtils.isEmpty(SPHelper.getSerialNumber())){
                        SPHelper.saveSerialNumber(productInfoBean.getData().getProductSn());
                    }
                } else if (code == 401) {
                    getToken(UPLOAD_VERSION_INFO_REQ);
                }

            }
        });
    }
    private void syncPlan() {
        OkHttpUtils.getAsync(Api.getPlan + SPHelper.getUserId(), true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Setting Activity", "--->计划获取结果" + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 401) {
                    getToken(PLAN_REQ);
                    return;
                }
                if (code == 0) {
                    ServerPlanEntity serverPlanEntity = new Gson().fromJson(result, ServerPlanEntity.class);
//                Type type = new TypeToken<List<PlanEntity>>(){}.getType();
//                List<PlanEntity> planEntityList = gson.fromJson(serverPlanEntity.getData(),type);
                    List<PlanEntity> planEntityList = serverPlanEntity.getData();
                    if (planEntityList.size() == 0 || TrainPlanManager.getInstance().comparePlanUpdateDate(DateFormatUtil.getString2Date(planEntityList.get(0).getUpdateDate()),
                            planEntityList.get(0).getUserId()) == Global.UploadLocalStatus) {
                        List<PlanEntity> localPlan = TrainPlanManager.getInstance().getMasterPlanListByUserId(SPHelper.getUserId());
                        OkHttpUtils.postJsonAsync(Api.uploadPlan, new Gson().toJson(localPlan), new OkHttpUtils.DataCallBack() {
                            @Override
                            public void requestFailure(Request request, IOException e) {
                                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
                            }

                            @Override
                            public void requestSuccess(String result) throws Exception {
                                Log.e("Setting Activity", "--->计划同步结果" + result);
                                JSONObject toJsonObj = new JSONObject(result);
                                int code = toJsonObj.getInt("code");
                                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_PLAN_RESULT, code));

                            }
                        });
                    } else if (TrainPlanManager.getInstance().comparePlanUpdateDate(DateFormatUtil.getString2Date(planEntityList.get(0).getUpdateDate()),
                            planEntityList.get(0).getUserId()) == Global.UploadStatus) {
                        if (planEntityList.size()>0){
                            TrainPlanManager.getInstance().clearTrainPlanDatabaseByUserId(planEntityList.get(0).getUserId());
                        }
                        List<OriginalPlanEntity> originalPlanEntityList = serverPlanEntity.getOriginalData();
                        for (OriginalPlanEntity planEntity : originalPlanEntityList){
                            OriginalPlanManager.getInstance().insert(planEntity);
                            if (planEntity.getSubPlanEntityList() != null && planEntity.getSubPlanEntityList().size() > 0){
                                OriginalSubPlanManager.getInstance().insertMany(planEntity.getSubPlanEntityList());
                            }
                        }
                        for (PlanEntity planEntity : planEntityList){
                            TrainPlanManager.getInstance().insert(planEntity);
                            if (planEntity.getSubPlanEntityList() != null && planEntity.getSubPlanEntityList().size() > 0){
                                SubPlanManager.getInstance().insertMany(planEntity.getSubPlanEntityList());
                            }
                        }
                    }
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_PLAN_RESULT, code));
                } else {
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
                }
            }
        });
    }
    private void checkVersion(){
        String versionUrl = Api.getNewVersion + "?type=" + queryVersion;
        if (!TextUtils.isEmpty(localCodeBean.getMacAddress())){
            versionUrl = Api.getNewVersion + "?type=" + queryVersion + "&macAddress=" + localCodeBean.getMacAddress();
        }
        OkHttpUtils.getAsync(versionUrl , true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Setting Activity", "获取最新版本号返回结果: " + result);
                JSONObject toJsonObj= new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0){
                    VersionInfoBean bean = new Gson().fromJson(result,VersionInfoBean.class);
                    if (!TextUtils.isEmpty(bean.getData().getVersion()) && Float.parseFloat(bean.getData().getVersion()) > AppUtils.getAppVersionCode()){
                        updateApk(bean.getData().getUrl());
//                        RxDialogSureCancel rxDialog = new RxDialogSureCancel(MainActivity.this);
//                        rxDialog.setContent("新版本可更新");
//                        rxDialog.setCancel("取消");
//                        rxDialog.setSure("更新");
//                        rxDialog.setCancelable(false);
//                        rxDialog.setCanceledOnTouchOutside(false);
//                        rxDialog.setSureListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                updateApk(bean.getData().getUrl());
//                                rxDialog.dismiss();
//                            }
//                        });
//                        rxDialog.show();
//                        rxDialog.setCancelListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                rxDialog.dismiss();
//                            }
//                        });
                    }else {
                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_CHECK_VERSION));
                    }
                }else if (code == 401){
                    getToken(CHECK_VERSION__REQ);
                }

            }
        });
    }
    private void updateApk(String url){
        String desDir = Environment.getExternalStorageDirectory()  + "";
//        AppUtils.installAppRootCmd(desDir);
        if (waitDialog == null){
            progressDisplay();
            OkHttpUtils.downloadAsync(url, desDir, false,new OkHttpUtils.DataCallBack() {
                @Override
                public void requestFailure(Request request, IOException e) {
                    e.printStackTrace();
                    waitDialog.dismiss();
                    waitDialog = null;
                    ToastUtils.showShort("下载失败");
                }

                @Override
                public void requestSuccess(String result) throws Exception {
                    waitDialog.dismiss();
                    waitDialog = null;
                    AppUtils.installAppRootCmd(desDir + url.substring(url.lastIndexOf("/")));
//                    AppUtils.installApp(desDir + url.substring(url.lastIndexOf("/")));
                }
            });
        }else if (!waitDialog.isShowing()){
            waitDialog.show();
        }

    }
    private ProgressDialog waitDialog;
    private void progressDisplay(){
        if (waitDialog == null){
            waitDialog = new ProgressDialog(this);
            waitDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            waitDialog.setCancelable(false);
            waitDialog.setCanceledOnTouchOutside(false);
            waitDialog.setMessage("下载中，请稍等。。。");

            waitDialog.setProgress(0);
        }
        waitDialog.show();

    }
    private void updateTime() {
        Calendar cal = Calendar.getInstance();
        String date = DateFormatUtil.getDate2String(System.currentTimeMillis(), "yyyy/MM/dd");
        String time = DateFormatUtil.getDate2String(System.currentTimeMillis(), "HH : mm");
        if (Global.ReleaseVersion == Global.HomeVersion){
            tvDate1.setText(date);
            tvTime1.setText(time);
            if (cal.get(Calendar.AM_PM) == Calendar.AM) {
                tvAmPm1.setText("AM");
            } else {
                tvAmPm1.setText("PM");
            }
        }else {
            tvDate.setText(date);
            tvTime.setText(time);
            if (cal.get(Calendar.AM_PM) == Calendar.AM) {
                tvAmPm.setText("AM");
            } else {
                tvAmPm.setText("PM");
            }
        }

    }

    private void goEvaluateDialog() {
        RxDialogSureCancel dialog = new RxDialogSureCancel(this);
        dialog.setContent("是否开始评估");
        dialog.setCancel("");
        dialog.setSure("开始评估");
        dialog.setSureListener(v -> {
            dialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectUserMode);
            startTargetActivity(bundle, ConnectDeviceActivity.class, false);
        });
//        dialog.setCancelListener(v -> {
//            dialog.dismiss();
//            Bundle bundle = new Bundle();
//            bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE,Global.ConnectMainMode);
//            startTargetActivity(bundle,ConnectDeviceActivity.class,false);
//        });
        dialog.show();
    }

    private void selectModeDialog() {
        RxDialogSureCancel dialog = new RxDialogSureCancel(this);
        dialog.setContent("选择功能");
        dialog.setCancel("康复评估");
        dialog.setSure("康复训练");
        dialog.setSureListener(v -> {
            dialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectMainMode);
            startTargetActivity(bundle, ConnectDeviceActivity.class, false);
        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectEvaluateMode);
            startTargetActivity(bundle, ConnectDeviceActivity.class, false);
        });
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Global.ReleaseVersion = SPHelper.getReleaseVersion();
//        Global.ReleaseVersion = Global.HomeVersion;
        if (Global.ReleaseVersion == Global.HomeVersion) {
            rlSample.setVisibility(View.VISIBLE);
            reRoot.setVisibility(View.GONE);
        } else {
            rlSample.setVisibility(View.GONE);
            reRoot.setVisibility(View.VISIBLE);
        }
//        TrainPlanManager.getInstance().refreshPlanStatus(SPHelper.getUserId());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        int selectUser = 0;
        int ResultTips = 0;
        int backHome = 0;
        if (bundle != null) {
            selectUser = bundle.getInt("SelectUser", 0);
            ResultTips = bundle.getInt("ResultTips", 0);
            backHome = bundle.getInt(AppKeyManager.EXTRA_BACK_HOME_SETTING, 0);
        }

        if (selectUser == 1 && NetworkUtils.isConnected() && TextUtils.isEmpty(SPHelper.getUser().getStr())) {
            syncPlan();
        }
        if (backHome == Global.HomeUser && SPHelper.getHomeSetting() == Global.HomeUser){
            mHandler.sendEmptyMessageDelayed(0,1000);
        }
        Log.appenderFlush();
//        Log.appenderFlushSync(false);
        //现在写文件的逻辑：调用 close或者 flush; 缓存区满50kb或者间隔 15min;下次启动的时候，把上次还没有写入文件的日志写入文件
//        if (ResultTips == 1) {
//            startTargetActivity(CommitResultActivity.class, false);
//
//        }
        super.onNewIntent(intent);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://收缩到0.8(正常值是1)，速度200
                v.animate().scaleX(0.8f).scaleY(0.8f).setDuration(200).start();
                touchFlag = false;
                break;
            case MotionEvent.ACTION_UP:
                v.animate().scaleX(1).scaleY(1).setDuration(200).start();
                if (touchFlag) return true;
                break;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        Log.appenderFlushSync(false);
        Log.appenderClose();
        super.onDestroy();
    }

    //透明度动画
    private void alphaAnimation() {
        //显示
        alphaAniShow = new AlphaAnimation(0, 1);//百分比透明度，从0%到100%显示
        alphaAniShow.setDuration(1000);//一秒

        //隐藏
        alphaAniHide = new AlphaAnimation(1, 0);
        alphaAniHide.setDuration(1000);
    }

    //位移动画
    private void translateAnimation() {


        //向上位移显示动画  从自身位置的最下端向上滑动了自身的高度
        translateAniShow = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,//RELATIVE_TO_SELF表示操作自身
                1,//fromXValue表示开始的X轴位置
                Animation.RELATIVE_TO_SELF,
                0,//fromXValue表示结束的X轴位置
                Animation.RELATIVE_TO_SELF,
                0,//fromXValue表示开始的Y轴位置
                Animation.RELATIVE_TO_SELF,
                0);//fromXValue表示结束的Y轴位置
        translateAniShow.setRepeatMode(Animation.REVERSE);
        translateAniShow.setDuration(1000);

        //向下位移隐藏动画  从自身位置的最上端向下滑动了自身的高度
        translateAniHide = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,//RELATIVE_TO_SELF表示操作自身
                0,//fromXValue表示开始的X轴位置
                Animation.RELATIVE_TO_SELF,
                0,//fromXValue表示结束的X轴位置
                Animation.RELATIVE_TO_SELF,
                0,//fromXValue表示开始的Y轴位置
                Animation.RELATIVE_TO_SELF,
                1);//fromXValue表示结束的Y轴位置
        translateAniHide.setRepeatMode(Animation.REVERSE);
        translateAniHide.setDuration(1000);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
            case 2:
                boolean isAllGranted = true;
                // 判断是否所有的权限都已经授予了
                for (int grant : grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        isAllGranted = false;
                        break;
                    }
                }
                if (isAllGranted) {
                    if (NetworkUtils.isConnected()) {
                        uploadVersionInfo();
                    }
                } else {
                    activationCodeDialog("必要的权限没有允许，请退出系统重新打开软件，允许权限", "退出系统", false);
                }
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (Settings.System.canWrite(this)) {
                Log.e("ERRRRRRRRRRRRR", "onActivityResult write settings granted");
            } else {
                Log.e("ERRRRRRRRRRRRR", "onActivityResult write settings not granted");
            }
        }else if (requestCode == REQUEST_CODE_MANAGER_FILES){
            if (Build.VERSION.SDK_INT >= 30) {
                if (!Environment.isExternalStorageManager()){
                    activationCodeDialog("必要的权限没有允许，请退出系统重新打开软件，允许权限", "退出系统", false);
                }
            }
        }
    }
}
