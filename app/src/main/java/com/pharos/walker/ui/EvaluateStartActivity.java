package com.pharos.walker.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.pharos.walker.R;
import com.pharos.walker.beans.Battery;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.SpeechUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EvaluateStartActivity extends BaseActivity {
    @BindView(R.id.tv_next)
    TextView tv_next;
    @BindView(R.id.tv_evaluate_caption)
    TextView tvEvaluateCaption;
    @BindView(R.id.tv_evaluate_caption_content)
    TextView tvEvaluateCaptionContent;
    @BindView(R.id.tv_pain_caption)
    TextView tvPainCaption;
    @BindView(R.id.tv_pain_caption_content)
    TextView tvPainCaptionContent;

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        SpeechUtil.getInstance(this).speak(tvEvaluateCaption.getText().toString() + "：" + tvEvaluateCaptionContent.getText().toString(), new SynthesizerListener() {
            @Override
            public void onSpeakBegin() {

            }

            @Override
            public void onBufferProgress(int i, int i1, int i2, String s) {

            }

            @Override
            public void onSpeakPaused() {

            }

            @Override
            public void onSpeakResumed() {

            }

            @Override
            public void onSpeakProgress(int i, int i1, int i2) {

            }

            @Override
            public void onCompleted(SpeechError speechError) {
                SpeechUtil.getInstance(EvaluateStartActivity.this).speak(tvPainCaption.getText().toString() + tvPainCaptionContent.getText().toString());
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_evalute_start;
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
            default:
                break;
        }
    }

    @OnClick({R.id.tv_next, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_next:
                if(DateFormatUtil.avoidFastClick(2000)){
                    Bundle bundle = getIntent().getExtras();
                    startTargetActivity(bundle, StartActivity.class, true);
                }
                break;
            case R.id.iv_back:
                finish();
                break;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        SpeechUtil.getInstance(this).stopSpeak();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
