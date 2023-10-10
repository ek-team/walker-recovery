package com.pharos.walker.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.pharos.walker.R;
import com.pharos.walker.beans.BLeDeviceInfoBean;
import com.pharos.walker.customview.MyMarkerView;
import com.pharos.walker.database.BleDeviceInfoManager;
import com.pharos.walker.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BleDeviceInfoActivity extends BaseActivity {
    @BindView(R.id.chart)
    LineChart chart;
    @BindView(R.id.chart1)
    LineChart chart1;

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        setChartStyle(chart,0);
//        setChartStyle(chart1,1);
        initLegend(chart);
//        initLegend(chart1);
    }
    private void initData() {
        List<Entry> dataList1 = new ArrayList<>();
        List<Entry> dataList2 = new ArrayList<>();
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();   //线条数据集合
        List<BLeDeviceInfoBean> deviceInfoBeanList = BleDeviceInfoManager.getInstance().loadAll();
        for (int i = 0; i < deviceInfoBeanList.size(); i++){
            dataList1.add(new Entry(i, deviceInfoBeanList.get(i).getInterval()));
            dataList2.add(new Entry(i, deviceInfoBeanList.get(i).getRssi()));
        }
        LineDataSet dataSetLeft = new LineDataSet(dataList1, "数据间隔(ms)");
        LineDataSet dataSetRight = new LineDataSet(dataList2, "蓝牙信号(db)");
        dataSetLeft.setLineWidth(2f);
        dataSetLeft.setColor(Color.parseColor("#008000"));
        dataSetLeft.setDrawCircles(false);
        dataSetLeft.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSetLeft.setDrawValues(false);
        dataSets.add(dataSetLeft);
        dataSetRight.setLineWidth(2f);
        dataSetRight.setColor(Color.parseColor("#FF0000"));
        dataSetRight.setDrawCircles(false);
        dataSetRight.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSetRight.setDrawValues(false);
        dataSets.add(dataSetRight);
        LineData data = new LineData(dataSets);
//        LineData data1 = new LineData(dataSetRight);
        chart.setData(data);
//        chart1.setData(data1);
        chart.invalidate();
    }
    private void setChartStyle(LineChart chart,int type) {
        //不显示描述内容
        chart.getDescription().setEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);//禁止双击放大
        //设置样式
        YAxis rightAxis = chart.getAxisRight();
        //设置图表右边的y轴禁用
        rightAxis.setEnabled(false);
//        rightAxis.setDrawZeroLine(true);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextSize(21f);
        leftAxis.setDrawZeroLine(true); // draw a zero line
        leftAxis.setZeroLineColor(Color.DKGRAY);
        leftAxis.setZeroLineWidth(1f);
//        leftAxis.setAxisMinimum(0f);
//        //y轴
//        leftAxis.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                DecimalFormat decimalFormat = new DecimalFormat("0.0");
//                return decimalFormat.format(value) + "kg";
//            }
//        });
        //设置x轴
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(Color.parseColor("#333333"));
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        xAxis.setTextSize(18f);
        xAxis.setAxisMinimum(0f);

        xAxis.setDrawAxisLine(false);//是否绘制轴线
        xAxis.setDrawGridLines(false);//设置x轴上每个点对应的线
        xAxis.setDrawLabels(false);//绘制标签  指x轴上的对应数值
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置
        xAxis.setGranularity(1f);//禁止放大后x轴标签重绘
        if (type == 0){
            MyMarkerView mv = new MyMarkerView(this, R.layout.custom_mark_view,"ms");
            chart.setMarker(mv);
        }else {
            MyMarkerView mv = new MyMarkerView(this, R.layout.custom_mark_view,"db");
            chart.setMarker(mv);
        }
    }
    private void initLegend(LineChart chart){
        Legend l = chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setTextSize(16);
        l.setTextColor(getResources().getColor(R.color.black));
        l.setDrawInside(false);
    }
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_ble_device_info;
    }

    @OnClick(R.id.tv_back)
    public void onViewClicked() {
        finish();
    }
    @OnClick(R.id.tv_clear)
    public void onClear() {
        BleDeviceInfoManager.getInstance().delete();
        initData();
        ToastUtils.showShort("清除成功！");
    }
}
