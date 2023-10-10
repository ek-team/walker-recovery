package com.pharos.walker.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.ListPopupWindow;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.exoplayer2.ui.PlayerView;
import com.pharos.walker.MainActivity;
import com.pharos.walker.R;
import com.pharos.walker.beans.OriginalSubPlanEntity;
import com.pharos.walker.beans.PlanEntity;
import com.pharos.walker.beans.SubPlanEntity;
import com.pharos.walker.beans.TrainMessageBean;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.constants.Global;
import com.pharos.walker.customview.ChartTodayMarkerView;
import com.pharos.walker.customview.popupdialog.PopupSheet;
import com.pharos.walker.customview.popupdialog.PopupSheetCallback;
import com.pharos.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharos.walker.database.OriginalSubPlanManager;
import com.pharos.walker.database.SubPlanManager;
import com.pharos.walker.database.TrainPlanManager;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.DimensUtil;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.SPUtils;
import com.pharos.walker.utils.SnowflakeIdUtil;
import com.pharos.walker.utils.ToastUtils;
import com.pharos.walker.utils.VideoPlayUtil;
import com.tencent.mars.xlog.Log;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/4/15
 * Describe:
 */
public class TrainParamActivity extends BaseActivity {
    @BindView(R.id.player_view)
    PlayerView playerView;
    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_tread_num)
    TextView tvTreadNum;
    @BindView(R.id.layout_tread_num)
    RelativeLayout layoutTreadNum;
    @BindView(R.id.tv_weight)
    TextView tvWeight;
    @BindView(R.id.layout_weight)
    RelativeLayout layoutWeight;
    @BindView(R.id.tv_music)
    TextView tvMusic;
    @BindView(R.id.layout_music)
    RelativeLayout layoutMusic;
    @BindView(R.id.tv_previous)
    TextView tvPrevious;
    @BindView(R.id.tv_start)
    TextView tvStart;
    @BindView(R.id.tv_overview_plan)
    TextView tvOverviewPlan;
    @BindView(R.id.layout_main)
    RelativeLayout layoutMain;
    @BindView(R.id.layout_count_of_time)
    RelativeLayout layoutCountOfTime;
    @BindView(R.id.tv_count_of_time)
    TextView tvCountOfTime;
    @BindView(R.id.layout_times_of_day)
    RelativeLayout layoutTimesOfDay;
    @BindView(R.id.tv_times_of_day)
    TextView tvTimesOfDay;
    @BindView(R.id.tv_title_train_time)
    TextView tvTitleTrainTime;
    @BindView(R.id.seekbar_volume)
    SeekBar seekbarVolume;
    @BindView(R.id.chart)
    CombinedChart chart;
    private String fileName = "file:///android_asset/video_user_help_1.mp4";
    private List<String> musicData;
    private PopupSheet popupSheet;
    private int weight = 30;
    private int planid;
    private int level;
    private int trainTime = 5;
    private int trainTimeSelect = 5;
    private int musicPosition = 0;
    private int timesOfDay = 3;
    private String selectMusic = "卡农";
    private AudioManager mAudioManager;
    private int countOfTime = 10;
    private int trainCountOfTimeSelect = countOfTime;
    private SubPlanEntity subPlanEntity = null;
    private int selectWeight = weight;
    private int axisFontSize = 16;
    private List<SubPlanEntity> subPlanEntityList;
    private List<OriginalSubPlanEntity> originalSubPlanEntityList;



    private int dataSize = 0;
    private int dataPosLenX = 0;
    private int dataPosLenY = 0;
    private int ZX = 630; //屏幕尺寸
    private int ZX_START = 50;
    private int ZY = 480; //屏幕尺寸
    private int pos = 0; //第几个点
    private int MaxWeight = 100; //体重
    private float downX = 0; //点击时候的X
    private float downY = 0; //点击时候的Y
    private float moveUpX = 0; //移动时候的X
    private float moveUpY = 0; //移动时候的Y
    boolean isMoveChange = false;

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
    }

    private void initData() {
        if (Global.USER_MODE) {
            subPlanEntityList = SubPlanManager.getInstance().loadDataByUserId(SPHelper.getUserId());
            originalSubPlanEntityList = OriginalSubPlanManager.getInstance().loadDataByUserId(SPHelper.getUserId());
            setChart();
            setYAxis();
            initLegend();
            initChart(subPlanEntityList);
            if (MyUtil.isNoPlanUser() && TrainPlanManager.getInstance().isPlanEmpty(SPHelper.getUserId()) ){
                weight = (int) SPHelper.getNoPlanUserEvaluateWeight(SPHelper.getUserId());
                countOfTime = trainCountOfTimeSelect = 10;//没有计划的用户默认训练步数10步
            }else {
                subPlanEntity = SubPlanManager.getInstance().getThisDayLoadEntity(SPHelper.getUserId());
//                subPlanEntity = SubPlanManager.getInstance().getThisWeekLoadEntity(SPHelper.getUserId());
                TrainMessageBean trainMessageBean = TrainPlanManager.getInstance().refreshPlanStatus(SPHelper.getUserId());
                if (subPlanEntity != null){
                    weight = subPlanEntity.getLoad();
                }else if (trainMessageBean != null && trainMessageBean.getPlanStatus()<=0){
                    weight = (int) SPHelper.getUserEvaluateWeight();
                }else if ((System.currentTimeMillis() > DateFormatUtil.getString2Date(SPHelper.getUser().getDate()))){
                    weight = Integer.parseInt(SPHelper.getUser().getWeight());
                }else {
                    weight = (int) SPHelper.getUserEvaluateWeight();
                }
//                if ((int) SPHelper.getUserEvaluateWeight() <= 0){
//                    if (subPlanEntity != null){
//                        weight = subPlanEntity.getLoad();
//                    }else if ((System.currentTimeMillis() > DateFormatUtil.getString2Date(SPHelper.getUser().getDate()))){
//                        weight = Integer.parseInt(SPHelper.getUser().getWeight());
//                    }
//                }else {
//                    weight = (int) SPHelper.getUserEvaluateWeight();
//                    SPHelper.saveUserEvaluateWeight(0);
//                }
                if (subPlanEntity != null && SPHelper.getReleaseVersion() == Global.OrthopedicsVersion){//骨科版从子计划获取训练数据
                    trainTime = subPlanEntity.getTrainTime();
                    countOfTime = subPlanEntity.getTrainStep();
                }else {
                    if (trainMessageBean != null && trainMessageBean.getPlanStatus() > 0){
                        trainTime = trainMessageBean.getTrainTime();
                        if (trainTime <= 0){
                            trainTime = 20;
                            countOfTime = Global.MaxTrainStep;
                        }else {
                            countOfTime = trainTime * Global.TrainCountMinute;
                        }
                    }else if (SPHelper.getReleaseVersion() == Global.RecoveryVersion){
                        countOfTime = trainTime * Global.TrainCountMinute;
                    }
                }
                trainTimeSelect = trainTime;
                trainCountOfTimeSelect = countOfTime;
            }

        }else {
            weight = (int) SPHelper.getUserEvaluateWeight();
            trainTimeSelect = trainTime;
            trainCountOfTimeSelect = countOfTime = trainTime * Global.TrainCountMinute;
        }

        selectWeight = weight;
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        initVolume();
    }

    private void initView() {
//        ZX = getWidthPixels()/2;
        String[] musics = getResources().getStringArray(R.array.music_list);
        musicData = Arrays.asList(musics);
        musicPosition = SPHelper.getMusicPosition();
        tvMusic.setText(musicData.get(musicPosition));
        selectMusic = musicData.get(SPHelper.getMusicPosition());
        tvTreadNum.setText(MessageFormat.format("{0}", trainTime));
        tvCountOfTime.setText(MessageFormat.format("{0}", countOfTime));
        tvWeight.setText(MessageFormat.format("{0}", weight));
        tvTimesOfDay.setText(MessageFormat.format("{0}", timesOfDay));
        tvOverviewPlan.setText(MessageFormat.format("总体训练计划：{0}", MyUtil.getPlanSummary()));
        seekbarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 设置音量
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                SPUtils.getInstance().put("system_voice",progress);
            }
        });
        if (SPHelper.getReleaseVersion() == Global.RecoveryVersion){
            layoutTreadNum.setVisibility(View.VISIBLE);
            tvTitleTrainTime.setVisibility(View.VISIBLE);
        }else {
            layoutTreadNum.setVisibility(View.GONE);
            tvTitleTrainTime.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_train_param;
    }

    @Override
    protected void onResume() {
        VideoPlayUtil.getInstance().setVideoPlayer(this, fileName, playerView);
        VideoPlayUtil.getInstance().startPlayer();
        VideoPlayUtil.getInstance().setMute(true);
        initData();
        initView();
        super.onResume();
    }

    @OnClick({R.id.iv_close, R.id.tv_previous, R.id.tv_start, R.id.layout_tread_num, R.id.layout_weight, R.id.layout_music, R.id.btn_go_evaluate,R.id.layout_count_of_time,R.id.layout_times_of_day})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                startTargetActivity(MainActivity.class, true);
                break;
            case R.id.tv_previous:
                if (DateFormatUtil.avoidFastClick(1000)) {
                    startTargetActivity(ConnectDeviceActivity.class, false);
                }
                break;
            case R.id.tv_start:
                if (weight <= 0) {
                    trainTip();
                    ToastUtils.showShort("训练重量必须大于0kg");
                    return;
                }
                if (trainTimeSelect  <= 0 && SPHelper.getReleaseVersion() == Global.RecoveryVersion) {
                    trainTip();
                    ToastUtils.showShort("训练时间必须大于0分钟");
                    return;
                }
//                if (trainCountOfTimeSelect <= 0) {
//                    trainTip();
//                    ToastUtils.showShort("步数必须大于0");
//                    return;
//                }
                if (DateFormatUtil.avoidFastClick2(1000)) {
                    if (trainTimeSelect != trainTime){//保存训练修改的时间
                        List<PlanEntity> planEntityList = TrainPlanManager.getInstance().getPlanListByUserId(SPHelper.getUserId());
                        for (PlanEntity planEntity: planEntityList){
                            planEntity.setTrainTime(trainTimeSelect);
                            planEntity.setUpdateDate(DateFormatUtil.getNowDate());
                            TrainPlanManager.getInstance().update(planEntity);
                        }

                    }
                    Bundle bundle = new Bundle();
                    bundle.putInt(AppKeyManager.EXTRA_WEIGHT, weight);
                    bundle.putInt(AppKeyManager.EXTRA_TIMENUM, trainTimeSelect);
                    bundle.putInt(AppKeyManager.EXTRA_PLAN_TRAIN_NUM, trainCountOfTimeSelect);
                    bundle.putInt(AppKeyManager.EXTRA_MUSIC_FILE, musicPosition);
                    bundle.putString(AppKeyManager.EXTRA_MUSIC_NAME, selectMusic);
                    bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectMainMode);
//                    if (selectWeight != weight && Global.USER_MODE){
//                        MyUtil.insertTemplate(weight);
//                    }
                    startTargetActivity(bundle, StartActivity.class, true);
                }
                break;
            case R.id.layout_tread_num:
                trainTimesPop();
                break;
            case R.id.layout_weight:
                trainWeightPop();
                break;
            case R.id.layout_music:
                trainMusicPop();
                break;
            case R.id.layout_count_of_time:
                countOfTimesPop();
                break;
            case R.id.layout_times_of_day:
                timesOfDayPop();
                break;
            case R.id.btn_go_evaluate:
                Bundle bundle = new Bundle();
                bundle.putInt("TrainParam", 1);
                bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectEvaluateMode);
                startTargetActivity(bundle, EvaluateStartActivity.class, false);
                break;
        }
    }
    private void trainTip(){
        RxDialogSureCancel dialog = new RxDialogSureCancel(this);
        dialog.setContent("目前不能训练，请查看训练计划");
        dialog.setCancel("");
        dialog.setSure("我知道了");
        dialog.setSureListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }
    @SuppressLint("ClickableViewAccessibility")
    private void setChart() {

        MaxWeight = Integer.parseInt(SPHelper.getUser().getWeight());
        //获取数据数量
        dataSize = subPlanEntityList.size();

        if (dataSize != 0){
            dataPosLenX = (ZX - ZX_START) / dataSize;
        }
        // 取消描述文字
        chart.getDescription().setEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);//禁止双击放大
        // 透明背景
        chart.setBackgroundColor(Color.TRANSPARENT);


        // 没有数据时显示的文字
        chart.setNoDataText("暂无记录");
        // 没有数据时显示文字的颜色
        chart.setNoDataTextColor(Color.WHITE);
        // 绘图区后面的背景矩形将绘制
        chart.setDrawGridBackground(false);
        // 是否禁止绘制图表边框的线
        chart.setDrawBarShadow(false);
//        // 设置chart边框线的颜色。
//        chart.setBorderColor(Color.WHITE);
//        //设置 chart 边界线的宽度，单位 dp。
//        chart.setBorderWidth(1f);
        // 能否点击
        chart.setTouchEnabled(true);
        // 能否拖拽
        chart.setDragEnabled(true);
        // 能否缩放
        chart.setScaleXEnabled(false);
        chart.setAutoScaleMinMaxEnabled(true);
        chart.setScaleYEnabled(false);


        //设置从X轴出来的动画时间
        chart.animateX(0);
        //设置XY轴动画
        //chart.animateXY(1500,1500, Easing.EaseInSine, Easing.EaseInSine);

        // 绘制动画 从下到上
        // chart.animateY(1000);
        chart.setHighlightFullBarEnabled(false);
        chart.setExtraBottomOffset(10);//解决x轴底部显示被裁剪的问题
        // draw bars behind lines
        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.LINE
        });



        if (dataSize > 1){
            chart.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:


                            pos = (int) ((event.getX() - ZX_START) / dataPosLenX);
                            downX = event.getX(); //点击时候的X
                            downY = event.getY(); //点击时候的Y

                            if (pos < 0) {
                                pos = 0;
                            }
                            if (pos >= dataSize) {
                                pos = dataSize - 1;
                            }
                            isMoveChange = true;

                            return true;
                        case MotionEvent.ACTION_MOVE:
                            if (isMoveChange) {
                                if (pos >= dataSize - 1) {
                                    pos = dataSize - 1;
                                } else {
                                    update(pos, event.getY(), 1); //修改点
                                }
                            }
                            return chart.onTouchEvent(event);
                        case MotionEvent.ACTION_UP:
                            moveUpX = event.getX(); //移动松手时候的X
                            moveUpY = event.getY(); //移动松手时候的Y
                            if (moveUpX!=downX){
                                if (downX >= (dataPosLenX * (dataSize - 1))) {
                                    if (event.getY() <= 100) {
                                        if (moveUpX > downX + 20) {
                                            // update(pos, event.getY(), 2); //增加点
                                        }
                                        if (moveUpX - 20 <= downX) {
                                            //update(pos, event.getY(), 3); //减少点
                                        }
                                    }
                                }
                            }

                            isMoveChange = false;
                            return true;
                        case MotionEvent.ACTION_CANCEL:
                            isMoveChange = false;
                            return true;
                    }
                    return chart.onTouchEvent(event);
                }
            });
        }
        // 点击数据点弹出框
        ChartTodayMarkerView mv = new ChartTodayMarkerView(this, R.layout.chart_today_marker_view);
        mv.setChartView(chart); // For bounds control
        chart.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            ZX = chart.getWidth();
            ZY = chart.getHeight();
            if (dataSize != 0){
                dataPosLenX = (ZX - ZX_START) / dataSize;
            }
        });
    }

    private void setYAxis() {
        // 获取左y轴线
        YAxis leftAxis = chart.getAxisLeft();
        // 是否绘制轴线
        leftAxis.setDrawAxisLine(true);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawLabels(true);
        //是否绘制0所在的网格线/默认false绘制
        leftAxis.setDrawZeroLine(false);
        //将网格线设置为虚线模式
        leftAxis.enableGridDashedLine(10f,10f,0f);
        //获取图表右边y轴
        YAxis right=chart.getAxisRight();
        //禁用图表右边y轴
        right.setEnabled(false);


        // 设置文字大小
        leftAxis.setTextSize(axisFontSize);
//        leftAxis.setTypeface(Typeface.DEFAULT_BOLD);//字体加粗
        // 设置文字颜色
        leftAxis.setTextColor(getResources().getColor(R.color.white));
        //设置轴线颜色
        leftAxis.setAxisLineColor(getResources().getColor(R.color.white));
        //设置轴线宽度
        leftAxis.setAxisLineWidth(1f);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) value + "kg";
            }
        });
        leftAxis.setAxisMinimum(0f);


        // 获取左y轴线
//        YAxis rightAxis = chart.getAxisRight();
//        // 是否绘制轴线
//        rightAxis.setDrawAxisLine(true);
//        // 设置轴上每个点对应的线
//        rightAxis.setDrawGridLines(false);
//        // 绘制标签 指x轴上的对应数值
//        rightAxis.setDrawLabels(true);
//        // 设置文字大小
//        rightAxis.setTextSize(axisFontSize);
////        rightAxis.setTypeface(Typeface.DEFAULT_BOLD);//字体加粗
//        // 设置文字颜色
//        rightAxis.setTextColor(getResources().getColor(R.color.white));
//        //设置轴线颜色
//        rightAxis.setAxisLineColor(getResources().getColor(R.color.white));
//        //设置轴线宽度
//        rightAxis.setAxisLineWidth(1f);
//        rightAxis.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                return (int) value + "kg";
//            }
//        });
//        rightAxis.setAxisMinimum(0f);
    }

    private void initLegend() {
        Legend l = chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setTextSize(22);
        l.setTextColor(getResources().getColor(R.color.white));
        l.setTypeface(Typeface.DEFAULT_BOLD);//字体加粗
        l.setDrawInside(false);
        l.setEnabled(false);
    }

    private void initChart(List<SubPlanEntity> lists) {
        if (lists == null)
            return;
        // 获取x轴线
        XAxis xAxis = chart.getXAxis();
        // 是否绘制轴线
        // 是否绘制轴线
        xAxis.setDrawAxisLine(true);
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        // 设置轴上每个点对应的线
        xAxis.setDrawGridLines(true);
        xAxis.setDrawGridLinesBehindData(true);
        // 绘制标签 指x轴上的对应数值
        xAxis.setDrawLabels(true);
        // 设置x轴的显示位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // 设置文字大小
        xAxis.setTextSize(axisFontSize);
//        xAxis.setTypeface(Typeface.DEFAULT_BOLD);//字体加粗
        // 设置文字颜色
        xAxis.setTextColor(getResources().getColor(R.color.white));
        // 设置轴的显示个数
        xAxis.setLabelCount(7);
        // 图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        xAxis.setAvoidFirstLastClipping(false);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);//设置标签居中
        //设置轴线颜色
        xAxis.setAxisLineColor(getResources().getColor(R.color.white));
        //设置轴线宽度
        xAxis.setAxisLineWidth(1f);
//        xAxis.setLabelRotationAngle(2);//设置x轴字体显示角度
//        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= lists.size() || index < 0) {
                    return "";
                } else {
//                    index = index + 1;
                    return DateFormatUtil.getDay(subPlanEntityList.get(index).getStartDate());
                }
            }
        });

        CombinedData data = new CombinedData();
        data.setData(generateLineData(lists,originalSubPlanEntityList));
        data.setDrawValues(true);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(14);
//        data.setValueTypeface(Typeface.DEFAULT_BOLD);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) value + "";
            }
        });
        YAxis yLeftAxis = chart.getAxisLeft();
        yLeftAxis.setAxisMaximum(data.getYMax() + 5);
        xAxis.setAxisMaximum(data.getXMax() + 0.25f);
        xAxis.setAxisMinimum(0);
//        xAxis.setCenterAxisLabels(true);
        chart.setData(data);
        chart.invalidate();
    }

    private LineData generateLineData(List<SubPlanEntity> lists,List<OriginalSubPlanEntity> originalSubPlanEntityList) {
        LineData d = new LineData();
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<Entry> entries1 = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        for (int index = 0; index < lists.size(); index++) {
            entries.add(new Entry(index + 0.5f, lists.get(index).getLoad(), lists.get(index)));
        }
        if (originalSubPlanEntityList != null){
            for (int index = 0; index < originalSubPlanEntityList.size(); index++) {
                entries1.add(new Entry(index + 0.5f, originalSubPlanEntityList.get(index).getLoad(), originalSubPlanEntityList.get(index)));
            }
        }
        for (SubPlanEntity subPlanEntity : lists){
            if (DateFormatUtil.getString2Date(subPlanEntity.getStartDate()) > System.currentTimeMillis()){
                colors.add(Color.rgb(7, 190, 170));
            }else {
                colors.add(Color.rgb(192 ,192 ,192));
            }
        }
        LineDataSet set = new LineDataSet(entries, "负重值");
//        set.setColor(Color.rgb(7, 190, 170));
        set.setDrawCircles(true);
        set.setDrawValues(true);
        set.setCircleColors(colors);
        set.setDrawCircleHole(false);
        set.setLineWidth(2f);
        set.setValueTextColor(getResources().getColor(R.color.white));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        LineDataSet set1 = new LineDataSet(entries1, "负重值");
        set1.enableDashedLine(10f, 5f, 0f);
        set1.setColor(Color.rgb(139, 171, 59));
        set1.setDrawCircles(true);
        set1.setDrawValues(true);
        set1.setDrawCircleHole(true);
        d.addDataSet(set);
        d.addDataSet(set1);
        return d;
    }
    private void trainTimesPop() {
        final List<Integer> timeData = new ArrayList<>();
        int minTrainingTime = 5;
        int maxTrainingTime = 60;
        for (int i = minTrainingTime; i <= maxTrainingTime; i++) {
            timeData.add(i);
        }
        popupSheet = new PopupSheet(this, layoutTreadNum, timeData, new PopupSheetCallback() {
            @Override
            public View setupItemView(int position) {
                View itemV = LayoutInflater.from(TrainParamActivity.this).inflate(R.layout.item_music_dropdown, null);
                TextView titleTV = itemV.findViewById(R.id.tv_music);
                titleTV.setText(MessageFormat.format("{0}", timeData.get(position)));
                return itemV;
            }

            @Override
            public void itemClicked(ListPopupWindow popupWindow, int position) {
                popupWindow.dismiss();
                int data = timeData.get(position);
                trainTimeSelect = data;
                tvTreadNum.setText(MessageFormat.format("{0}", data));
            }
        }, DimensUtil.dp2px(260));
        popupSheet.show();
    }
    private void countOfTimesPop() {
        final List<Integer> countOfTimeData = new ArrayList<>();
        int minCountOfTime = 5;
        int maxCountOfTime = 200;
        for (int i = minCountOfTime; i <= maxCountOfTime; i+=5) {
            countOfTimeData.add(i);
        }
        popupSheet = new PopupSheet(this, layoutCountOfTime, countOfTimeData, new PopupSheetCallback() {
            @Override
            public View setupItemView(int position) {
                View itemV = LayoutInflater.from(TrainParamActivity.this).inflate(R.layout.item_music_dropdown, null);
                TextView titleTV = itemV.findViewById(R.id.tv_music);
                titleTV.setText(MessageFormat.format("{0}", countOfTimeData.get(position)));
                return itemV;
            }

            @Override
            public void itemClicked(ListPopupWindow popupWindow, int position) {
                popupWindow.dismiss();
                int data = countOfTimeData.get(position);
                trainCountOfTimeSelect = data;
                tvCountOfTime.setText(MessageFormat.format("{0}", data));
            }
        }, DimensUtil.dp2px(260));
        popupSheet.show();
    }
    private void timesOfDayPop() {
        final List<Integer> timesOfDayData = new ArrayList<>();
        int minCountOfTime = 1;
        int maxCountOfTime = 10;
        for (int i = minCountOfTime; i <= maxCountOfTime; i++) {
            timesOfDayData.add(i);
        }
        popupSheet = new PopupSheet(this, layoutTimesOfDay, timesOfDayData, new PopupSheetCallback() {
            @Override
            public View setupItemView(int position) {
                View itemV = LayoutInflater.from(TrainParamActivity.this).inflate(R.layout.item_music_dropdown, null);
                TextView titleTV = itemV.findViewById(R.id.tv_music);
                titleTV.setText(MessageFormat.format("{0}", timesOfDayData.get(position)));
                return itemV;
            }

            @Override
            public void itemClicked(ListPopupWindow popupWindow, int position) {
                popupWindow.dismiss();
                int data = timesOfDayData.get(position);
                tvTimesOfDay.setText(MessageFormat.format("{0}", data));
            }
        }, DimensUtil.dp2px(260));
        popupSheet.show();
    }

    private void trainWeightPop() {
        final List<Integer> weightData = new ArrayList<>();
        int minWeight = 2;
        int maxminWeight = 100;
        for (int i = minWeight; i <= maxminWeight; i++) {
            weightData.add(i);
        }
        popupSheet = new PopupSheet(this, layoutWeight, weightData, new PopupSheetCallback() {
            @Override
            public View setupItemView(int position) {
                View itemV = LayoutInflater.from(TrainParamActivity.this).inflate(R.layout.item_music_dropdown, null);
                TextView titleTV = itemV.findViewById(R.id.tv_music);
                titleTV.setText(MessageFormat.format("{0}", weightData.get(position)));
                return itemV;
            }

            @Override
            public void itemClicked(ListPopupWindow popupWindow, int position){
                popupWindow.dismiss();
                int data = weightData.get(position);
                weight = data;
                tvWeight.setText(MessageFormat.format("{0}", data));
//                MyUtil.insertTemplate(weight);
            }
        }, DimensUtil.dp2px(260));
        popupSheet.show();
    }

    private void trainMusicPop() {
        popupSheet = new PopupSheet(this, layoutTreadNum, musicData, new PopupSheetCallback() {
            @Override
            public View setupItemView(int position) {
                View itemV = LayoutInflater.from(TrainParamActivity.this).inflate(R.layout.item_music_dropdown, null);
                TextView titleTV = itemV.findViewById(R.id.tv_music);
                titleTV.setText(MessageFormat.format("{0}", musicData.get(position)));
                return itemV;
            }

            @Override
            public void itemClicked(ListPopupWindow popupWindow, int position) {
                popupWindow.dismiss();
                String musicName = musicData.get(position);
                selectMusic = musicName;
                musicPosition = position;
                SPHelper.saveMusicPosition(position);
                tvMusic.setText(MessageFormat.format("{0}", musicName));
            }
        }, DimensUtil.dp2px(260));
        popupSheet.show();
    }
    private void initVolume() {
        // 获取系统最大音量
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 设置voice_seekbar的最大值
        seekbarVolume.setMax(maxVolume);
        // 获取到当前 设备的音量
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        // 显示音量
        Log.e("当前音量百分比：", currentVolume * 100 / maxVolume + " %");
        seekbarVolume.setProgress(currentVolume);
    }

    @Override
    protected void onPause() {
        VideoPlayUtil.getInstance().stopPlayer();
        VideoPlayUtil.getInstance().destroyPlayer();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void update(int pos, float f, int isAdd) {


        if (f >= ZY) {
            f = ZY;
        }
        CombinedData data = chart.getCombinedData();
        YAxis yAxis = chart.getAxisLeft();
        int MaxYNow = (int) yAxis.getAxisMaximum(); //最大的Y值
        int KgDp = (int) (ZY / MaxYNow);
        int test = (int) (Math.abs((ZY - f))) / KgDp;
        if (test >= MaxWeight) {

            ToastUtils.showShort("最大只能设置" + MaxWeight + "KG");
            test = MaxWeight;
        }

        chart.clear();
        if (isAdd == 2) {


            if (subPlanEntityList.size() >= 2) {
                SubPlanEntity subPlanEntity1 = subPlanEntityList.get(subPlanEntityList.size() - 1);
                SubPlanEntity subPlanEntity2 = subPlanEntityList.get(subPlanEntityList.size() - 2);

                subPlanEntity1.setLoad((int) MaxWeight - ((MaxWeight - subPlanEntity2.getLoad())) / 2);
                subPlanEntityList.remove(subPlanEntityList.size() - 1);
                subPlanEntityList.add(subPlanEntityList.size(), subPlanEntity1);

            }

            //
            SubPlanEntity subPlanEntity1 = subPlanEntityList.get(subPlanEntityList.size() - 1);
            SubPlanEntity subPlanEntity=new SubPlanEntity();
            subPlanEntity.setKeyId(SnowflakeIdUtil.getUniqueId());
            subPlanEntity.setId(subPlanEntity1.getId() + 1); //设置ID
            subPlanEntity.setUserId(subPlanEntity1.getUserId());
            subPlanEntity.setPlanId(subPlanEntity1.getPlanId());
            subPlanEntity.setPlanStatus(0); //设置状态
            subPlanEntity.setClassId(subPlanEntity1.getClassId());
            subPlanEntity.setLoad((int) MaxWeight); //修改体重
            subPlanEntity.setWeekNum(subPlanEntity1.getWeekNum() + 1); //增加日期
            subPlanEntity.setDayNum(subPlanEntity1.getDayNum());
            subPlanEntity.setTrainTime(subPlanEntity1.getTrainTime());
            subPlanEntity.setTrainStep(subPlanEntity1.getTrainStep());
            subPlanEntity.setModifyStatus(subPlanEntity1.getModifyStatus());

            subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(7, subPlanEntity1.getStartDate())); //添加时间
            subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate(7, subPlanEntity1.getEndDate()));//添加时间


            subPlanEntityList.add(subPlanEntity1);


            setChart();
            setYAxis();
            initLegend();
            initChart(subPlanEntityList);



        } else if (isAdd == 1) {

            SubPlanEntity subPlanEntity = subPlanEntityList.get(pos);
            subPlanEntity.setLoad((int) test);
            subPlanEntityList.remove(pos);
            subPlanEntityList.add(pos, subPlanEntity);

            data.setData(generateLineData(subPlanEntityList,originalSubPlanEntityList));
            data.setDrawValues(true);
            data.setValueTextColor(Color.WHITE);
            data.setValueTextSize(16);
            data.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return (int) value + "kg";
                }
            });
            chart.setData(data);
            chart.notifyDataSetChanged();
            chart.invalidate();
        } else {

            SubPlanEntity subPlanEntity = subPlanEntityList.get(subPlanEntityList.size() - 2);
            subPlanEntity.setLoad((int) MaxWeight);
            subPlanEntityList.remove(pos);
            subPlanEntityList.add(pos, subPlanEntity);
            subPlanEntityList.remove(subPlanEntityList.size() - 1);
            data.setData(generateLineData(subPlanEntityList,originalSubPlanEntityList));
            chart.invalidate();
//            setChart();
//            setYAxis();
//            initLegend();
//            initChart(subPlanEntityList);
        }


    }

}
