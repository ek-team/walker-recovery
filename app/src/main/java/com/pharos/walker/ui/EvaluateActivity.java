package com.pharos.walker.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dx.command.Main;
import com.github.anastr.speedviewlib.SpeedView;
import com.github.anastr.speedviewlib.components.Indicators.Indicator;
import com.github.anastr.speedviewlib.components.note.Note;
import com.github.anastr.speedviewlib.components.note.TextNote;
import com.pharos.walker.MainActivity;
import com.pharos.walker.R;
import com.pharos.walker.beans.Battery;
import com.pharos.walker.beans.BleBean;
import com.pharos.walker.beans.EvaluateEntity;
import com.pharos.walker.beans.MessageBean;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.bluetooth.BluetoothController;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.customview.DoubleSlideSeekBar;
import com.pharos.walker.customview.WaveLoadingView;
import com.pharos.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharos.walker.customview.rxdialog.RxPlanConfirmDialog;
import com.pharos.walker.customview.rxdialog.RxRadioButtonDialog;
import com.pharos.walker.database.EvaluateManager;
import com.pharos.walker.database.UserManager;
import com.pharos.walker.utils.AsyncThreadTask;
import com.pharos.walker.utils.DataTransformUtil;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.SpeechUtil;
import com.pharos.walker.utils.ToastUtils;
import com.tencent.mars.xlog.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/4/27
 * Describe:
 */
public class EvaluateActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.double_slide)
    DoubleSlideSeekBar doubleSlide;
    @BindView(R.id.waveLoadingView)
    WaveLoadingView waveLoadingView;
    @BindView(R.id.speedview)
    SpeedView speedview;
    @BindView(R.id.tv_vas)
    TextView tvVas;
    @BindView(R.id.tv_study_setting)
    TextView tvStudySetting;
    @BindView(R.id.img_reduce)
    ImageView imgReduce;
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.img_increase)
    ImageView imgIncrease;
    @BindView(R.id.btn_clear)
    TextView btnClear;
    @BindView(R.id.btn_commit)
    TextView btnCommit;
    @BindView(R.id.algorithm_spinner)
    Spinner algorithmSpinner;
    @BindView(R.id.ed_remote_calibration)
    EditText edRemoteCalibration;
    @BindView(R.id.tv_remote_calibration)
    TextView tvRemoteCalibration;
    @BindView(R.id.tv_first_value)
    TextView tvFirstValue;
    @BindView(R.id.tv_second_value)
    TextView tvSecondValue;
    @BindView(R.id.tv_third_value)
    TextView tvThirdValue;
    @BindView(R.id.tv_fourth_value)
    TextView tvFourthValue;
    @BindView(R.id.tv_fifth_value)
    TextView tvFifthValue;
    @BindView(R.id.tv_evaluate_tips1)
    TextView tvEvaluateTips1;
    @BindView(R.id.tv_evaluate_tips)
    TextView tvEvaluateTips;
    @BindView(R.id.ll_function_setting)
    LinearLayout llFunctionSetting;
    @BindView(R.id.ll_evaluate_1)
    LinearLayout llEvaluate1;
    @BindView(R.id.waveLoadingView_1)
    WaveLoadingView waveLoadingView1;
    @BindView(R.id.speedview_1)
    SpeedView speedview1;
    @BindView(R.id.tv_effective_time)
    TextView tvEffectiveTime;
    @BindView(R.id.layout_time)
    LinearLayout layoutTime;
    @BindView(R.id.tv_warning_time)
    TextView tvWarningTime;
    @BindView(R.id.layout_warning_time)
    LinearLayout layoutWarningTime;
    @BindView(R.id.tv_stop)
    TextView tvStop;
    @BindView(R.id.rl_root_v1)
    RelativeLayout rlRootV1;
    private float weightValue = 0;
    private float saveResult = 0;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private int selectPosition = 1;
    private int vasValue = 1;
    private int mode = 0;
    private static int ClickCount = 0;
    private static long ClickTime = 0;
    private boolean firstEvaluateFinish = false;
    private boolean secondEvaluateFinish = false;
    private boolean thirdEvaluateFinish = false;
    private boolean evaluateFinish = false;
    private final int evaluateMaxCount = 5;
    private final float threshold = 0.5f;
    private boolean isUnConnected = false;
    private boolean isHeartConnected = false;
    private boolean isDisconnectedDialogShow = false;
    List<Float> floatList = new ArrayList<>();
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                int[] list = (int[]) msg.obj;
                updateSpeedView(list[0], list[1]);
            }
        }
    };

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);

    }

    private void initData() {
        Global.isStartReadData = true;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mode = bundle.getInt("TrainParam", 0);
        }
    }

    private void initView() {
        speedview.setIndicator(Indicator.Indicators.KiteIndicator);
        speedview.setIndicatorColor(Color.WHITE);
        speedview.setWithTremble(false);
        if (Global.USER_MODE){
            speedview.setMaxSpeed(Float.parseFloat(SPHelper.getUser().getWeight()) + 10);
        }else {
            speedview.setMaxSpeed(100);
        }
        speedview.setMinSpeed(0);
        speedview.setTickNumber(11);
        speedview.setTickPadding(36);
        doubleSlide.setOnRangeListener((low, big) -> {
            int[] list = {0, 0};
            Message msg = new Message();
            msg.what = 0;
            list[0] = Math.round(low);
            list[1] = Math.round(big);
            msg.obj = list;
            mHandler.sendMessage(msg);
        });
        tvStudySetting.setOnLongClickListener(v -> {
            RxRadioButtonDialog dialog = new RxRadioButtonDialog(EvaluateActivity.this);
            dialog.setSureListener(v1 -> {
                tvVas.setText(MessageFormat.format("VAS {0}分", dialog.selectValue));
                vasValue = dialog.selectValue;
                dialog.dismiss();
            });
            dialog.show();
            return false;
        });
        algorithmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectPosition = 1;
                    clearView();
                } else {
                    selectPosition = position;
                    clearView();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateSpeedView(float min, float max) {
        speedview.setIndicator(Indicator.Indicators.KiteIndicator);
        speedview.setIndicatorColor(Color.WHITE);
        speedview.setWithTremble(false);
        speedview.setMaxSpeed(max);
        speedview.setMinSpeed(min);
        speedview.setTickNumber(11);
        speedview.setTickPadding(36);
        speedview.speedTo(min, 10);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        BleBean bleBean;
        switch (event.getAction()) {
            case MessageEvent.ACTION_GATT_CONNECTED:
                bleBean = (BleBean) event.getData();
                setPoint(true);
                Global.ReadCount = 0;
                Global.ConnectedAddress = bleBean.getAddress();
                Global.ConnectedName = bleBean.getName();
                break;
            case MessageEvent.ACTION_GATT_DISCONNECTED:
                bleBean = (BleBean) event.getData();
                clearTimerTask();
                setPoint(false);
                if (isHeartConnected) {
                    return;
                } else if (!Global.isConnected) {
//                    Toast.makeText(this, getString(R.string.ble_disconnect), Toast.LENGTH_SHORT).show();
//                    bleUnconnectedDialog();
//                    Global.isStartReadData = false;
//                    BluetoothController.getInstance().stopDealThread();
                }
                break;
            case MessageEvent.ACTION_READ_DATA:
                bleBean = (BleBean) event.getData();
                synchronized (this) {
                    Global.ReadCount = 0;
                }
                String value = bleBean.getData();
                if (rlRootV1.getVisibility() == View.VISIBLE){
                    testRefreshView(Float.valueOf(value));
                }else if (selectPosition == 2) {
                    refreshView(Float.valueOf(value));
                } else {
                    calculMinValue(Float.valueOf(value));
                }
//                refreshView(Float.valueOf(value));
                break;
            case MessageEvent.GATT_TRANSPORT_OPEN:
                startTask();
                isUnConnected = false;
                Global.ReadCount = 0;
                Global.ConnectStatus = "connected";
                BluetoothController.getInstance().startDealThread();
                if (progressDialog != null && progressDialog.isShowing()) {
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
            case MessageEvent.ACTION_HEART_GATT_DISCONNECTED:
                bleUnconnectedDialog();
                Global.isStartReadData = false;
                BluetoothController.getInstance().stopDealThread();
                break;
            case MessageEvent.ACTION_UNCONNECTED_TIMEOUT:
                android.util.Log.e("evaluate activity", "onMessageEvent: " + "超时机制触发" );
                if (isUnConnected && !isDisconnectedDialogShow && progressDialog == null) {
                    android.util.Log.e("evaluate activity", "onMessageEvent: " + "重连弹框" );
                    SpeechUtil.getInstance(this).speak("蓝牙鞋已断开正在重新连接");
                    showWaiting("提示", "蓝牙已断开，正在重新连接…");
                    bleDisconnectTimer();
                    isHeartConnected = false;
                }
                break;
            case MessageEvent.READ_DATA_HEART_DISCONNECT:
                Log.e("Evaluate", "onMessageEvent: " + "心跳停了");
                if (Global.ConnectedAddress != null) {
                    Log.e("Evaluate", "onMessageEvent: " + "关闭上次连接，开始新的连接");
                    isUnConnected = true;
                    isHeartConnected = false;
                    bleUnconnectedTimer();
                    clearTimerTask();
                    Global.isStartReadData = false;
                    BluetoothController.getInstance().closeGatt(Global.ConnectedAddress);
                    BluetoothController.getInstance().connect(Global.ConnectedAddress);
//                    BluetoothController.getInstance().clearConnectedStatus();
                }
                break;
            case MessageEvent.ACTION_TRAIN_TIPS:
                SpeechUtil.getInstance(this).speak("太用力了");
                noteCenterIndicator("太用力了!!");
                break;
            case MessageEvent.TASK_FINISH:
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                EvaluateManager.getInstance().insert((int) result, vasValue, firstValue, secondValue, thirdValue, fourthValue, fifthValue);
                startTargetActivity(MainActivity.class,true);
                break;
            case MessageEvent.TASK_FINISH_ERROR:
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                RxDialogSureCancel dialog = new RxDialogSureCancel(this);
                dialog.setTitle("计划提示");
                dialog.setContent("1.您的评估日期已经超出你的训练周期，请联系医生手动生成训练计划。");
                dialog.setCancel("");
                dialog.setSure("退出");
                dialog.setSureListener(v -> {
                    dialog.dismiss();
                    startTargetActivity(MainActivity.class, true);
                });
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
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
    private void bleUnconnectedDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (isDisconnectedDialogShow)
            return;
        Toast.makeText(this, getString(R.string.ble_disconnect), Toast.LENGTH_SHORT).show();
        RxDialogSureCancel dialog = new RxDialogSureCancel(this);
        dialog.setTitle("蓝牙鞋连接失败!");
        dialog.setContent("1.请检查蓝牙鞋是否打开。\n2.检查蓝牙鞋是否在附近。");
        dialog.setCancel("");
        dialog.setSure("退出");
        dialog.setSureListener(v -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            dialog.dismiss();
            startTargetActivity(MainActivity.class, true);
        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
            BluetoothController.getInstance().connect(Global.TempConnectedAddress);
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        isDisconnectedDialogShow = true;
    }

    private void refreshView(Float valueOf) {
        speedview.speedTo(valueOf, 300);
//        if (valueOf < weightValue) {
//            return;
//        }
        if (evaluateCount >= evaluateMaxCount)
            return;
        if (tempValue < valueOf) {
            tempValue = valueOf;
        }
        floatList.add(valueOf);
        weightValue = Collections.max(floatList).intValue();
//        tvResult.setText(MessageFormat.format("{0}kg", (int) weightValue));
        if (evaluateCount == 0) {
            tvFirstValue.setText(MessageFormat.format("第一次：{0}kg", weightValue));
        }
        if (evaluateCount == 1) {
            tvSecondValue.setText(MessageFormat.format("第二次：{0}kg", weightValue));
            result = 0;

        }
        if (evaluateCount == 2) {
            tvThirdValue.setText(MessageFormat.format("第三次：{0}kg", weightValue));
            result = 0;
        }
        if (evaluateCount == 3) {
            tvFourthValue.setText(MessageFormat.format("第四次：{0}kg", weightValue));
            result = 0;
        }
        if (evaluateCount == 4) {
            tvFifthValue.setText(MessageFormat.format("第五次：{0}kg", weightValue));
            result = 0;
        }

        if (tempValue > valueOf && valueOf < 2 && weightValue > 1) {
            evaluateCount++;
            tempValue = valueOf;
            if (evaluateCount == 1) {
                firstValue = weightValue;
                tvFirstValue.setText(MessageFormat.format("第一次：{0}kg", firstValue));
                tempValue = 0;
                floatList.clear();
                sendDelay("第二次");
                //SpeechUtil.getInstance(this).speak("请开始第二次踩踏");
                tvEvaluateTips1.setText("请开始第二次踩踏");
            }
            if (evaluateCount == 2) {
                secondValue = weightValue;
                tvSecondValue.setText(MessageFormat.format("第二次：{0}kg", secondValue));
                tempValue = 0;
                floatList.clear();
                sendDelay("第三次");
                //SpeechUtil.getInstance(this).speak("请开始第三次踩踏");
                tvEvaluateTips1.setText("请开始第三次踩踏");
            }
            if (evaluateCount == 3) {
                thirdValue = weightValue;
                tvThirdValue.setText(MessageFormat.format("第三次：{0}kg", thirdValue));
                tempValue = 0;
                minValueList.clear();
                floatList.clear();
                sendDelay("第四次");
                //SpeechUtil.getInstance(this).speak("请开始第四次踩踏");
                tvEvaluateTips1.setText("请开始第四次踩踏");
            }
            if (evaluateCount == 4) {
                fourthValue = weightValue;
                tvFourthValue.setText(MessageFormat.format("第四次：{0}kg", fourthValue));
                tempValue = 0;
                minValueList.clear();
                floatList.clear();
                sendDelay("第五次");
                //SpeechUtil.getInstance(this).speak("请开始第五次踩踏");
                tvEvaluateTips1.setText("请开始第五次踩踏");
            }
            if (evaluateCount == 5) {
                fifthValue = weightValue;
                tvFifthValue.setText(MessageFormat.format("第五次：{0}kg", fifthValue));
                result = getAverageValue();
                tvResult.setText(MessageFormat.format("{0}kg", (int) result));
                tempValue = 0;
                floatList.clear();
                SpeechUtil.getInstance(this).speak("你已评估完成");
                tvEvaluateTips1.setText("你已评估完成");
                evaluateFinish = true;
            }
        }
    }
    float currentMaxweight;
    boolean isEffect = false;
    boolean isWarning = false;
    boolean isDidNotMakeIt = false;//判断最大值是否在黄色范围，便于记录未达标的数据
    private float minWeight = 0;
    private float maxWeight = 0;
    private int count = 0;
    private int notcount = 0;
    private int warningcount = 0;
    private void testRefreshView(Float value) {
        if (value < 0.5f) {
            value = 0.0f;
            speedview1.speedTo(value, 300);
        } else {
            speedview1.speedTo(value, 300);
        }
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
        } else if (currentMaxweight < minWeight && currentMaxweight > saveResult * 0.3) {
            if (!isWarning && !isEffect) {
                isDidNotMakeIt = true;
            }
        }

        if (isDidNotMakeIt && !isWarning && !isEffect) {
            noteCenterIndicator("加油");
        }

        if (value <= saveResult * 0.2 && (isDidNotMakeIt || isEffect || isWarning)) {
            currentMaxweight = 0;
            if (isWarning) {
                warningcount++;
                tvWarningTime.setText(MessageFormat.format("{0}", warningcount));
                if ((warningcount + count)%5 == 0){
                    tvEvaluateTips1.setText(getString(R.string.text_test_tips1));
                    SpeechUtil.getInstance(this).speak(getString(R.string.text_test_tips1));
                }
            }
            if (isEffect) {
                SpeechUtil.getInstance(this).speak("太棒了，完成一次");
                count++;
                tvEffectiveTime.setText(MessageFormat.format("{0}", count));
                if ((warningcount + count)%5 == 0){
                    tvEvaluateTips1.setText(getString(R.string.text_test_tips1));
                    SpeechUtil.getInstance(this).speak(getString(R.string.text_test_tips1));
                }
            }
            if (isDidNotMakeIt){
                notcount++;
            }
            isEffect = false;
            isWarning = false;
            isDidNotMakeIt = false;
        }


    }
    private List<Float> valueList = new ArrayList<>();
    private List<Float> minValueList = new ArrayList<>();
    private int indexMax = 5;

    private float tempValue = 0;
    private int evaluateCount = 0;
    private float firstValue = 0;
    private float secondValue = 0;
    private float thirdValue = 0;
    private float fourthValue = 0;
    private float fifthValue = 0;
    private float result = 0;

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                SpeechUtil.getInstance(EvaluateActivity.this).speak(msg.obj + "");
            }
        }
    };


    private void sendDelay(String message1) {
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 0;
                message.obj = message1;
                myHandler.sendMessage(message);

            }
        }, 1000);
    }

    private void calculMinValue(float value) {
        if (tempValue < value) {
            tempValue = value;
        }
        speedview.speedTo(value, 300);
        if (valueList.size() < indexMax) {
            valueList.add(value);
        } else if (valueList.size() == indexMax) {
            minValueList.add(Collections.min(valueList));
            valueList.clear();
        }
        if (minValueList.size() > 0) {
            weightValue = Collections.max(minValueList).intValue();
//            tvResult.setText(MessageFormat.format("{0}kg", weightValue));
            if (evaluateCount == 0) {
                tvFirstValue.setText(MessageFormat.format("第一次：{0}kg", weightValue));
            }
            if (evaluateCount == 1) {
                tvSecondValue.setText(MessageFormat.format("第二次：{0}kg", weightValue));
                result = 0;
            }
            if (evaluateCount == 2) {
                tvThirdValue.setText(MessageFormat.format("第三次：{0}kg", weightValue));
                result = 0;
            }
            if (evaluateCount == 3) {
                tvFourthValue.setText(MessageFormat.format("第四次：{0}kg", weightValue));
                result = 0;
            }
            if (evaluateCount == 4) {
                tvFifthValue.setText(MessageFormat.format("第五次：{0}kg", weightValue));
                result = 0;
            }
            if (tempValue > value && value < 1.5 && weightValue > 1) {
                evaluateCount++;
                tempValue = value;
                if (evaluateCount == 1) {
                    firstValue = weightValue;
                    tvFirstValue.setText(MessageFormat.format("第一次：{0}kg", firstValue));
                    tempValue = 0;
                    minValueList.clear();


                    sendDelay("第二次");
                    //SpeechUtil.getInstance(this).speak("请开始第二次踩踏");
                    tvEvaluateTips1.setText("请开始第二次踩踏");
                }
                if (evaluateCount == 2) {
                    secondValue = weightValue;
                    tvSecondValue.setText(MessageFormat.format("第二次：{0}kg", secondValue));
                    tempValue = 0;
                    minValueList.clear();

                    sendDelay("第三次");
                    //SpeechUtil.getInstance(this).speak("请开始第三次踩踏");
                    tvEvaluateTips1.setText("请开始第三次踩踏");
                }
                if (evaluateCount == 3) {
                    thirdValue = weightValue;
                    tvThirdValue.setText(MessageFormat.format("第三次：{0}kg", thirdValue));
                    tempValue = 0;
                    minValueList.clear();
                    sendDelay("第四次");
                    //   SpeechUtil.getInstance(this).speak("请开始第四次踩踏");
                    tvEvaluateTips1.setText("请开始第四次踩踏");
                }
                if (evaluateCount == 4) {
                    fourthValue = weightValue;
                    tvFourthValue.setText(MessageFormat.format("第四次：{0}kg", fourthValue));
                    tempValue = 0;
                    minValueList.clear();
                    sendDelay("第五次");
                    // SpeechUtil.getInstance(this).speak("请开始第五次踩踏");
                    tvEvaluateTips1.setText("请开始第五次踩踏");
                }
                if (evaluateCount == 5) {
                    fifthValue = weightValue;
                    tvFifthValue.setText(MessageFormat.format("第五次：{0}kg", fifthValue));
                    result = getAverageValue();
                    tvResult.setText(MessageFormat.format("{0}kg", (int) result));
                    tempValue = 0;
                    minValueList.clear();
                    SpeechUtil.getInstance(this).speak("你已评估完成");
                    tvEvaluateTips1.setText("你已评估完成");
                    evaluateFinish = true;
                }
            }
        }

    }


    private void deleteMax(float value) {
        for (int i = 0; i < minValueList.size(); i++) {
            if (minValueList.get(i) >= value) {
                minValueList.set(i, value);
            }
        }
    }

    private void clearView() {
        myHandler.removeMessages(0);
        tvFirstValue.setText("");
        tvSecondValue.setText("");
        tvThirdValue.setText("");
        tvFourthValue.setText("");
        tvFifthValue.setText("");
        tvEvaluateTips1.setText("");
        tvResult.setText("0kg");
        tempValue = 0;
        evaluateCount = 0;
        firstValue = 0;
        secondValue = 0;
        thirdValue = 0;
        fourthValue = 0;
        fifthValue = 0;
        result = 0;
        floatList.clear();
        minValueList.clear();
        evaluateFinish = false;
        String voiceText = "请开始第一次踩踏";
        SpeechUtil.getInstance(EvaluateActivity.this).speak(voiceText);
        tvEvaluateTips1.setText(voiceText);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_evaluate;
    }

    @Override
    protected void onResume() {
        EventBus.getDefault().register(this);
        initData();
        initView();
        BluetoothController.getInstance().startDealThread();
//        Global.isReconnectBle = true;
        isHeartConnected = Global.isConnected;
        if (!Global.isConnected) {
            EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_GATT_DISCONNECTED));
        }
        startTask();
        super.onResume();
    }

    @OnClick({R.id.iv_back, R.id.tv_study_setting, R.id.img_reduce, R.id.img_increase, R.id.btn_clear, R.id.btn_commit, R.id.tv_remote_calibration,R.id.tv_stop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
//                if (evaluateFinish && DateFormatUtil.avoidFastClick2(2000)) {
//                    if (Global.USER_MODE) {
//                        UserBean userBean = SPHelper.getUser();
//                        if (MyUtil.isNoPlanUser()) {
//                            SPHelper.saveEvaluateDate(SPHelper.getUserId(), System.currentTimeMillis());
//                            SPHelper.saveNoPlanUserEvaluateWeight(userBean.getUserId(), result);
//                        } else if (MyUtil.isGeneratePlan(userBean)) {
//                            MyUtil.insertTemplate((int) saveResult);
//                        }
//                        SPHelper.saveEvaluateStatus(false);
//                        EvaluateEntity evaluateEntity = EvaluateManager.getInstance().insert((int) result, vasValue, firstValue, secondValue, thirdValue, fourthValue, fifthValue);
//                    }
//                }
                if (rlRootV1.getVisibility() == View.VISIBLE){
                    generatePlanDialog();
                }else {

                    startTargetActivity(MainActivity.class, true);
                }
                break;
            case R.id.tv_study_setting:
                //设置在五秒内点击七次版本号会显示标定功能
                ClickCount++;
                if (ClickCount == 1) {
                    ClickTime = System.currentTimeMillis();
                } else if (ClickCount >= 7 && (System.currentTimeMillis() - ClickTime < 5000)) {
                    tvRemoteCalibration.setVisibility(View.VISIBLE);
                    edRemoteCalibration.setVisibility(View.VISIBLE);
                    ClickTime = 0;
                    ClickCount = 0;
                } else if (ClickCount >= 7 && (System.currentTimeMillis() - ClickTime > 5000)) {
                    ClickTime = 0;
                    ClickCount = 0;
                }
                break;
            case R.id.img_reduce:
                if (evaluateCount >= 3) {
                    if (result > 0) {
                        result = result - 1;
                        deleteMax(result);
//                    DecimalFormat decimalFormat = new DecimalFormat("0.0");
//                    tvResult.setText(MessageFormat.format("{0}kg", decimalFormat.format(weightValue)));
                        tvResult.setText(MessageFormat.format("{0}kg", (int) result));
                    }
                }
                break;
            case R.id.img_increase:
                if (evaluateCount >= 3) {
                    if (result < 100) {
                        result = result + 1;
                        if (selectPosition == 1) {
                            minValueList.add(result);
                        }
                        tvResult.setText(MessageFormat.format("{0}kg", (int) result));
                    }
                }
                break;
            case R.id.btn_clear:
                weightValue = 0;
                result = 0;
                floatList.clear();
                valueList.clear();
                minValueList.clear();
                clearView();
                tvResult.setText(MessageFormat.format("{0}kg", (int) weightValue));
                break;
            case R.id.btn_commit:
                saveResult = result;
                if (saveResult < 1) {
                    Toast.makeText(this, "评估值不能为0", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isValidValue()) {
                    if ((int)result > Float.parseFloat(SPHelper.getUser().getWeight())){
                        evaluateCommitDialog("您评估的值超过你填写的体重，请重测","");
                        return;
                    }
                    if (MyUtil.isGeneratePlan(SPHelper.getUser())){
                        initSpeedView();
                    }else if (DateFormatUtil.avoidFastClick2(2000)){
//                        initSpeedView();
                        generateEvaluateResult(0,null,null);
                    }
                } else {
                    if ((int)result > Float.parseFloat(SPHelper.getUser().getWeight())){
                        evaluateCommitDialog("您评估的值超过你填写的体重，请重测","");
                    }else {
                        evaluateCommitDialog("评估偏差比较大，是否重新评估","下一步");
                    }
                }
                break;
            case R.id.tv_remote_calibration:
                String message;
                if (TextUtils.isEmpty(edRemoteCalibration.getText().toString())) {
                    ToastUtils.showShort("请输入标定值");
                    return;
                } else {
                    message = "set" + edRemoteCalibration.getText().toString();
                }
                BluetoothController.getInstance().writeRXCharacteristic(Global.ConnectedAddress, message.getBytes(StandardCharsets.UTF_8));
                Toast.makeText(this, "命令已发送", Toast.LENGTH_SHORT).show();
                tvRemoteCalibration.setVisibility(View.GONE);
                edRemoteCalibration.setVisibility(View.GONE);
                break;
            case R.id.tv_stop:
                if (DateFormatUtil.avoidFastClick(2000)){
                    generateEvaluateResult(0,null,null);
//                    generatePlanDialog();
                }
                break;
        }
    }
    private void generatePlanDialog(){
        String date2String = DateFormatUtil.getDate2String(DateFormatUtil.getZeroClockTimestamp(System.currentTimeMillis()),"yyyy-MM-dd");
        String startLoad = (int)saveResult+"";
        String endWeight = SPHelper.getUser().getWeight();
        if (SPHelper.getUser().getDiagnosis().equals("髋臼周围截骨术")){
            endWeight =(int)Float.parseFloat(SPHelper.getUser().getWeight())/2 + "";
        }
        android.util.Log.e("evaluate", "generatePlanDialog: " + date2String );
        RxPlanConfirmDialog dataEditDialog = new RxPlanConfirmDialog(this,date2String,startLoad,endWeight);
        dataEditDialog.setSureListener(v -> {
            String startDate;
            if (dataEditDialog.startTime.length() > "yyyy-MM-dd".length()){
                startDate = dataEditDialog.startTime;
            }else {
                startDate = dataEditDialog.startTime + " 00:00:00";
            }
            String startWeight = dataEditDialog.getStartLoad();
            String endLoad = dataEditDialog.getEndLoad();
            if (TextUtils.isEmpty(startWeight)){
                ToastUtils.showShort("开始负重不能为空");
                return;
            }
            if (Integer.parseInt(endLoad) <=0){
                ToastUtils.showShort("开始负重不能小于0");
                return;
            }
            if (TextUtils.isEmpty(endLoad)){
                ToastUtils.showShort("结束负重不能为空");
                return;
            }
            if (Integer.parseInt(endLoad) <=0){
                ToastUtils.showShort("结束负重不能小于0");
                return;
            }
            dataEditDialog.dismiss();
            android.util.Log.e("evaluate", "generatePlanDialog: " + startDate );
            generateEvaluateResult(DateFormatUtil.getString2Date(startDate,"yyyy-MM-dd HH:mm:ss"),startWeight,endLoad);
        });
        dataEditDialog.setOnCancelListener(dialog -> startTargetActivity(MainActivity.class,true));
        dataEditDialog.show();

    }
    private void evaluateCommitDialog(String content,String cancel){
        RxDialogSureCancel dialog = new RxDialogSureCancel(this);
        dialog.setContent(content);
        dialog.setCancel(cancel);
        dialog.setSure("重测");
        dialog.setSureListener(v -> {
            dialog.dismiss();
            weightValue = 0;
            result = 0;
            floatList.clear();
            valueList.clear();
            minValueList.clear();
            clearView();
            tvResult.setText(MessageFormat.format("{0}kg", (int) weightValue));
        });
        dialog.setCancelListener(v -> {
            floatList.clear();
            valueList.clear();
            minValueList.clear();
            weightValue = 0;
            dialog.dismiss();
            if (MyUtil.isGeneratePlan( SPHelper.getUser())){
                initSpeedView();
            }else {
//                            initSpeedView();
                generateEvaluateResult(0,null,null);
            }
        });
        dialog.show();
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void initSpeedView(){
        tvEvaluateTips1.setText(getString(R.string.text_test_tips));
        SpeechUtil.getInstance(this).speak(getString(R.string.text_test_tips));
        minWeight = saveResult * 0.8f;
        maxWeight = saveResult * 1.2f;
        llFunctionSetting.setVisibility(View.GONE);
        llEvaluate1.setVisibility(View.GONE);
        tvEvaluateTips.setVisibility(View.GONE);
        rlRootV1.setVisibility(View.VISIBLE);
        speedview1.setIndicator(Indicator.Indicators.KiteIndicator);
        speedview1.setIndicatorColor(Color.WHITE);
        if (!Global.isChangSha) {
            speedview1.setLowSpeedColor(getColor(R.color.speed_yellow));
            speedview1.setMediumSpeedColor(getColor(R.color.speed_green));
        }
        speedview1.setLowSpeedPercent(40);
        speedview1.setMediumSpeedPercent(60);
        speedview1.setWithTremble(false);
        speedview1.setMaxSpeed(saveResult * 2);
        speedview1.setTickNumber(11);
        speedview1.setTickPadding(36);
    }

    private boolean isValidValue() {
        List<Float> valueList = new ArrayList<>();
        valueList.add(firstValue);
        valueList.add(secondValue);
        valueList.add(thirdValue);
        valueList.add(fourthValue);
        valueList.add(fifthValue);
        float maxValue1 = Collections.max(valueList);
        valueList.remove(maxValue1);
        float maxValue2 = Collections.max(valueList);
        valueList.remove(maxValue2);
        float maxValue3 = Collections.max(valueList);
        List<Float> valueList1 = new ArrayList<>();
        valueList1.add(maxValue1);
        valueList1.add(maxValue2);
        valueList1.add(maxValue3);
        float minValue = Collections.min(valueList1);
        float maxValue = Collections.max(valueList1);
        return !((maxValue - minValue) / maxValue > threshold);
    }

    private float getAverageValue() {
        List<Float> valueList = new ArrayList<>();
        valueList.add(firstValue);
        valueList.add(secondValue);
        valueList.add(thirdValue);
        valueList.add(fourthValue);
        valueList.add(fifthValue);
        float maxValue1 = Collections.max(valueList);
        valueList.remove(maxValue1);
        float maxValue2 = Collections.max(valueList);
        valueList.remove(maxValue2);
        float maxValue3 = Collections.max(valueList);
        return (maxValue1 + maxValue2 + maxValue3) / 3;

    }

    private void generateEvaluateResult(long planStartTime,String startLoad,String planFinishLoad) {
        UserBean userBean = SPHelper.getUser();
        userBean.setEvaluateWeight(saveResult);
        SPHelper.saveUser(userBean);
        if (Global.USER_MODE) {
            if (MyUtil.isNoPlanUser()) {
                SPHelper.saveEvaluateDate(SPHelper.getUserId(), System.currentTimeMillis());
                SPHelper.saveNoPlanUserEvaluateWeight(userBean.getUserId(), saveResult);
            } else if (MyUtil.isGeneratePlan(userBean) && planStartTime>0 && startLoad != null && planFinishLoad != null) {
                Global.MinTrainStep = indexMax + count + warningcount;
                userBean.setStr("");
                UserManager.getInstance().insert(userBean, 1);
                showWaiting("计划生成","计划生成中，请稍等…");
                AsyncThreadTask.execute(() -> {
                    int status = MyUtil.insertTemplate(Integer.parseInt(startLoad),planStartTime,planFinishLoad);
                    if (status == Integer.MAX_VALUE){
                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.TASK_FINISH_ERROR));
                    }else {
                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.TASK_FINISH));
                    }
                });
//                EvaluateManager.getInstance().insert((int) result, vasValue, firstValue, secondValue, thirdValue, fourthValue, fifthValue);
//                startTargetActivity(MainActivity.class,true);
//                userBean.setStr("");
//                UserManager.getInstance().insert(userBean, 1);
                return;
            } else {
//                Global.MinTrainStep = indexMax + count + warningcount;
//                MyUtil.insertTemplate((int) saveResult);
            }
            MyUtil.updateProductStatus(2);
            SPHelper.saveEvaluateStatus(false);
        }
        userBean.setStr("");
        UserManager.getInstance().insert(userBean, 1);
        if (Global.USER_MODE) {
            Bundle bundle = new Bundle();
            EvaluateEntity evaluateEntity = EvaluateManager.getInstance().insert((int) result, vasValue, firstValue, secondValue, thirdValue, fourthValue, fifthValue);
            bundle.putParcelable("EvaluateEntity", evaluateEntity);
            bundle.putInt("class_type", 0);
            startTargetActivity(bundle, PrintContentActivity.class, true);
        } else {
            RxDialogSureCancel dialog = new RxDialogSureCancel(this);
            dialog.setContent("是否进入训练");
            dialog.setCancel("回到主界面");
            dialog.setSure("进入训练");
            dialog.setSureListener(v -> {
                dialog.dismiss();
                if (Global.isConnected) {
                    startTargetActivity(TrainParamActivity.class, true);
                } else {
                    startTargetActivity(ConnectDeviceActivity.class, true);
                }
            });
            dialog.setCancelListener(v -> {
                floatList.clear();
                valueList.clear();
                minValueList.clear();
                weightValue = 0;
                dialog.dismiss();
                startTargetActivity(MainActivity.class, true);
            });
            dialog.show();
        }
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
                        Global.isStartReadData = true;
                        String message = ":1A" + DataTransformUtil.toHexString((byte) b) + "00" + DataTransformUtil.toHexString((byte) d);
                        BluetoothController.getInstance().writeRXCharacteristic(Global.ConnectedAddress, message.getBytes(StandardCharsets.UTF_8));
                    }

                }
            };
            mTimer.schedule(mTimerTask, 0, 1000);
        }
    }

    private void clearTimerTask() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimerTask.cancel();
            mTimer = null;
            mTimerTask = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Global. isReconnectBle = false;
        EventBus.getDefault().unregister(this);
        clearTimerTask();
        Global.isStartReadData = false;
        Global.ReadCount = 0;
        BluetoothController.getInstance().stopDealThread();
        SpeechUtil.getInstance(this).stopSpeak();
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
//        Global.isReconnectBle = false;
//        EventBus.getDefault().unregister(this);
//        clearTimerTask();
//        Global.isStartReadData = false;
//        Global.ReadCount = 0;
//        BluetoothController.getInstance().stopDealThread();
        super.onDestroy();
    }
}
