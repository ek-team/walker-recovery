package com.pharos.walker.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pharos.walker.beans.Battery;
import com.pharos.walker.R;
import com.pharos.walker.application.MyApplication;
import com.pharos.walker.beans.EvaluateEntity;
import com.pharos.walker.beans.MigrationDataBean;
import com.pharos.walker.beans.PlanEntity;
import com.pharos.walker.beans.SubPlanEntity;
import com.pharos.walker.beans.TrainDataEntity;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.beans.UserTrainRecordEntity;
import com.pharos.walker.bluetooth.BluetoothController;
import com.pharos.walker.constants.Api;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.database.ActivationCodeManager;
import com.pharos.walker.database.EvaluateManager;
import com.pharos.walker.database.SubPlanManager;
import com.pharos.walker.database.TrainDataManager;
import com.pharos.walker.database.TrainPlanManager;
import com.pharos.walker.database.UserManager;
import com.pharos.walker.database.UserTrainRecordManager;
import com.pharos.walker.utils.DataTransformUtil;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.NetworkUtils;
import com.pharos.walker.utils.OkHttpUtils;
import com.pharos.walker.utils.ShellUtils;
import com.pharos.walker.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.util.ConvertUtils;
import okhttp3.Request;


/**
 * @Description: 前台工作服务（设置为前台优先级最高，软件启动就开始了）
 * @Author: zf
 * @Time 2019/4/25
 */
public class ForegroundWorkService extends Service {

    public static final String CMD = "cmd";
    /**
     * id不可设置为0,否则不能设置为前台service
     */
    private static final int NOTIFICATION_DOWNLOAD_PROGRESS_ID = 0x0001;

    private boolean isTimeFlag = true;
    private TimeThread mTimeThread;
    private HeartThread mHeartThread;
    private BatteryRefreshThread batteryRefreshThread;
    private CheckHeartThread checkHeartThread;
    private CheckHeartThreadTrain checkHeartThreadTrain;
    private ScreenReceiver screenReceiver;
    private TelephonyManager mTelephonyManager;
    private IBinder mBinder = new LocalBinder();
    public class LocalBinder extends Binder {
        public ForegroundWorkService getService() {
            return ForegroundWorkService.this;
        }
    }


    public static void launch() {
        launch(null);
    }

    public static void launch(String cmd) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MyApplication.getInstance().startForegroundService(new Intent(MyApplication.getInstance(), ForegroundWorkService.class)
                    .putExtra(CMD, cmd));
        }else {
            MyApplication.getInstance().startService(new Intent(MyApplication.getInstance(), ForegroundWorkService.class)
                    .putExtra(CMD, cmd));
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
        mTimeThread = new TimeThread();
        mTimeThread.start();
        mHeartThread = new HeartThread();
        mHeartThread.start();
        batteryRefreshThread = new BatteryRefreshThread();
        batteryRefreshThread.start();
        checkHeartThread = new CheckHeartThread();
        checkHeartThread.start();
        checkHeartThreadTrain = new CheckHeartThreadTrain();
        checkHeartThreadTrain.start();
        setScreenReceiver();
        mTelephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener();
        Log.d("Service", "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        String cmd = intent.getStringExtra(CMD);
//        Log.d("Service", "onStartCommand()" +startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
//        Log.d("Service", "onBind()");
        return mBinder;
    }
    private void setScreenReceiver(){
        screenReceiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter();
        //添加要注册的action
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        //动态注册广播接收者
        registerReceiver(screenReceiver, filter);
    }
    @Override
    public void onDestroy() {
        if (mTimeThread != null) {
            mTimeThread.interrupt();
            isTimeFlag = false;
            mTimeThread = null;
        }
        if (mHeartThread != null){
            mHeartThread.interrupt();
            Global.isSendHeart = false;
            mHeartThread = null;
        }
        if (batteryRefreshThread != null){
            batteryRefreshThread.interrupt();
            batteryRefreshThread = null;
        }
        if (checkHeartThread != null){
            checkHeartThread.interrupt();
            checkHeartThread = null;
        }
        if (checkHeartThreadTrain != null){
            checkHeartThreadTrain.interrupt();
            checkHeartThreadTrain = null;
        }
        stopForeground(true);
        unregisterReceiver(screenReceiver);//注销广播接收器
        Log.e("ForegroundWorkService", "onDestroy: "+"死掉了" );
        super.onDestroy();
    }

    /**
     * Notification
     */
    public void createNotification() {
        String channel_id = "com.pharos.walker";
        String channelName = "MyWorkService";

        //使用兼容版本
        Notification.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(channel_id, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);
            builder = new Notification.Builder(this,channel_id);
        }else {
            builder = new Notification.Builder(this);
        }
        //设置状态栏的通知图标
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //禁止用户点击删除按钮删除
        builder.setAutoCancel(true);
        //禁止滑动删除
        builder.setOngoing(true);
        //右上角的时间显示
        builder.setShowWhen(true);
        //设置通知栏的标题内容
        builder.setContentTitle("助行");
//        builder.setContentText("连接你我");
        //创建通知
        Notification notification = builder.build();
        //设置为前台服务
        startForeground(NOTIFICATION_DOWNLOAD_PROGRESS_ID, notification);
    }


    //动态更新时间的线程
    private class TimeThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.UPDATE_TOP_TIME));
                if (Global.isUploadAllData){
                    new Thread(() -> {
                        Gson gson = new Gson();
                        writeJsonFile(gson.toJson(UserManager.getInstance().loadAllExceptGuest()),"user");
                        writeJsonFile(gson.toJson(TrainPlanManager.getInstance().loadAll()),"plan");
                        writeJsonFile(gson.toJson(SubPlanManager.getInstance().loadAll()),"subPlan");
                        writeJsonFile(gson.toJson(UserTrainRecordManager.getInstance().loadAll()),"trainRecord");
                        writeJsonFile(gson.toJson(TrainDataManager.getInstance().loadAll()),"trainData");
                        writeJsonFile(gson.toJson(EvaluateManager.getInstance().loadAll()),"evaluateRecord");
                        postDataFiles();
                        Global.isUploadAllData = false;
                    }).start();

                }
                if (Global.isDownloadAllData && !TextUtils.isEmpty(Global.downloadSourceSerial)){
                    downloadDataFiles();
                    Global.isDownloadAllData = false;
                    Global.downloadSourceSerial = null;
                }
                SystemClock.sleep(1000);
            } while (isTimeFlag);
        }
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
        OkHttpUtils.getAsync(Api.downloadDataFiles +"?macAdd=" + ActivationCodeManager.getInstance().getCodeBean().getMacAddress() + "&sourceProductSn=" + Global.downloadSourceSerial,
                true, new OkHttpUtils.DataCallBack() {
                    @Override
                    public void requestFailure(Request request, IOException e) {
//                        ToastUtils.showShort("数据请求失败");
                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.DOWNLOAD_FAIL,"下载链接请求失败"));
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
//                            ToastUtils.showShort("未获取到设备数据");
                            EventBus.getDefault().post(new MessageEvent<>(MessageEvent.DOWNLOAD_FAIL,"未获取到设备数据"));
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
//                ToastUtils.showShort("下载失败");
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.DOWNLOAD_FAIL,"下载失败"));
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("engineer", "requestSuccess:下载成功 " + result );
                fileNameList.add(result);
                if (position + 1 >= urlList.size()){
                    loadData();
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.DOWNLOAD_FINISH));
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
    private void postDataFiles(){
        List<String> pathList = MyUtil.getFilesAllName(getFilesDir().getPath() + File.separator + "upload_files");
        if (pathList == null){

            return;
        }
        uploadDataFiles(pathList);
    }
    private void writeJsonFile(String json,String type){
        String path = getFilesDir().getPath() + File.separator + "upload_files";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            path = file.getAbsolutePath() + File.separator + ActivationCodeManager.getInstance().getCodeBean().getMacAddress().replaceAll(":","") + "_" + type + ".json";
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
    private void uploadDataFiles(List<String> fileList){
        OkHttpUtils.postFormFilesAsync(Api.uploadDataFiles + "?macAdd=" + ActivationCodeManager.getInstance().getCodeBean().getMacAddress(),fileList, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                com.tencent.mars.xlog.Log.e("Engineer Activity", e.getMessage());

            }

            @Override
            public void requestSuccess(String result) throws Exception {
                com.tencent.mars.xlog.Log.e("Setting Activity", "--->上传数据返回结果" + result);

//                ToastUtils.showShort("上传完成");
            }
        });
    }
    private void updateProductStatus(String parmas){
        StringBuilder sb = new StringBuilder();
//        sb.append("{");
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
//        sb.append("}");
        OkHttpUtils.putJsonAsync(Api.updateCloudDeviceStatus, sb.toString(), new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {

            }

            @Override
            public void requestSuccess(String result) throws Exception {

            }
        });

    }
    //动态更新时间的线程
    private class BatteryRefreshThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                BatteryManager manager = (BatteryManager) getSystemService(BATTERY_SERVICE);
                int batteryVolume = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);//当前电量百分比
                int batteryStatus = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);
//                ShellUtils.CommandResult result = NetworkUtils.pingResult();
//                if (result.result == 0){
//                    int beginIndex = result.successMsg.indexOf("time=") + 5;
//                    String time = result.successMsg.substring(beginIndex,beginIndex+3);
//                    Log.e("service", "run: " + time );
//                }else {
//                    Log.e("service", "run: " + 999 );
//                }

//                phoneStateListener();
                if (batteryVolume >= 100){
                    batteryVolume = 100;
                }else if (batteryVolume <= 0){
                    batteryVolume = 9;
                }
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.BATTERY_REFRESH, new Battery(batteryStatus,batteryVolume)));
                SystemClock.sleep(1000);
            } while (isTimeFlag);
        }
    }
    //维持心跳线程 一分钟和下位机通信一次
    private class HeartThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                if (Global.isSendHeart && Global.isConnected){
                    if (!Global.isStartReadData){
                        int a = 0x1A;
                        int b = 0x04 | 0x30;
                        int c = 0x00;
                        int d = 0xFF - (a + b + c) + 1;
                        String message = ":1A" + DataTransformUtil.toHexString((byte) b) + "00" + DataTransformUtil.toHexString((byte) d);
                        BluetoothController.getInstance().writeRXCharacteristic(Global.ConnectedAddress,message.getBytes(StandardCharsets.UTF_8));
//                HermesEventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_HEART_STATUS, heartStatus));
                        SystemClock.sleep(30 * 1000);
                    }
                }
            }while (isTimeFlag);
        }
    }
    //检测心跳线程
    private class CheckHeartThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                if (Global.isConnected && !Global.isStartReadData){
                    if (Global.ReadCount == 1){
                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_GATT_DISCONNECTED));
                    }else {
                        Global.ReadCount = 1;
                    }
                    SystemClock.sleep(30*1000 + 1);
                }
            }while (isTimeFlag);
        }
    }
    //检测心跳线程
    private class CheckHeartThreadTrain extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                if (Global.isConnected && Global.isStartReadData){
                    if (Global.ReadCount == 1){
                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.READ_DATA_HEART_DISCONNECT));
                    }else {
                        Global.ReadCount = 1;
                    }
                    SystemClock.sleep(2000);
                }
            }while (isTimeFlag);
        }
    }
    public class ScreenReceiver extends BroadcastReceiver {

        //当我们进行屏幕锁屏和解锁 这个方法执行
        @Override
        public void onReceive(Context context, Intent intent) {

            //获取当前广播的事件类型
            String action = intent.getAction();
            if ("android.intent.action.SCREEN_OFF".equals(action)) {
                Log.e("Work Service", "onReceive: 熄屏了" );
                ShellUtils.execCmd("reboot -p",true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ShellUtils.execCmd("reboot -p",true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ShellUtils.execCmd("reboot -p",true);
//                ShellUtils.execCmd("shutdown -h now",true);
            } else if ("android.intent.action.SCREEN_ON".equals(action)) {
                Log.e("Work Service", "onReceive: 屏幕亮了" );
            }
        }
    }
    PhoneStateListener phoneStateListener = new PhoneStateListener(){
        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            super.onServiceStateChanged(serviceState);
            Log.d("phoneStateListener", "onServiceStateChanged: "+serviceState.toString());
        }

        @Override
        public void onDataConnectionStateChanged(int state, int networkType) {
            super.onDataConnectionStateChanged(state, networkType);
            Log.d("phoneStateListener", "onDataConnectionStateChanged: state "+state+" networkType "+networkType);
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            int level = signalStrength.getLevel();
            Log.d("phoneStateListener", "onSignalStrengthsChanged: level "+level);
        }

    };
    private void phoneStateListener(){
        if (mTelephonyManager != null){
            mTelephonyManager.listen(phoneStateListener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE | PhoneStateListener.LISTEN_SERVICE_STATE);
        }
    }
}
