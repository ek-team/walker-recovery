package com.pharos.walker.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.pharos.walker.beans.Battery;
import com.pharos.walker.R;
import com.pharos.walker.bluetooth.BluetoothController;
import com.pharos.walker.constants.Api;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.database.ActivationCodeManager;
import com.pharos.walker.utils.DataTransformUtil;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.OkHttpUtils;
import com.pharos.walker.utils.SpeechUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

/**
 * Created by zhanglun on 2021/4/14
 * Describe:
 */
public class StartActivity extends BaseActivity {
    @BindView(R.id.tv_warning_left)
    TextView tvWarningLeft;
    @BindView(R.id.tv_warning_right)
    TextView tvWarningRight;
    @BindView(R.id.btn_start)
    TextView btnStart;
    private TimerTask timerTask = null;
    private Timer timer = null;
    private int clo;
    private int count;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Timer mTimer1;
    private TimerTask mTimerTask1;
    private int timeNext = 0;//开始倒计时
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        BluetoothController.getInstance().setLastTimestamp(0);
        initView();
        startAnimTimerTask();
    }

    private void initView() {
        MyUtil.updateProductStatus(1);
        Animation mAnimation = AnimationUtils.loadAnimation(this, R.anim.start_anim);
        btnStart.setAnimation(mAnimation);
        mAnimation.start();
        btnStart.setText("开始");
        GetTimer(5);
    }

    private void startTask() {
        if (mTimer == null && mTimerTask == null) {
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    int a = 0x1A;
                    int b = 0x04 | 0x10;
                    int c = 0x00;
                    int d = 0xFF - (a + b + c) + 1;
                    String message = ":1A" + DataTransformUtil.toHexString((byte) b) + "00" + DataTransformUtil.toHexString((byte) d);
                    BluetoothController.getInstance().writeRXCharacteristic(Global.ConnectedAddress, message.getBytes(StandardCharsets.UTF_8));
                }
            };
            mTimer.schedule(mTimerTask, 0, 1000);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getAction()) {
            case MessageEvent.ACTION_GATT_CONNECTED:
                setPoint(true);
                break;
            case MessageEvent.UPDATE_TOP_TIME:
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
            case MessageEvent.START_TIME:
                int time = (int) event.getData();
                if (time<=3){
                    btnStart.setTextSize(80);
                    btnStart.setText(time+"");
                }
                break;
            case MessageEvent.END_TIME:
                goNextPage();
                break;


            default:
                break;
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_start;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTask();
        BluetoothController.getInstance().startDealThread();
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearTimerTask();
        BluetoothController.getInstance().stopDealThread();
    }

    private void startAnimTimerTask() {
        if (timer == null && timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (clo == 0) {
                                clo = 1;
                                tvWarningLeft.setVisibility(View.VISIBLE);
                                tvWarningRight.setVisibility(View.VISIBLE);
                            } else if (clo == 1) {
                                clo = 0;
                                tvWarningLeft.setVisibility(View.INVISIBLE);
                                tvWarningRight.setVisibility(View.INVISIBLE);
                                count++;
                                if (count >= 10) {
                                    if (timer != null) {
                                        timer.cancel();
                                        timer = null;
                                        tvWarningLeft.setVisibility(View.VISIBLE);
                                        tvWarningRight.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    });
                }
            };
            timer = new Timer();
            timer.schedule(timerTask, 1, 600);
        }
    }


    /**
     * 倒计时
     *
     * @param time
     */

    private void GetTimer(int time) {
        if (mTimer1 == null && mTimerTask1 == null) {
            mTimer1 = new Timer();
            mTimerTask1 = new TimerTask() {
                @Override
                public void run() {
                    timeNext++;
                    if ((time >= timeNext + 1)) {
                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.START_TIME, time - timeNext));
                    } else {
                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.END_TIME));
                    }

                }
            };
            mTimer1.schedule(mTimerTask1, 0, 1000);
        }
    }

    private void goNextPage(){
        BluetoothController.getInstance().sendReadDeviceInfoCmd();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getInt(AppKeyManager.EXTRA_CONNECT_MODE, 0) == Global.ConnectEvaluateMode) {
            startTargetActivity(bundle, EvaluateActivity.class, true);
        } else if (bundle != null && bundle.getInt(AppKeyManager.EXTRA_CONNECT_MODE, 0) == Global.ConnectMainMode) {
            startTargetActivity(bundle, TrainingActivity.class, true);
            SpeechUtil.getInstance(this).speak(getString(R.string.start_train));
        }
    }


    @OnClick({R.id.tv_battery, R.id.iv_notification, R.id.iv_voice, R.id.iv_back, R.id.btn_start})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_battery:
                break;
            case R.id.iv_notification:
                break;
            case R.id.iv_voice:
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_start:
//                if (DateFormatUtil.avoidFastClick(2000)) {
//                    goNextPage();
//                }
                break;
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (timerTask != null && timer != null) {
            timer.cancel();
            timerTask.cancel();
            timerTask = null;
            timer = null;
        }
    }
}
