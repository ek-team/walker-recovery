package com.pharos.walker.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pharos.walker.R;
import com.pharos.walker.application.MyApplication;
import com.pharos.walker.beans.ActivationCodeBean;
import com.pharos.walker.beans.DataSendBean;
import com.pharos.walker.beans.EvaluateEntity;
import com.pharos.walker.beans.MigrationDataBean;
import com.pharos.walker.beans.PlanEntity;
import com.pharos.walker.beans.SubPlanEntity;
import com.pharos.walker.beans.TokenBean;
import com.pharos.walker.beans.TrainDataEntity;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.beans.UserTrainRecordEntity;
import com.pharos.walker.bluetooth.BluetoothController;
import com.pharos.walker.constants.Api;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.constants.SocketCmd;
import com.pharos.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharos.walker.database.ActivationCodeManager;
import com.pharos.walker.database.EvaluateManager;
import com.pharos.walker.database.SubPlanManager;
import com.pharos.walker.database.TrainDataManager;
import com.pharos.walker.database.TrainPlanManager;
import com.pharos.walker.database.UserManager;
import com.pharos.walker.database.UserTrainRecordManager;
import com.pharos.walker.services.SocketServices;
import com.pharos.walker.utils.AppUtils;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.GreenDaoHelper;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.NetworkUtils;
import com.pharos.walker.utils.OkHttpUtils;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.SimInfoUtils;
import com.pharos.walker.utils.SqlToExcleUtil;
import com.pharos.walker.utils.ToastUtils;
import com.pharos.walker.utils.Utils;
import com.pharos.walker.utils.WifiApUtil;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.client.impl.client.action.ActionDispatcher;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;
import com.xuhao.didi.socket.client.sdk.client.connection.NoneReconnect;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.util.ConvertUtils;
import okhttp3.Request;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by zhanglun on 2021/6/1
 * Describe:
 */
public class EngineerActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_version_build_name)
    TextView tvVersionBuildName;
    @BindView(R.id.btn_wifi)
    TextView btnWifi;
    @BindView(R.id.btn_mode_select)
    TextView btnModeSelect;
    @BindView(R.id.btn_start_connect)
    TextView btnStartConnect;
    @BindView(R.id.btn_send)
    TextView btnSend;
    @BindView(R.id.tv_msg)
    TextView tvMsg;
    @BindView(R.id.tv_wifi_ap_name)
    TextView tvWifiApName;
    @BindView(R.id.tv_sim_info)
    TextView tvSimInfo;
    @BindView(R.id.cb_socket_server)
    CheckBox cbSocketServer;
    @BindView(R.id.ll_connect_device)
    LinearLayout llConnectDevice;
    @BindView(R.id.rg_version_select)
    RadioGroup rgVersionSelect;
    @BindView(R.id.rg_home_page_select)
    RadioGroup rgHomePageSelect;
    @BindView(R.id.rg_1)
    RadioButton rgSingle;
    @BindView(R.id.rg_2)
    RadioButton rgClient;
    @BindView(R.id.rb_home_main)
    RadioButton rbHomeMain;
    @BindView(R.id.rb_home_user)
    RadioButton rbHomeUser;
    @BindView(R.id.debug_switch)
    Switch debugSwitch;
    @BindView(R.id.video_switch)
    Switch videoSwitch;
    @BindView(R.id.operation_switch)
    Switch operationSwitch;
    @BindView(R.id.weight_limit_switch)
    Switch weightLimitSwitch;
    @BindView(R.id.tv_save_serial_number)
    TextView tvSaveSerialNumber;
    @BindView(R.id.tv_mac)
    TextView tvMac;
    @BindView(R.id.et_serial_number)
    EditText etSerialNumber;
    @BindView(R.id.et_source_serial_number)
    EditText etSourceSerialNumber;
    private ConnectionInfo mInfo;
    private IConnectionManager mManager;
    private OkSocketOptions mOkOptions;
    private String ip;
    private int port = 9001;
    private boolean hotState = false;
    private Gson gson = new Gson();
    private HotspotReceiver hotspotReceiver;
    private ActivationCodeBean activationCodeBean;
    private int RESET_SCAN_USER = 5;

    @Override
    protected void initialize(Bundle savedInstanceState) {
//        Window window;
//        window = getWindow();
//        WindowManager.LayoutParams params = window.getAttributes();
//        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//        window.setAttributes(params);
        ButterKnife.bind(this);
        tvVersionBuildName.setText(AppUtils.getAppVersionName());
        initView();
        initManager();
        int wifiApStatus = WifiApUtil.getWifiApState();
        if (wifiApStatus == 13)
            hotState = true;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        hotspotReceiver = new HotspotReceiver();
        registerReceiver(hotspotReceiver, intentFilter);
        tvSimInfo.setText(MessageFormat.format("{0}", new SimInfoUtils(this).getIccid()));
        activationCodeBean = ActivationCodeManager.getInstance().getCodeBean();
        etSerialNumber.setText(SPHelper.getSerialNumber());
        if (activationCodeBean != null)
            tvMac.setText(activationCodeBean.getMacAddress());
    }

    private void initView() {
        if (Global.isOpenTest) {
            debugSwitch.setChecked(true);
        } else {
            debugSwitch.setChecked(false);
        }
        if (SPHelper.getDoctorVideoStatus()){
            videoSwitch.setChecked(true);
        }else {
            videoSwitch.setChecked(false);
        }
        if (SPHelper.getOperationSwitch()){
            operationSwitch.setChecked(true);
        }else {
            operationSwitch.setChecked(false);
        }
        if (SPHelper.getWeightLimitSwitch()){
            weightLimitSwitch.setChecked(true);
        }else {
            weightLimitSwitch.setChecked(false);
        }
        if (SPHelper.getServiceStatus() && hotState){
            llConnectDevice.setVisibility(View.GONE);
            startService(new Intent(this,SocketServices.class));
            cbSocketServer.setChecked(true);
        }else {
            cbSocketServer.setChecked(false);
//            llConnectDevice.setVisibility(View.VISIBLE);
        }
        if (SPHelper.getReleaseVersion() == Global.RecoveryVersion){
            rgSingle.setChecked(true);
        }else if (SPHelper.getReleaseVersion() == Global.OrthopedicsVersion){
            rgClient.setChecked(true);
        }
        if (SPHelper.getHomeSetting() == Global.HomeUser){
            rbHomeUser.setChecked(true);
        }else {
            rbHomeMain.setChecked(true);
        }
        cbSocketServer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SPHelper.saveServiceStatus(isChecked);
            Intent intent = new Intent(this, SocketServices.class);
            if (isChecked && hotState){
                startService(intent);
                llConnectDevice.setVisibility(View.GONE);
            }else if (!hotState){
                llConnectDevice.setVisibility(View.VISIBLE);
                ToastUtils.showShort("热点未打开");
            }else {
                llConnectDevice.setVisibility(View.VISIBLE);
                stopService(intent);
            }
        });
        debugSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> Global.isOpenTest = isChecked);
        videoSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> SPHelper.saveDoctorVideo(isChecked));
        operationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> SPHelper.saveOperationSwitch(isChecked));
        weightLimitSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> SPHelper.saveWeightLimitSwitch(isChecked));
        tvMsg.setMovementMethod(ScrollingMovementMethod.getInstance());
        rgVersionSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rg_1){
                    Global.ReleaseVersion = Global.RecoveryVersion;
                }else if (checkedId == R.id.rg_2){
                    Global.ReleaseVersion = Global.OrthopedicsVersion;
                }else if (checkedId == R.id.rg_3){
                    Global.ReleaseVersion = Global.HomeVersion;
                }
                SPHelper.saveReleaseVersion(Global.ReleaseVersion);
            }
        });
        rgHomePageSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_home_user){
                    Global.HomeSetting = Global.HomeUser;
                }else if (checkedId == R.id.rb_home_main){
                    Global.HomeSetting = Global.HomeMain;
                }
                SPHelper.saveHomeSetting(Global.HomeSetting);
            }
        });
    }

    private void initManager() {
        ip = getWifiRouteIPAddress(this);
        final Handler handler = new Handler();
        mInfo = new ConnectionInfo(ip, port);
        mOkOptions = new OkSocketOptions.Builder()
                .setReconnectionManager(new NoneReconnect())
                .setConnectTimeoutSecond(10)
                .setCallbackThreadModeToken(new OkSocketOptions.ThreadModeToken() {
                    @Override
                    public void handleCallbackEvent(ActionDispatcher.ActionRunnable runnable) {
                        handler.post(runnable);
                    }
                })
                .build();
        mManager = OkSocket.open(mInfo).option(mOkOptions);
        mManager.registerReceiver(adapter);
    }
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_engineer;
    }



    @OnClick({R.id.iv_back, R.id.tv_version_build_name, R.id.btn_wifi, R.id.btn_mode_select, R.id.btn_save_record,R.id.btn_start_connect, R.id.btn_send, R.id.btn_wifi_ap,
            R.id.btn_clear_log,R.id.btn_upload_log,R.id.btn_ble_device_info,R.id.btn_upload_data,R.id.btn_download_data,R.id.tv_save_serial_number,R.id.btn_system_default})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_version_build_name:
                break;
            case R.id.btn_wifi:
                if (DateFormatUtil.avoidFastClick(2000)) {
                    BluetoothController.getInstance().initBle();
                    Toast.makeText(this, "初始化成功", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_mode_select:
//                TrainPlanManager.getInstance().clearAllTrainPlan();
                break;
            case R.id.btn_save_record:
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_HOME_SETTINGS);
                startActivity(intent);
//                startActivity(new Intent(Settings.ACTION_HOME_SETTINGS));
//                Intent intentw = new Intent(Intent.ACTION_MAIN);
//                intentw.addCategory(Intent.CATEGORY_HOME);
//                intentw.setClassName("android",
//                        "com.android.internal.app.ResolverActivity");
//                startActivity(intentw);
//                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    Intent intent = new Intent(Settings.ACTION_HOME_SETTINGS);
//                    ComponentName componentName = new ComponentName("com.android.settings", "com.android.AdvancedAppsActivity");
//                    intent.setComponent(componentName);
//                    intent.setAction("android.intent.action.VIEW");
//                    startActivityForResult(intent, 0);
//                }
                break;
            case R.id.btn_start_connect:
                if (mManager == null) {
                    return;
                }
                if (!mManager.isConnect()) {
                    initManager();
                    mManager.connect();

                } else {
                    mManager.disconnect();
                    btnStartConnect.setText("开始连接");
                }

                break;
            case R.id.btn_send:
                if (mManager == null) {
                    return;
                }
                if (!mManager.isConnect()) {
                    Toast.makeText(getApplicationContext(), "Unconnected", LENGTH_SHORT).show();
                } else {
                    showWaiting("同步提示","正在同步...");
//                    String result = new Gson().toJson(SPHelper.getUser());
                    String request = SocketCmd.SYNC_USER + Global.Delimiter + TextUtils.join("," , UserManager.getInstance().loadAllUserId());
                    DataSendBean msgDataBean = new DataSendBean(request);
                    mManager.send(msgDataBean);
                }
                break;
            case R.id.btn_wifi_ap:
                if (!hotState){
                    WifiApUtil.openWifiAP();
                    showWaiting("操作提示","正在开启热点...");
                }else {
                    Toast.makeText(getApplicationContext(), "热点已开启", LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_clear_log:
                tvMsg.setText("");
                break;
            case R.id.btn_upload_log:
                if (NetworkUtils.isConnected()){
                    showWaiting("操作提示", "正在上传…");
                    postLogFiles();
                }else {
                    ToastUtils.showShort("网络不可用！");
                }
                break;
            case R.id.btn_ble_device_info:
                startTargetActivity(BleDeviceInfoActivity.class,false);
                break;
            case R.id.btn_upload_data:
                RxDialogSureCancel dialogSureCancel = new RxDialogSureCancel(this);
                dialogSureCancel.setContent("是否上传数据");
                dialogSureCancel.setSureListener(v -> {
                    showWaiting("上传数据","正在上传…");
                    Gson gson = new Gson();
                    writeJsonFile(gson.toJson(UserManager.getInstance().loadAllExceptGuest()),"user");
                    writeJsonFile(gson.toJson(TrainPlanManager.getInstance().loadAll()),"plan");
                    writeJsonFile(gson.toJson(SubPlanManager.getInstance().loadAll()),"subPlan");
                    writeJsonFile(gson.toJson(UserTrainRecordManager.getInstance().loadAll()),"trainRecord");
                    writeJsonFile(gson.toJson(TrainDataManager.getInstance().loadAll()),"trainData");
                    writeJsonFile(gson.toJson(EvaluateManager.getInstance().loadAll()),"evaluateRecord");
                    postDataFiles();
                    dialogSureCancel.dismiss();
                });
                dialogSureCancel.show();
                break;
            case R.id.btn_download_data:
                String sourceSerial = etSourceSerialNumber.getText().toString();
                if(TextUtils.isEmpty(sourceSerial)){
                    ToastUtils.showShort("请输入源设备序列号");
                    return;
                }
                RxDialogSureCancel dialog = new RxDialogSureCancel(this);
                dialog.setContent("是否下载数据");
                dialog.setSureListener(v -> {
                    showWaiting("下载数据","正在下载…");
                    downloadDataFiles();
                    dialog.dismiss();
                });
                dialog.show();
                break;
            case R.id.tv_save_serial_number:
//                String filePath = Environment.getExternalStorageDirectory() + File.separator + "Android" +
//                        File.separator + "fls" + File.separator + "data";
//                File file = new File(filePath);
//                if (!file.exists()){
//                    file.mkdirs();
//                }
//                String path = filePath + File.separator + "sn.txt";
                String serialNumber = etSerialNumber.getText().toString();
                if (!TextUtils.isEmpty(serialNumber)){
                    SPHelper.saveSerialNumber(serialNumber);
                    ToastUtils.showShort("保存成功");
                }else {
                    ToastUtils.showShort("序列号不能为空");
                }
                break;
            case R.id.btn_system_default:
                selectModeDialog();
                break;
        }
    }
    private void selectModeDialog() {
        RxDialogSureCancel dialog = new RxDialogSureCancel(this);
        dialog.setContent("是否恢复出厂设置");
        dialog.setCancel("取消");
        dialog.setSure("恢复出厂设置");
        dialog.setSureListener(v -> {
            dialog.dismiss();
            ActivationCodeBean activationCodeBean = ActivationCodeManager.getInstance().getCodeBean();
//            MyUtil.deleteFile(new File("data/data/" + Utils.getApp().getPackageName()));
            GreenDaoHelper.deleteSQL();
            MyUtil.deleteFile(new File("data/data/" + Utils.getApp().getPackageName() + "/shared_prefs"));
            ActivationCodeManager.getInstance().insertCodeBean(activationCodeBean);
            SPHelper.saveUser(UserManager.getInstance().initUser(0L));//创建初始用户，并保存到本地
            Global.USER_MODE = false;
//            SPHelper.saveMacAddress(activationCodeBean.getMacAddress());
            showWaiting("恢复出厂操作", "正在恢复出厂设置…");
            if (NetworkUtils.isConnected()) {
                resetScanUser();
            } else {
                ToastUtils.showShort("网络不可用");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
//            exitSystemDialog("恢复出厂完成，请手动重启机器","确认");
        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }
    private void writeJsonFile(String json,String type){
        String path = getFilesDir().getPath() + File.separator + "upload_files";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            path = file.getAbsolutePath() + File.separator + activationCodeBean.getMacAddress().replaceAll(":","") + "_" + type + ".json";
            File e = new File(path);
            FileOutputStream fos;
            if (e.exists()) {
                e.delete();
            }
            fos = new FileOutputStream(e);
            fos.write(json.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("path", path);
    }
    private void postDataFiles(){
        List<String> pathList = MyUtil.getFilesAllName(getFilesDir().getPath() + File.separator + "upload_files");
        if (pathList == null){
            if (progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            return;
        }
        uploadDataFiles(pathList);
    }
    private void resetScanUser() {
        OkHttpUtils.getAsync(Api.clearPlatformQrScanUser + "?macAdd=" + ActivationCodeManager.getInstance().getCodeBean().getMacAddress(), true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                ToastUtils.showShort("访问服务器出错");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                com.tencent.mars.xlog.Log.e("User Activity", "重置扫码用户返回结果: " + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0) {
                    deleteScanUser();
//                    exitSystemDialog("恢复出厂完成，请手动重启机器","确认");
                } else if (code == 401) {
                    getToken(RESET_SCAN_USER);
                } else {
                    ToastUtils.showShort("访问服务器出错");
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }

            }
        });
    }

    private void deleteScanUser() {
        OkHttpUtils.deleteAsyncToken(Api.deletePlatformQrScanUser + ActivationCodeManager.getInstance().getCodeBean().getMacAddress(), new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                ToastUtils.showShort("访问服务器出错");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                com.tencent.mars.xlog.Log.e("User Activity", "重置扫码用户返回结果: " + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    exitSystemDialog("恢复出厂完成，请手动重启机器", "确认");
                } else if (code == 401) {
                    getToken(RESET_SCAN_USER);
                } else {
                    ToastUtils.showShort("访问服务器出错");
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }

            }
        });
    }
    private void exitSystemDialog(String content, String sureText) {
        RxDialogSureCancel rxDialog = new RxDialogSureCancel(this);
        rxDialog.setContent(content);
        rxDialog.setCancel("");
        rxDialog.setSure(sureText);
        rxDialog.setCancelable(false);
        rxDialog.setCanceledOnTouchOutside(false);
        rxDialog.setSureListener(v -> {
//            Process.killProcess(Process.myPid());
//            System.exit(0);
            rxDialog.dismiss();
        });
        rxDialog.show();
    }
    private void uploadDataFiles(List<String> fileList){
        OkHttpUtils.postFormFilesAsync(Api.uploadDataFiles + "?macAdd=" + activationCodeBean.getMacAddress(),fileList, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                com.tencent.mars.xlog.Log.e("Engineer Activity", e.getMessage());
                ToastUtils.showShort("服务器请求异常");
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                com.tencent.mars.xlog.Log.e("Setting Activity", "--->上传数据返回结果" + result);
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                ToastUtils.showShort("上传完成");
            }
        });
    }
    private void getToken(int flag) {
        OkHttpUtils.getAsyncToken(Api.tokenUrl + "?grant_type=client_credentials", true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                com.tencent.mars.xlog.Log.e("MainActivity", "requestSuccess: " + result);
                TokenBean tokenBean = new Gson().fromJson(result, TokenBean.class);
                if (tokenBean.getCode() == 0) {
                    SPHelper.saveToken(tokenBean.getData().getAccess_token());
                   if (flag == RESET_SCAN_USER) {
                        resetScanUser();
                    }
                }
            }
        });

    }
    int currentPosition = 0;
    private void postLogFiles(){
        String logPath = Environment.getExternalStorageDirectory().getPath() + "/FLSLog/xlog/";
        List<String> fileList = MyUtil.getFilesAllName(logPath,".xlog");
        if (fileList == null){
            if (progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            return;
        }
        uploadLog(fileList,currentPosition);
    }

    private void uploadLog(List<String> fileList,int position){
        OkHttpUtils.postFormFileAsync(Api.uploadFiles,fileList.get(position), new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                com.tencent.mars.xlog.Log.e("Engineer Activity", e.getMessage());
                ToastUtils.showShort("服务器请求异常");
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                com.tencent.mars.xlog.Log.e("Setting Activity", "--->上传log返回结果" + result);
                addLinkToDevice(result,fileList,currentPosition++);
            }
        });
    }

    private void addLinkToDevice(String url,List<String> fileList,int position){
        Map<String,String> map = new HashMap<>();
        map.put("macAdd", ActivationCodeManager.getInstance().getCodeBean().getMacAddress());
        map.put("logUrl",url);
        OkHttpUtils.postJsonAsync(Api.addLinkToDevice, new Gson().toJson(map), new OkHttpUtils.DataCallBack(){
            @Override
            public void requestFailure(Request request, IOException e) {
                com.tencent.mars.xlog.Log.e("Engineer Activity", e.getMessage());
                ToastUtils.showShort("服务器请求异常");
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                com.tencent.mars.xlog.Log.e("Setting Activity", "--->绑定log链接返回结果" + result);
                if (position < fileList.size()){
                    uploadLog(fileList,position);
                }else {
                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    ToastUtils.showShort("上传成功");
                }
            }
        });
    }
    private void downloadDataFiles(){
        File filesDir = getFilesDir();
        String downloadPath = filesDir.getPath() + File.separator + "download_files";
        File file = new File(downloadPath);
        if (!file.exists()) {
            file.mkdir();
        }

        if (fileNameList.size() > 0)
            fileNameList.clear();
        OkHttpUtils.getAsync(Api.downloadDataFiles +"?macAdd=" + activationCodeBean.getMacAddress() + "&sourceProductSn=" + etSourceSerialNumber.getText().toString(),
                true, new OkHttpUtils.DataCallBack() {
                    @Override
                    public void requestFailure(Request request, IOException e) {
                        if (progressDialog != null && progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        ToastUtils.showShort("数据请求失败");
                    }

                    @Override
                    public void requestSuccess(String result) throws Exception {
                        MigrationDataBean migrationDataBean = new Gson().fromJson(result, MigrationDataBean.class);
                        com.tencent.mars.xlog.Log.e("Engineer Activity", "--->下载数据返回结果" + result);
                        if (migrationDataBean.getCode() == 0){
                            List<String> urlList = new ArrayList<>();
                            if (!TextUtils.isEmpty(migrationDataBean.getData().getUser())){
                                urlList.add(migrationDataBean.getData().getUser());
                            }
                            if (!TextUtils.isEmpty(migrationDataBean.getData().getPlan())){
                                urlList.add(migrationDataBean.getData().getPlan());
                            }
                            if (!TextUtils.isEmpty(migrationDataBean.getData().getSubPlan())){
                                urlList.add(migrationDataBean.getData().getSubPlan());
                            }
                            if (!TextUtils.isEmpty(migrationDataBean.getData().getTrainData())){
                                urlList.add(migrationDataBean.getData().getTrainData());
                            }
                            if (!TextUtils.isEmpty(migrationDataBean.getData().getEvaluateRecord())){
                                urlList.add(migrationDataBean.getData().getEvaluateRecord());
                            }
                            if (!TextUtils.isEmpty(migrationDataBean.getData().getTrainRecord())){
                                urlList.add(migrationDataBean.getData().getTrainRecord());
                            }
                            saveFile(urlList,0,downloadPath);
                        }else {
                            if (progressDialog != null && progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            ToastUtils.showShort("未获取到设备数据");
                        }
//                if (result != null){
//                }else {
//                    ToastUtils.showShort("下载失败");
//                }

                    }
                });
    }
    private List<String> fileNameList = new ArrayList<>();
    private void saveFile(List<String> urlList,int position,String filePath){
        OkHttpUtils.downloadAsync(urlList.get(position), filePath, false,new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                ToastUtils.showShort("下载失败");
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("engineer", "requestSuccess:下载成功 " + result );
                fileNameList.add(result);
                if (position + 1 >= urlList.size()){
                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    loadData();
                    ToastUtils.showShort("下载成功");
                }else {
                    saveFile(urlList,position + 1,filePath);
                }
            }
        });
    }
    private void loadData(){
        List<UserBean> userBeanList = new ArrayList<>();
        List<PlanEntity> planBeanList = new ArrayList<>();
        List<SubPlanEntity> subPlanBeanList = new ArrayList<>();
        List<UserTrainRecordEntity> trainRecordBeanList = new ArrayList<>();
        List<TrainDataEntity> trainDataBeanList = new ArrayList<>();
        List<EvaluateEntity> evaluateBeanList = new ArrayList<>();
        for (String filename: fileNameList){
            try {
                String json = ConvertUtils.toString(new FileInputStream(filename));
                if (filename.endsWith("_user.json")){
                    userBeanList.addAll(new Gson().fromJson(json, new TypeToken<List<UserBean>>() {}.getType()));
                    UserManager.getInstance().insert(userBeanList);
                }
                if (filename.endsWith("_plan.json")){
                    planBeanList.addAll(new Gson().fromJson(json, new TypeToken<List<PlanEntity>>() {}.getType()));
                    TrainPlanManager.getInstance().insertMany(planBeanList);
                }
                if (filename.endsWith("_subPlan.json")){
                    subPlanBeanList.addAll(new Gson().fromJson(json, new TypeToken<List<SubPlanEntity>>() {}.getType()));
                    SubPlanManager.getInstance().insert(subPlanBeanList);
                }
                if (filename.endsWith("_trainData.json")){
                    trainDataBeanList.addAll(new Gson().fromJson(json, new TypeToken<List<TrainDataEntity>>() {}.getType()));
                    TrainDataManager.getInstance().insertMany(trainDataBeanList);
                }
                if (filename.endsWith("_evaluateRecord.json")){
                    evaluateBeanList.addAll(new Gson().fromJson(json, new TypeToken<List<EvaluateEntity>>() {}.getType()));
                    EvaluateManager.getInstance().insert(evaluateBeanList);
                }
                if (filename.endsWith("_trainRecord.json")){
                    trainRecordBeanList.addAll(new Gson().fromJson(json, new TypeToken<List<UserTrainRecordEntity>>() {}.getType()));
                    UserTrainRecordManager.getInstance().insertMany(trainRecordBeanList);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static String getWifiRouteIPAddress(Context context) {
        WifiManager wifi_service = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();
        String routeIp = Formatter.formatIpAddress(dhcpInfo.gateway);
        Log.i("route ip", "wifi route ip：" + routeIp);
        return routeIp;
    }
    private SocketActionAdapter adapter = new SocketActionAdapter() {

        @Override
        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
            tvMsg.append("断开连接！！！");
        }

        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
            tvMsg.append("连接成功！！！");
            btnStartConnect.setText("断开连接");
        }

        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
            tvMsg.append("连接失败！！！");
        }

        @Override
        public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
            String receiveResult = new String(data.getBodyBytes(), Charset.forName("utf-8"));
            if (TextUtils.isEmpty(receiveResult))
                return;
            handleReceiveData(receiveResult);

            tvMsg.append("收到数据：" + receiveResult + "     ");
        }

        @Override
        public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
            String str = new String(data.parse(), Charset.forName("utf-8"));
            tvMsg.append("发送数据：" + str + "     ");
        }
    };

    private void handleReceiveData(String receiveResult) {
        int endPosition = receiveResult.indexOf("[");
        if (endPosition <= 0){
            endPosition = receiveResult.indexOf("{");
        }
        String dataStr = receiveResult.substring(endPosition);
        String head = receiveResult.substring(0,endPosition);
        String request;
        DataSendBean msgDataBean;
        switch (head){
            case SocketCmd.SYNC_USER_ASK:
                if (dataStr.length() > 2){
                    UserBean userBean = gson.fromJson(dataStr, UserBean.class);
                    UserManager.getInstance().insertSyncUser(userBean);
                }

                break;
            case SocketCmd.SYNC_USER_FINISH:
                request = SocketCmd.SYNC_PLAN + gson.toJson(UserManager.getInstance().loadAllUserIdPlanUpdate());
                msgDataBean = new DataSendBean(request);
                mManager.send(msgDataBean);
                break;
            case SocketCmd.SYNC_PLAN_ASK:
                Type type = new TypeToken<List<PlanEntity>>(){}.getType();
                List<PlanEntity> planEntityList = gson.fromJson(dataStr,type);
                TrainPlanManager.getInstance().insertMany(planEntityList);
                String userId = "";
                if (planEntityList.size() > 0){
                    userId = planEntityList.get(0).getUserId() + "";
                }
                if (TextUtils.isEmpty(userId))
                    return;
                request = SocketCmd.SYNC_SUB_PLAN + Global.Delimiter + userId;
                msgDataBean = new DataSendBean(request);
                mManager.send(msgDataBean);
                break;
            case SocketCmd.SYNC_PLAN:
                String userIdStr = dataStr.replaceAll("\\[","");
                if (!TextUtils.isEmpty(userIdStr)){
                    List<PlanEntity> planList = TrainPlanManager.getInstance().getPlanListByUserId(Long.parseLong(userIdStr));
                    String result = SocketCmd.SYNC_PLAN_ASK + gson.toJson(planList);
                    msgDataBean = new DataSendBean(result);
                    mManager.send(msgDataBean);
                }
                break;
            case SocketCmd.SYNC_SUB_PLAN_ASK:
                Type type1 = new TypeToken<List<SubPlanEntity>>(){}.getType();
                List<SubPlanEntity> subPlanEntityList = gson.fromJson(dataStr,type1);
                SubPlanManager.getInstance().insertMany(subPlanEntityList);
                break;
            case SocketCmd.SYNC_TRAIN_RECORD:
                List<Long> ids = UserManager.getInstance().loadAllUserIdRecord();
                for (Long id : ids){
                    List<UserTrainRecordEntity> listRecord = UserTrainRecordManager.getInstance().loadAllUpdate(id);
                    for (UserTrainRecordEntity entity : listRecord){
                        String result = SocketCmd.SYNC_TRAIN_RECORD_ASK + Global.Delimiter + gson.toJson(entity);
                        DataSendBean dataSendBean = new DataSendBean(result);
                        mManager.send(dataSendBean);
                    }
                    UserBean userBean = UserManager.getInstance().loadByUserId(id);
                    userBean.setIsRecordUpdate(1);
                    UserManager.getInstance().update(userBean);
                    UserTrainRecordManager.getInstance().updateTrainUploadStatus(listRecord);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                progressDialog.dismiss();
                ToastUtils.showShort("同步成功");
                break;
            case SocketCmd.SYNC_TRAIN_DATA:
                String userInfo = dataStr.replaceAll("\\[","");
                String [] infoArray = userInfo.split(Global.Comma);
                if (!TextUtils.isEmpty(infoArray[0])){
                    List<TrainDataEntity> trainDataEntityList = TrainDataManager.getInstance().
                            getTrainDataByDateAndFrequency(Long.parseLong(infoArray[0]),infoArray[1],Integer.parseInt(infoArray[2])-1);
                    String result = SocketCmd.SYNC_TRAIN_DATA_ASK + gson.toJson(trainDataEntityList);
                    DataSendBean dataSendBean = new DataSendBean(result);
                    mManager.send(dataSendBean);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mManager != null) {
            mManager.disconnect();
            mManager.unRegisterReceiver(adapter);
        }
        if (hotspotReceiver != null){
            unregisterReceiver(hotspotReceiver);//注销广播接收器
        }
    }
    class HotspotReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.net.wifi.WIFI_AP_STATE_CHANGED")) {//便携式热点的状态为：10---正在关闭；11---已关闭；12---正在开启；13---已开启
                int state = intent.getIntExtra("wifi_state", 0);
                if (state == 10) {
                    tvWifiApName.setText("热点状态：正在关闭");
                } else if (state == 11) {
                    tvWifiApName.setText("热点状态：已关闭");
                    hotState = false;
                } else if (state == 12) {
                    tvWifiApName.setText("热点状态：正在开启");
                } else if (state == 13) {
                    tvWifiApName.setText("热点状态：已经打开");
                    hotState = true;
                    if (progressDialog != null){
                        progressDialog.dismiss();
                    }
                    ToastUtils.showShort("热点已打开");
                }else {
                    if (progressDialog != null){
                        progressDialog.dismiss();
                    }
                    ToastUtils.showShort("热点开启失败");
                }
            }
        }
    }
}

