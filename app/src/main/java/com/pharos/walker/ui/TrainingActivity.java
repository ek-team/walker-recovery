package com.pharos.walker.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.anastr.speedviewlib.SpeedView;
import com.github.anastr.speedviewlib.components.Indicators.Indicator;
import com.github.anastr.speedviewlib.components.note.Note;
import com.github.anastr.speedviewlib.components.note.TextNote;
import com.google.android.exoplayer2.ui.PlayerView;
import com.pharos.walker.MainActivity;
import com.pharos.walker.R;
import com.pharos.walker.beans.Battery;
import com.pharos.walker.beans.BleBean;
import com.pharos.walker.beans.GameEntity;
import com.pharos.walker.beans.MessageBean;
import com.pharos.walker.beans.TrainDataEntity;
import com.pharos.walker.bluetooth.BluetoothController;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.customview.VerticalProgressBar;
import com.pharos.walker.customview.WaveLoadingView;
import com.pharos.walker.customview.electime.ElecTimeNumView;
import com.pharos.walker.customview.rxdialog.RxDialog;
import com.pharos.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharos.walker.database.TrainPlanManager;
import com.pharos.walker.database.UserTrainRecordManager;
import com.pharos.walker.utils.DataTransformUtil;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.FpsTest;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.NetworkUtils;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.SnowflakeIdUtil;
import com.pharos.walker.utils.SpeechUtil;
import com.pharos.walker.utils.ToastUtils;
import com.pharos.walker.utils.VideoPlayUtil;
import com.tencent.mars.xlog.Log;
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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/4/14
 * Describe:
 */
public class TrainingActivity extends BaseActivity {
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
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.waveLoadingView)
    WaveLoadingView waveLoadingView;
    @BindView(R.id.speedview)
    SpeedView speedviewV1;
    @BindView(R.id.vp_progress)
    VerticalProgressBar vpProgress;
    @BindView(R.id.tv_effective_time)
    TextView tvEffectiveTime;
    @BindView(R.id.tv_total_time)
    TextView tvTotalTime;
    @BindView(R.id.layout_time)
    LinearLayout layoutTime;
    @BindView(R.id.tv_warning_time)
    TextView tvWarningTime;
    @BindView(R.id.layout_warning_time)
    LinearLayout layoutWarningTime;
    @BindView(R.id.iv_wifi_setting)
    ImageView ivWifiSetting;
    @BindView(R.id.iv_video_game)
    ImageView ivVideoGame;
    @BindView(R.id.iv_video_game_off)
    ImageView ivVideoGameOff;
    @BindView(R.id.tv_stop)
    TextView tvStop;
    @BindView(R.id.tv_train_time)
    TextView tvTrainTime;
    @BindView(R.id.rl_root_v1)
    RelativeLayout rlRootV1;
    @BindView(R.id.ll_title_train_time)
    LinearLayout llTitleTrainTime;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private MediaPlayer mediaPlayer;
    private int count = 0;
    private int notcount = 0;
    private int warningcount = 0;
    private int totalTrainingTime = 0;
    private int totalCount = 0;
    private int trainingTime = 0;
    private float weight = 0;
    private float minWeight;
    private float maxWeight;
    private int planid = 0;
    private int level = 1;
    private int planTrainNum = 10;
    private int musicPosition;
    private ArrayList<TrainDataEntity> trainDataEntityList;
    private IConnectionManager mManager;
    private static final String TAG = "TrainingActivity";
    private static final int delayTime = 4000;
    private SpeedView speedview;
    private List<String> listMusic;
    private int lastStepCount = 0;
    private List<Float> fakeDataList = new ArrayList<>();
    private int simulationDataCount = 0;
    private boolean isUnConnected = false;
    private boolean isHeartConnected = false;
    private boolean isDisconnectedDialogShow = false;
    private boolean trainFinish = false;

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        speedview = speedviewV1;
        rlRootV1.setVisibility(View.VISIBLE);
        initView();
        initManager();
        SPHelper.saveRebootTime(0);

    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        listMusic = MyUtil.getFilesAllName(Environment.getExternalStorageDirectory() + File.separator + "android" + File.separator + "fls" + File.separator + "background_data");
        if (bundle != null) {
            trainingTime = bundle.getInt(AppKeyManager.EXTRA_TIMENUM, 0);
            totalTrainingTime = trainingTime * 60;
            totalCount = totalTrainingTime;
            weight = bundle.getInt(AppKeyManager.EXTRA_WEIGHT, 0);
            musicPosition = bundle.getInt(AppKeyManager.EXTRA_MUSIC_FILE, 0);
            planTrainNum = bundle.getInt(AppKeyManager.EXTRA_PLAN_TRAIN_NUM, 0);
        }
        mediaPlayer = new MediaPlayer();
//        Global.isReconnectBle = true;
        isHeartConnected = Global.isConnected;
        Global.isStartReadData = true;
    }

    @SuppressLint("NewApi")
    private void initView() {
        minWeight = weight * 0.8f;
        maxWeight = weight * 1.2f;
        speedview.setIndicator(Indicator.Indicators.KiteIndicator);
        speedview.setIndicatorColor(Color.WHITE);
        if (!Global.isChangSha) {
            speedview.setLowSpeedColor(getColor(R.color.speed_yellow));
            speedview.setMediumSpeedColor(getColor(R.color.speed_green));
        }
        speedview.setLowSpeedPercent(40);
        speedview.setMediumSpeedPercent(60);
        speedview.setWithTremble(false);
        speedview.setMaxSpeed(weight * 2);
        speedview.setTickNumber(11);
        speedview.setTickPadding(40);
//        planid = b.getInt(AppKeyManager.EXTRA_PLANID, 0);
//        trainingTime = b.getInt(AppKeyManager.EXTRA_TREAD_NUM, 0);
//        tvTotalTime.setText(MessageFormat.format("/{0}次", planTrainNum));
        if (SPHelper.getReleaseVersion() == Global.OrthopedicsVersion){
            vpProgress.setMaxProgress(planTrainNum * 1.0f);
            llTitleTrainTime.setVisibility(View.GONE);
//            tvTotalTime.setVisibility(View.GONE);
        }else {
            vpProgress.setMaxProgress(totalTrainingTime * 1.0f);
            llTitleTrainTime.setVisibility(View.VISIBLE);
//            tvTotalTime.setVisibility(View.VISIBLE);
        }
        new FpsTest().startFps();

    }
    private void playMusic(int position,int mode){
        try {
//            AssetFileDescriptor fd = getAssets().openFd(musicPosition + ".mp3");
//            mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            if (listMusic != null && listMusic.size() > 0 && position < 6){
                mediaPlayer.reset();
                mediaPlayer.setDataSource(listMusic.get(position));
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }else if ((listMusic == null || listMusic.size() <=0) && position != 6){
                AssetFileDescriptor fd = getAssets().openFd(0 + ".mp3");
                mediaPlayer.reset();
                mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();
                if (mode == 1 && position != 0){
                    ToastUtils.showShort("未获取到选择的背景音乐,开始播放默认的音乐");
                }
//                ToastUtils.showShort("未获取到背景音乐");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMusicSelectedChanged(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        musicPosition = position;
        playMusic(position,1);
        super.onMusicSelectedChanged(position);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_training;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Global.isConnected){
            EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_GATT_DISCONNECTED));
        }
//        if (Global.isOpenTest){
//            simulationData();
//        }
        startTask();
        if (SPHelper.getReleaseVersion() == Global.RecoveryVersion){
            countDownThread();
        }
        timerCheckStepCount();
        if (mediaPlayer != null){
            playMusic(musicPosition,0);
        }
        BluetoothController.getInstance().startDealThread();
//        OneShotUtil.getInstance(this).startOneShot();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//            curIndex = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();

        }
    }
    private void simulationData(){
        if (fakeDataList != null && fakeDataList.size() > 0){
            fakeDataList.clear();
        }
        for (int i = 0; i < 50 ;i+=2){
            if (fakeDataList != null) {
                fakeDataList.add((float) i);
            }
        }
        for (int i = 50; i > 0 ;i-=2){
            if (fakeDataList != null) {
                fakeDataList.add((float) i);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        BleBean bleBean;
        switch (event.getAction()) {
            case MessageEvent.ACTION_GATT_CONNECTED:
                bleBean = (BleBean) event.getData();
                if (SPHelper.getReleaseVersion() == Global.RecoveryVersion){
                    countDownThread();
                }
                setPoint(true);
                Global.ReadCount = 0;
                Global.ConnectedAddress = bleBean.getAddress();
                Global.ConnectedName = bleBean.getName();
                break;
            case MessageEvent.ACTION_GATT_DISCONNECTED:
                bleBean = (BleBean) event.getData();
                clearTimerTask();
                setPoint(false);
//                Toast.makeText(this, getString(R.string.ble_disconnect), Toast.LENGTH_SHORT).show();
//                SpeechUtil.getInstance(this).speak("蓝牙鞋已断开正在重新连接");
                if (isHeartConnected){
                    return;
                }else if (!Global.isConnected){
//                    Toast.makeText(this, getString(R.string.ble_disconnect), Toast.LENGTH_SHORT).show();
//                    bleUnconnectedDialog();
//                    Global.isStartReadData = false;
//                    BluetoothController.getInstance().stopDealThread();
                }
//                BluetoothController.getInstance().connect(Global.TempConnectedAddress);
//                Log.e(TAG, "onMessageEvent: " + "关闭上次连接，开始新的连接" + Global.TempConnectedAddress);

                break;
            case MessageEvent.ACTION_READ_DATA:
                synchronized (this){
                    Global.ReadCount = 0;
                }
                bleBean = (BleBean) event.getData();
                String value = bleBean.getData();
//                if (Global.isOpenTest){
//                    refreshView(fakeDataList.get(simulationDataCount));
//                    simulationDataCount++;
//                    if (simulationDataCount >= fakeDataList.size()){
//                        simulationDataCount = 0;
//                    }
//                }else {
//                    refreshView(Float.valueOf(value));
//                }
                refreshView(Float.valueOf(value));
                break;
            case MessageEvent.GATT_TRANSPORT_OPEN:
                startTask();
                Global.ReadCount = 0;
                Global.ConnectStatus = "connected";
                BluetoothController.getInstance().startDealThread();
                isUnConnected = false;
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                    progressDialog = null;
                    SpeechUtil.getInstance(this).speak("连接成功");
                }
                clearBLeDisconnectTimer();
                isHeartConnected = true;
                break;
            case MessageEvent.BATTERY_REFRESH:
                Battery battery = (Battery) event.getData();
                setBattery(battery.getBatteryVolume(), battery.getBatteryStatus());
                break;
            case MessageEvent.ACTION_READ_DEVICE:
                int bleBattery = (int) event.getData();
                setTvBleBattery(bleBattery);
                break;
            case MessageEvent.ACTION_COUNTDOWN:
                int countTime = (int) event.getData();
                if (SPHelper.getReleaseVersion() != Global.OrthopedicsVersion){
                    vpProgress.setProgress(totalCount - countTime);//设置进度条
                }
                tvTrainTime.setText(DateFormatUtil.getMinuteTime(countTime));
                break;
            case MessageEvent.ACTION_TRAIN_TIPS:
                SpeechUtil.getInstance(this).speak("太用力了");
                noteCenterIndicator("太用力了!!");
                break;
            case MessageEvent.ACTION_GAME_DOG:
                new Thread(() -> {
                    try {
                        Thread.sleep(delayTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mManager.send(new MessageBean(new GameEntity(GameEntity.OPERATING_START_GAME_DOG)));
                }).start();

            case MessageEvent.ACTION_UNCONNECTED_TIMEOUT:
                if (isUnConnected && !isDisconnectedDialogShow && progressDialog == null){
                    SpeechUtil.getInstance(this).speak("蓝牙鞋已断开正在重新连接");
                    showWaiting("提示","蓝牙已断开，正在重新连接…");
                    bleDisconnectTimer();
                    isHeartConnected = false;
                }
                break;
            case MessageEvent.ACTION_HEART_GATT_DISCONNECTED:
                Toast.makeText(this, getString(R.string.ble_disconnect), Toast.LENGTH_SHORT).show();
                bleUnconnectedDialog();
                Global.isStartReadData = false;
                BluetoothController.getInstance().stopDealThread();
                break;
            case MessageEvent.READ_DATA_HEART_DISCONNECT:
                Log.e(TAG, "onMessageEvent: " + "心跳停了");
                if (Global.ConnectedAddress != null) {
                    isUnConnected = true;
                    isHeartConnected = false;
                    bleUnconnectedTimer();
                    Log.e(TAG, "onMessageEvent: " + "关闭上次连接，开始新的连接");
                    clearTimerTask();
                    BluetoothController.getInstance().closeGatt(Global.ConnectedAddress);
                    BluetoothController.getInstance().connect(Global.ConnectedAddress);
//                    BluetoothController.getInstance().clearConnectedStatus();
                    Global.isStartReadData = false;
                }
//                Global.isConnected = false;
                break;
            default:
                break;
        }
    }
    private Timer mTimer3;
    private TimerTask mTimerTask3;
    private void bleUnconnectedTimer() {
        if (mTimer3 == null && mTimerTask3 == null) {
            mTimer3 = new Timer();
            mTimerTask3 = new TimerTask() {
                @Override
                public void run() {
                    mTimer3.cancel();
                    mTimerTask3.cancel();
                    mTimer3 = null;
                    mTimerTask3 = null;
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_UNCONNECTED_TIMEOUT));
                }
            };
            mTimer3.schedule(mTimerTask3, 2000);
        }
    }
    private Timer mTimer4;
    private TimerTask mTimerTask4;
    private int timerCount = 30;
    private void bleDisconnectTimer() {
        if (mTimer4 == null && mTimerTask4 == null) {
            mTimer4 = new Timer();
            mTimerTask4 = new TimerTask() {
                @Override
                public void run() {
                    timerCount--;
                    if (timerCount <= 0){
                        Global.isConnected = false;
                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_HEART_GATT_DISCONNECTED));
                        clearBLeDisconnectTimer();
                    }
                }
            };
            mTimer4.schedule(mTimerTask4, 0,1000);
        }
    }
    private void clearBLeDisconnectTimer(){
        if (mTimer4 != null){
            mTimer4.cancel();
            mTimer4 = null;
            mTimerTask4.cancel();
            mTimerTask4 = null;
        }
        timerCount = 30;
    }
    private void bleUnconnectedDialog(){
        if (isDisconnectedDialogShow)
            return;
        RxDialogSureCancel dialog = new RxDialogSureCancel(this);
        dialog.setTitle("蓝牙鞋连接失败!");
        dialog.setContent("1.请检查蓝牙鞋是否打开。\n2.检查蓝牙鞋是否在附近。");
        dialog.setCancel("等待连接");
        dialog.setSure("退出");
        dialog.setSureListener(v -> {
            if (progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            dialog.dismiss();
            startTargetActivity( MainActivity.class, true);
        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
            isDisconnectedDialogShow = false;
            clearBLeDisconnectTimer();
            bleDisconnectTimer();
            BluetoothController.getInstance().connect(Global.TempConnectedAddress);
            Log.e(TAG, "onMessageEvent: " + "关闭上次连接，开始新的连接" + Global.TempConnectedAddress);
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        isDisconnectedDialogShow = true;
    }

    float currentMaxweight;
    boolean isEffect = false;
    boolean isWarning = false;
    boolean isDidNotMakeIt = false;//判断最大值是否在黄色范围，便于记录未达标的数据

    private void refreshView(Float value) {
        if (trainFinish)
            return;
        if (value < 0.5f) {
            value = 0.0f;
            speedview.speedTo(value, 300);
        } else {
            speedview.speedTo(value, 300);
        }
        mManager.send(new MessageBean(new GameEntity(GameEntity.OPERATING_SET_COUNT, 99, count)));
        mManager.send(new MessageBean(new GameEntity(GameEntity.OPERATING_SET_VALUE, value, weight)));
        if (value >= maxWeight && DateFormatUtil.avoidFastClick(1000)) {
            EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_TRAIN_TIPS));
        }
        // 计算压力最高值并保存
        // 计算压力最高值并保存
        if (currentMaxweight < value) {
            currentMaxweight = value;
        }
        if (currentMaxweight >= minWeight && currentMaxweight < maxWeight) {
            if (!isWarning) {
                isEffect = true;
                isDidNotMakeIt = false;
            }
        } else if (currentMaxweight >= maxWeight) {
            isWarning = true;
            isEffect = false;
            isDidNotMakeIt = false;
        } else if (currentMaxweight < minWeight && currentMaxweight > weight * 0.3) {
            if (!isWarning && !isEffect) {
                isDidNotMakeIt = true;
            }
        }

        if (isDidNotMakeIt && !isWarning && !isEffect) {
            noteCenterIndicator("加油");
        }

        if (value <= weight * 0.2 && (isDidNotMakeIt || isEffect || isWarning)) {
            insertData(currentMaxweight, weight);
            currentMaxweight = 0;
            if (isWarning) {
                warningcount++;
                tvWarningTime.setText(MessageFormat.format("{0}", warningcount));
//                if (!Global.isOpenTest && count + warningcount == planTrainNum){
//                    trainFinish = true;
////                    timerDelay();
////                    startFeedback();
//                }
            }
            if (isEffect) {
                SpeechUtil.getInstance(this).speak("太棒了，完成一次");
                count++;
                tvEffectiveTime.setText(MessageFormat.format("{0}", count));
                if (SPHelper.getReleaseVersion() == Global.OrthopedicsVersion){
                    vpProgress.setProgress(count);
                }
//                if (!Global.isOpenTest && count + warningcount == planTrainNum){
//                    trainFinish = true;
////                    timerDelay();
////                    startFeedback();
//                }
            }
            if (isDidNotMakeIt){
                notcount++;
            }
            isEffect = false;
            isWarning = false;
            isDidNotMakeIt = false;
        }


    }
    private Timer timer;
    private TimerTask timerTask;
    private void timerDelay(){
        if (timer == null && timerTask == null){
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    timer.cancel();
                    timerTask.cancel();
                    timerTask = null;
                    timer = null;
                    startFeedback();
                }
            };
            timer.schedule(timerTask,1000,1000);
        }
    }
    private void insertData(float realLoad, float targetLoad) {
        if (!Global.USER_MODE)//访客模式下不记录数据
            return;
        if (realLoad > 200)//过滤异常数据
            return;
        if (trainDataEntityList == null) {
            trainDataEntityList = new ArrayList<>();
        }
        TrainDataEntity entity = new TrainDataEntity();
        entity.setRealLoad((int) realLoad);
        entity.setTargetLoad((int) targetLoad);
        long userId = SPHelper.getUserId();
        long date = System.currentTimeMillis();
        entity.setCreateDate(date);
        entity.setDateStr(DateFormatUtil.getDate2String(date, AppKeyManager.DATE_YMD));
        entity.setPlanId(TrainPlanManager.getInstance().getCurrentPlanId(userId));
        entity.setClassId(TrainPlanManager.getInstance().getCurrentClassId(userId));
        entity.setIsUpload(0);
        entity.setUserId(userId);
        entity.setFrequency(UserTrainRecordManager.getInstance().getLastTimeFrequency(userId));
        entity.setKeyId(SnowflakeIdUtil.getUniqueId());
        trainDataEntityList.add(entity);
//        TrainDataManager.getInstance().insert(entity);
    }

    private void startTask() {
        if (mTimer == null && mTimerTask == null) {
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (Global.isConnected){
                        int a = 0x1A;
                        int b = 0x04 | 0x10;
                        int c = 0x00;
                        int d = 0xFF - (a + b + c) + 1;
                        String message = ":1A" + DataTransformUtil.toHexString((byte) b) + "00" + DataTransformUtil.toHexString((byte) d);
                        Log.d("发送数据", "run: " + message);
                        Global.isStartReadData = true;
                        BluetoothController.getInstance().writeRXCharacteristic(Global.ConnectedAddress, message.getBytes(StandardCharsets.UTF_8));
                    }
                }
            };
            mTimer.schedule(mTimerTask, 0, 1000);
        }
    }

    private Timer mTimer1;
    private TimerTask mTimerTask1;

    private void countDownThread() {
        if (mTimer1 == null && mTimerTask1 == null) {
            mTimer1 = new Timer();
            mTimerTask1 = new TimerTask() {
                @Override
                public void run() {
                    totalTrainingTime--;
                    if (totalTrainingTime <= 0) {
                        mTimer1.cancel();
                        mTimerTask1.cancel();
                        if (!Global.isOpenTest){
                            startFeedback();
                        }
                    }
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_COUNTDOWN, totalTrainingTime));
                }
            };
            mTimer1.schedule(mTimerTask1, 0, 1000);
        }
    }

    private void clearTimerTask() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimerTask.cancel();
            mTimer = null;
            mTimerTask = null;
        }
        if (mTimer1 != null) {
            mTimer1.cancel();
            mTimerTask1.cancel();
            mTimer1 = null;
            mTimerTask1 = null;
        }
    }

    @OnClick({R.id.iv_back, R.id.iv_wifi_setting, R.id.iv_video_game, R.id.iv_video_game_off, R.id.tv_stop, R.id.iv_game_dog})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_wifi_setting:
                NetworkUtils.openWirelessSettings();
                break;
            case R.id.iv_video_game:
            case R.id.iv_video_game_off:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (NetworkUtils.isWifiConnected()) {
                            if (mManager != null && !mManager.isConnect()) {
                                connectServer();
                            } else if (mManager != null) {
                                mManager.disconnect();
                            }
                        } else {
//                            ToastUtils.showShort("wifi 没有连接");
                            SpeechUtil.getInstance(TrainingActivity.this).speak("wifi没有连接");
                        }
                    }
                }).start();

                break;
            case R.id.iv_game_dog:
                if (mManager != null && mManager.isConnect()) {
                    mManager.send(new MessageBean(new GameEntity(GameEntity.OPERATING_START_GAME)));
                    new Thread(() -> {
                        try {
                            Thread.sleep(delayTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mManager.send(new MessageBean(new GameEntity(GameEntity.OPERATING_START_GAME_DOG)));
                    }).start();
//                    timerSend();
                } else if (mManager != null && !mManager.isConnect() && NetworkUtils.isWifiConnected()) {
                    connectServer();
                } else {
//                            ToastUtils.showShort("游戏没有连接");
                    SpeechUtil.getInstance(TrainingActivity.this).speak("游戏没有连接");
                }
                break;
            case R.id.tv_stop:
            case R.id.iv_back:
                RxDialogSureCancel dialog = new RxDialogSureCancel(this);
                dialog.setContent("是否退出训练？");
                dialog.setSureListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startFeedback();
                        dialog.dismiss();
                        if (mManager != null && mManager.isConnect()) {
                            mManager.send(new MessageBean(new GameEntity(GameEntity.OPERATING_STOP_GAME)));
                        }
                    }
                });
                dialog.show();
                break;
        }
    }

    private void startFeedback() {
        if (!Global.USER_MODE || (warningcount + count <=0)){
            startTargetActivity(MainActivity.class, true);
            return;
        }
        Bundle b = new Bundle();
        float completeRate = 0;
        float rightRate = 0;
        if (count + warningcount + notcount == 0){
            b.putFloat(AppKeyManager.EXTRA_SCORE, 0);
        }else {
            rightRate = (float)count/(count + warningcount + notcount);
            b.putFloat(AppKeyManager.EXTRA_SCORE, getStarByRate(rightRate));
        }
        completeRate = (totalCount - totalTrainingTime)/(float)totalCount;
//        completeRate = (float)(count + warningcount)/planTrainNum;
        b.putFloat(AppKeyManager.EXTRA_COMPLETE_RATE, getStarByRate(completeRate));
        b.putFloat(AppKeyManager.EXTRA_COMPLETE_SOURCE, completeRate);
        b.putFloat(AppKeyManager.EXTRA_RIGHT_RATE, rightRate);
        b.putInt(AppKeyManager.EXTRA_LEVEL, level);
        b.putInt(AppKeyManager.EXTRA_TRAIN_TIME, totalCount - totalTrainingTime);
//        b.putInt(AppKeyManager.EXTRA_TRAIN_TIME, trainingTime);
        b.putInt(AppKeyManager.EXTRA_EFFECTIVE_TIME, count);
        b.putInt(AppKeyManager.EXTRA_NOTE_ERRORNUMBER, warningcount);
        b.putInt(AppKeyManager.EXTRA_WEIGHT, (int) weight);
        if (trainDataEntityList != null) {
            b.putParcelableArrayList(AppKeyManager.EXTRA_TRAIN_DATA_ARRAY, trainDataEntityList);
        }
        if (mManager != null && mManager.isConnect()) {
            mManager.send(new MessageBean(new GameEntity(GameEntity.OPERATING_STOP_GAME)));
        }
        startTargetActivity(b, FeedbackOtherActivity.class, true);

    }

    private void initManager() {
        String ip = getWifiRouteIPAddress(this);
        final Handler handler = new Handler();
        ConnectionInfo mInfo;
        int port = 8080;
        mInfo = new ConnectionInfo(ip, port);
        OkSocketOptions mOkOptions = new OkSocketOptions.Builder()
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
    private float getStarByRate(float rate){
        if (rate > 0 && rate <= 0.1f){
            return 0.5f;
        }
        if (rate > 0.1 && rate <= 0.2f){
            return 1f;
        }
        if (rate > 0.2f && rate <= 0.3f){
            return 1.5f;
        }
        if (rate > 0.3 && rate <= 0.4f){
            return 2f;
        }
        if (rate > 0.4 && rate <= 0.5f){
            return 2.5f;
        }
        if (rate > 0.5f && rate <= 0.6f){
            return 3f;
        }
        if (rate > 0.6 && rate <= 0.7f){
            return 3.5f;
        }
        if (rate > 0.7 && rate <= 0.8f){
            return 4f;
        }
        if (rate > 0.8 && rate <= 0.9f){
            return 4.5f;
        }
        if (rate > 0.9){
            return 5f;
        }
        return 0;
    }
    private SocketActionAdapter adapter = new SocketActionAdapter() {
        @Override
        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
            Log.e(TAG, "onSocketDisconnection: 断开连接！！！ ");
            ivVideoGameOff.setImageResource(R.drawable.ic_videogame_off);
//            cancleSend();
        }

        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
            Log.e(TAG, "onSocketConnectionSuccess: 连接成功！！！");
            ivVideoGameOff.setImageResource(R.drawable.ic_videogame);
            mManager.send(new MessageBean(new GameEntity(GameEntity.OPERATING_START_GAME)));
            EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_GAME_DOG));
//            timerSend();
        }

        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
            Log.e(TAG, "onSocketConnectionFailed: 连接失败！！！");
            ToastUtils.showShort("连接失败。请检查网络是否连接");
            ivVideoGameOff.setImageResource(R.drawable.ic_videogame_off);
//            cancleSend();
        }

        @Override
        public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
            String receiveResult = new String(data.getBodyBytes(), Charset.forName("utf-8"));
            Log.e(TAG, "onSocketReadResponse: 接收的消息" + receiveResult);
        }

        @Override
        public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
            String str = new String(data.parse(), Charset.forName("utf-8"));
            Log.d(TAG, "onSocketWriteResponse: 发送数据：" + str);
        }
    };
    private Timer mTimer2;
    private TimerTask mTimerTask2;

    private void timerCheckStepCount() {
        if (mTimer2 == null && mTimerTask2 == null) {
            mTimer2 = new Timer();
            mTimerTask2 = new TimerTask() {
                @Override
                public void run() {
                    if (count + warningcount - lastStepCount < Global.TrainCountMinute * 0.6){
                        SpeechUtil.getInstance(TrainingActivity.this).speak("最近一分钟训练的有点慢哦，可以适当提高踩踏次数");
                    }else if (count + warningcount - lastStepCount > Global.TrainCountMinute * 1.4){
                        SpeechUtil.getInstance(TrainingActivity.this).speak("最近一分钟训练的速度有点快，可以适当降低踩踏次数");
                    }
                    lastStepCount = count + warningcount;
                }
            };
            mTimer2.schedule(mTimerTask2, 60 * 1000, 60 * 1000);
        }
    }

    private void cancelSend() {
        if (mTimer2 != null) {
            mTimer2.cancel();
            mTimerTask2.cancel();
            mTimer2 = null;
            mTimerTask2 = null;
        }
    }

    private static String getWifiRouteIPAddress(Context context) {
        WifiManager wifi_service = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();
        String routeIp = Formatter.formatIpAddress(dhcpInfo.gateway);
        Log.i("route ip", "wifi route ip：" + routeIp);
        return routeIp;
    }

    private void connectServer() {
        if (mManager == null) {
//            ToastUtils.showShort("对象创建失败");
            return;
        }
        if (!mManager.isConnect()) {
            initManager();
            mManager.connect();
        }

    }

    @Override
    protected void onStop() {
        Global.isReconnectBle = false;
        mediaPlayer.pause();
        mediaPlayer.stop();
        Global.isStartReadData = false;
        Global.ReadCount = 0;
        VideoPlayUtil.getInstance().destroyPlayer();
//        OneShotUtil.getInstance(this).stopOneShot();
        super.onStop();
    }

    public void noteCenterIndicator(String text) {
        TextNote note = new TextNote(getApplicationContext(), text)
                .setPosition(Note.Position.CenterIndicator)
                .setTextTypeFace(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
                .setTextSize(speedview.dpTOpx(25f));
        speedview.addNote(note, 200);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        clearTimerTask();
        cancelSend();
        if (mManager != null) {
            mManager.disconnect();
            mManager.unRegisterReceiver(adapter);
        }
        releasePlayer();
        BluetoothController.getInstance().stopDealThread();
        super.onDestroy();
    }
    /**
     * 释放播放器资源
     */
    private void releasePlayer() {
        if(mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }
}
