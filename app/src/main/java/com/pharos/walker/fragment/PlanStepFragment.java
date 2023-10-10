package com.pharos.walker.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.pharos.walker.R;
import com.pharos.walker.beans.OriginalSubPlanEntity;
import com.pharos.walker.beans.SubPlanEntity;
import com.pharos.walker.constants.Global;
import com.pharos.walker.customview.ChartTodayMarkerView;
import com.pharos.walker.customview.MyCombinedChart;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.SnowflakeIdUtil;
import com.pharos.walker.utils.ToastUtils;
import com.tencent.mars.xlog.Log;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PlanStepFragment extends Fragment {
    @BindView(R.id.chart)
    MyCombinedChart chart;
    private int dataSize = 0;
    private int orDataSize = 0;
    private int dataPosLenX = 0;
    private int dataPosLenY = 0;
    private int ZX = 1240; //屏幕尺寸
    private int ZX_START = 50;
    private int ZY = 420; //屏幕尺寸
    private int pos = 0; //第几个点
    private int maxStep = Global.MaxTrainStep; //体重
    private float downX = 0; //点击时候的X
    private float downY = 0; //点击时候的Y
    private float moveUpX = 0; //移动时候的X
    private float moveUpY = 0; //移动时候的Y
    boolean isMoveChange = false; //是否要
    private Unbinder unbinder;
    private List<OriginalSubPlanEntity> originalSubPlanEntityList;
    private List<Integer> initStepList;
    private int axisFontSize = 22;
    private int lastFinishPosition = 0;
    public PlanStepFragment(List<OriginalSubPlanEntity> originalSubPlanEntityList) {
        this.originalSubPlanEntityList = originalSubPlanEntityList;
    }
    public void refreshData(List<SubPlanEntity> subPlanEntityList){
        setChart();
        setYAxis();
        initLegend();
        initChart(subPlanEntityList);
        copyLoad(subPlanEntityList);

    }
    public void refreshData(){
        setChart();
        setYAxis();
        initLegend();
        initChart(TrainPlanFragment.subPlanEntityList);
        copyLoad(TrainPlanFragment.subPlanEntityList);

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_plan_load, container, false);
        unbinder = ButterKnife.bind(this, root);
//        initView();
        return root;
    }

    private void initView() {
        setChart();
        setYAxis();
        initLegend();
        initChart(TrainPlanFragment.subPlanEntityList);
    }
    private void copyLoad(List<SubPlanEntity> subPlanEntityList){
        if (initStepList == null){
            initStepList = new ArrayList<>();
        }else {
            initStepList.clear();
        }
        for (SubPlanEntity subPlanEntity:subPlanEntityList){
            initStepList.add(subPlanEntity.getTrainStep());
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    private void setChart() {
        //获取数据数量
        dataSize = TrainPlanFragment.subPlanEntityList.size();
        orDataSize = originalSubPlanEntityList.size();
        for (int i = 0; i < dataSize; i++) {
            SubPlanEntity subPlanEntity = TrainPlanFragment.subPlanEntityList.get(i);
            if (System.currentTimeMillis() > DateFormatUtil.getString2Date(subPlanEntity.getStartDate()) &&
                    System.currentTimeMillis() < DateFormatUtil.getString2Date(subPlanEntity.getEndDate())){
                lastFinishPosition = i;
            }
        }
        if (TrainPlanFragment.subPlanEntityList.size() > originalSubPlanEntityList.size()) {
            dataPosLenX = (ZX - ZX_START) / dataSize;
        } else {
            if (dataSize != 0) {
                dataPosLenX = (ZX - ZX_START) / orDataSize;
            }
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
                            Log.e("djskjdslkdjs",downX+"--");
                            Log.e("djskjdslkdjs",downY+"--");
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
//                            moveUpX = event.getX(); //移动松手时候的X
//                            moveUpY = event.getY(); //移动松手时候的Y
//                            if (moveUpX != downX) {
//                                if (downX >= (dataPosLenX * (dataSize - 1))-100) {
//                                    if (event.getY() <= 100) {
//                                        if (moveUpX > downX) {
//                                            update(pos, event.getY(), 2); //增加点
//                                        }
//                                        if (moveUpX - 30 < downX) {
//                                            if (pos>=2){
//                                                update(pos, event.getY(), 3); //减少点
//                                            }else {
//                                                ToastUtils.showShort("至少保留两个周期");
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                            isMoveChange = false;
                            //update(event.getY());
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
        ChartTodayMarkerView mv = new ChartTodayMarkerView(getActivity(), R.layout.chart_today_marker_view);
        mv.setChartView(chart); // For bounds control
        chart.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            ZX = chart.getWidth();
            ZY = chart.getHeight();
            if (TrainPlanFragment.subPlanEntityList.size() > originalSubPlanEntityList.size()) {
                dataPosLenX = (ZX - ZX_START) / dataSize;
            } else {
                if (dataSize != 0) {
                    dataPosLenX = (ZX - ZX_START) / orDataSize;
                }
            }
        });
    }

    private int lastModifyValue = 0;
    private int frontMaxIndex = 0;
    private int behindMaxIndex = 0;
    private void modifyData(int modifyPosition,int modifyValue){
        float frontHalfDiff = 0;
        float behindHalfDiff = 0;
        int frontIndex = 0;
        int behindIndex = 0;
        int startStep = TrainPlanFragment.subPlanEntityList.get(lastFinishPosition).getTrainStep();
        frontMaxIndex = (modifyPosition - lastFinishPosition);
        behindMaxIndex = (dataSize-1 - modifyPosition);
        if (modifyPosition > lastFinishPosition){
            frontHalfDiff = (modifyValue - startStep)*1.0f/frontMaxIndex;
//             frontHalfDiff = modifyValue - lastModifyValue;
        }
        int finishStep = TrainPlanFragment.subPlanEntityList.get(dataSize-1).getTrainStep();
        if (dataSize-1 > modifyPosition){
            behindHalfDiff = (finishStep - modifyValue)*1.0f/behindMaxIndex;
//             behindHalfDiff = modifyValue - lastModifyValue;
        }
        for (int i = 0; i < dataSize; i++) {
            SubPlanEntity subPlanEntity = TrainPlanFragment.subPlanEntityList.get(i);
            if (i >= lastFinishPosition && i < modifyPosition){
                subPlanEntity.setTrainStep(Math.round(startStep + frontHalfDiff*frontIndex));
                if (subPlanEntity.getTrainStep() <= 0)
                    subPlanEntity.setTrainStep(0);
                TrainPlanFragment.subPlanEntityList.set(i,subPlanEntity);
                frontIndex ++;
            }
            if (i > modifyPosition && i < dataSize-1){
                behindIndex++;
                subPlanEntity.setTrainStep(Math.round(modifyValue + behindHalfDiff*behindIndex));
                if (subPlanEntity.getTrainStep() >= finishStep)
                    subPlanEntity.setTrainStep(finishStep);
                TrainPlanFragment.subPlanEntityList.set(i,subPlanEntity);
            }
        }
        lastModifyValue = modifyValue;

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
        chart.clear();
        if (isAdd == 2) {
            if (TrainPlanFragment.subPlanEntityList.size() >= 2) {
                SubPlanEntity subPlanEntity1 = TrainPlanFragment.subPlanEntityList.get(TrainPlanFragment.subPlanEntityList.size() - 1);
                SubPlanEntity subPlanEntity2 = TrainPlanFragment.subPlanEntityList.get(TrainPlanFragment.subPlanEntityList.size() - 2);
                subPlanEntity1.setTrainStep(maxStep - ((maxStep - subPlanEntity2.getTrainStep())) / 2);
                TrainPlanFragment.subPlanEntityList.remove(TrainPlanFragment.subPlanEntityList.size() - 1);
                TrainPlanFragment.subPlanEntityList.add(TrainPlanFragment.subPlanEntityList.size(), subPlanEntity1);
            }
            SubPlanEntity subPlanEntity1 = TrainPlanFragment.subPlanEntityList.get(TrainPlanFragment.subPlanEntityList.size() - 1);
            SubPlanEntity subPlanEntity = new SubPlanEntity();
            subPlanEntity.setKeyId(SnowflakeIdUtil.getUniqueId());
            subPlanEntity.setUserId(subPlanEntity1.getUserId());
            subPlanEntity.setPlanId(subPlanEntity1.getPlanId());
            subPlanEntity.setPlanStatus(0);
            subPlanEntity.setClassId(subPlanEntity1.getClassId());
            subPlanEntity.setLoad(subPlanEntity1.getLoad());
            subPlanEntity.setWeekNum(subPlanEntity1.getWeekNum() + 1);
            subPlanEntity.setDayNum(subPlanEntity1.getDayNum());
            subPlanEntity.setTrainTime(subPlanEntity1.getTrainTime());
            subPlanEntity.setTrainStep(subPlanEntity1.getTrainStep());
            subPlanEntity.setModifyStatus(subPlanEntity1.getModifyStatus());
            subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(7, subPlanEntity1.getStartDate()));
            subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate(7, subPlanEntity1.getEndDate()));
            subPlanEntity.setKeyId(SnowflakeIdUtil.getUniqueId());
            TrainPlanFragment.subPlanEntityList.add(subPlanEntity);
            setChart();
            setYAxis();
            initLegend();
            initChart(TrainPlanFragment.subPlanEntityList);
        } else if (isAdd == 1) {
            if (test > maxStep) {
                ToastUtils.showShort("最大只能设置" + maxStep + "步");
                test = maxStep;
            }
            if (pos == TrainPlanFragment.subPlanEntityList.size() - 1)
                return;
            SubPlanEntity subPlanEntity = TrainPlanFragment.subPlanEntityList.get(pos);
            subPlanEntity.setTrainStep(test);
            TrainPlanFragment.subPlanEntityList.remove(pos);
            TrainPlanFragment.subPlanEntityList.add(pos, subPlanEntity);
            lastModifyValue = initStepList.get(pos);
            if (TrainPlanFragment.isReactInChain){
                modifyData(pos,test);
            }
            data.setData(generateLineData(TrainPlanFragment.subPlanEntityList, originalSubPlanEntityList));
            data.setDrawValues(true);
            data.setValueTextColor(Color.WHITE);
            data.setValueTextSize(16);
            data.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return (int) value + "";
                }
            });
            chart.setData(data);
            chart.notifyDataSetChanged();
            chart.invalidate();
        } else {
            SubPlanEntity subPlanEntity = TrainPlanFragment.subPlanEntityList.get(TrainPlanFragment.subPlanEntityList.size() - 2);
            subPlanEntity.setTrainStep(maxStep);
            TrainPlanFragment.subPlanEntityList.remove(TrainPlanFragment.subPlanEntityList.size() - 1);
            TrainPlanFragment.subPlanEntityList.remove(TrainPlanFragment.subPlanEntityList.size() - 1);
            TrainPlanFragment.subPlanEntityList.add(subPlanEntity);
            setChart();
            setYAxis();
            initLegend();
            initChart(TrainPlanFragment.subPlanEntityList);
        }


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
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        //获取图表右边y轴
        YAxis right = chart.getAxisRight();
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
                return (int) value + "步";
            }
        });
        leftAxis.setAxisMinimum(0f);

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
        xAxis.setLabelCount(10);
        // 图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        xAxis.setAvoidFirstLastClipping(false);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);//设置标签居中
        //设置轴线颜色
        xAxis.setAxisLineColor(getResources().getColor(R.color.white));
        //设置轴线宽度
        xAxis.setAxisLineWidth(1f);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= lists.size() || index < 0) {
                    return "";
                } else {
//                    index = index + 1;
                    return DateFormatUtil.getDay(TrainPlanFragment.subPlanEntityList.get(index).getStartDate());
                }
            }
        });

        CombinedData data = new CombinedData();
        data.setData(generateLineData(lists, originalSubPlanEntityList));
        data.setDrawValues(true);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(16);
//        data.setValueTypeface(Typeface.DEFAULT_BOLD);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value <= 0){
                    return "0";
                }
                return (int) value + "";
            }
        });
        YAxis yLeftAxis = chart.getAxisLeft();
        yLeftAxis.setAxisMaximum(data.getYMax() + 20);
        xAxis.setAxisMaximum(data.getXMax() + 0.25f);
        xAxis.setAxisMinimum(0);
//        xAxis.setCenterAxisLabels(true);
        chart.setData(data);
        chart.invalidate();
    }

    private LineData generateLineData(List<SubPlanEntity> lists, List<OriginalSubPlanEntity> originalSubPlanEntityList) {
        LineData d = new LineData();
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<Entry> entries1 = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        for (int index = 0; index < lists.size(); index++) {
            entries.add(new Entry(index + 0.5f, lists.get(index).getTrainStep(), lists.get(index)));
        }
        if (originalSubPlanEntityList != null) {
            for (int index = 0; index < originalSubPlanEntityList.size(); index++) {
                entries1.add(new Entry(index + 0.5f, originalSubPlanEntityList.get(index).getTrainStep(), originalSubPlanEntityList.get(index)));
            }
        }
        LineDataSet set = new LineDataSet(entries, "步数");
        set.setColor(Color.rgb(7, 190, 170));
        set.setDrawCircles(true);
        set.setDrawValues(true);
//        set.setCircleColors(colors);
        set.setLineWidth(2f);
        set.setValueTextColor(getActivity().getResources().getColor(R.color.white));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        LineDataSet set1 = new LineDataSet(entries1, "步数");
        set1.enableDashedLine(10f, 5f, 0f);
        set1.setColor(Color.rgb(139, 171, 59));
        set1.setDrawCircles(true);
        set1.setDrawValues(true);
        set.setDrawCircleHole(false);
//        set.setCircleColors(colors);
        set1.setLineWidth(2f);
        set1.setValueTextColor(getActivity().getResources().getColor(R.color.white));
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        d.addDataSet(set);
//        d.addDataSet(set1);
        return d;
    }
    @Override
    public void onResume() {
        super.onResume();
        copyLoad(TrainPlanFragment.subPlanEntityList);
        initView();
        android.util.Log.e("PlanLoadFragment", "onResume: 步数页面显示了");
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
