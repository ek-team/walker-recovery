package com.pharos.walker.customview.rxdialog;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pharos.walker.R;
import com.pharos.walker.beans.PlanEntity;
import com.pharos.walker.customview.MyDatePickerDialog;
import com.pharos.walker.utils.DateFormatUtil;

import org.joda.time.DateTime;

import java.text.MessageFormat;
import java.util.Calendar;

/**
 * Created by zhanglun on 2021/5/10
 * Describe:
 */
public class RxTrainDataEditDialog extends RxDialog {
    private TextView mTvCancel;
    private TextView mTvSure;
    private TextView tvTitle;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvEndLoad;
    private TextView tvStartLoad;
    private TextView tvInitStep;
    private MyDatePickerDialog mDatePickerDialog;
    private Context context;
    public String startTime;
    public String endTime;
    public String startLoad;
    public String endLoad;
    public String initStep;
    private int mode = 0;
    public RxTrainDataEditDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public RxTrainDataEditDialog(Context context,String startTime,String startLoad,String endTime,String endLoad) {
        super(context);
        this.context = context;
        this.startTime = startTime;
        this.startLoad = startLoad;
        this.endTime = endTime;
        this.endLoad = endLoad;
        initView();
        initDataTime();
    }

    public RxTrainDataEditDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    public void setSureListener(View.OnClickListener listener) {
        mTvSure.setOnClickListener(listener);
    }
    public String getStartLoad(){
        return tvStartLoad.getText().toString();
    }
    public String getEndLoad(){
        return tvEndLoad.getText().toString();
    }
    public String getInitStep(){
        return tvInitStep.getText().toString();
    }
    private void initView() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_train_data_edit, null);
        mTvSure = dialogView.findViewById(R.id.tv_sure);
        mTvCancel = dialogView.findViewById(R.id.tv_cancel);
        tvTitle = dialogView.findViewById(R.id.tv_title);
        tvStartTime = dialogView.findViewById(R.id.txtStartDate);
        tvEndTime = dialogView.findViewById(R.id.txtEndDate);
        tvEndLoad = dialogView.findViewById(R.id.tv_train_load_end);
        tvStartLoad = dialogView.findViewById(R.id.tv_train_load);
        tvInitStep = dialogView.findViewById(R.id.tv_train_step);
        tvStartTime.setText(startTime);
        tvStartLoad.setText(startLoad);
        tvEndTime.setText(endTime);
        tvEndLoad.setText(endLoad);
        mTvCancel.setOnClickListener(v -> dismiss());
        tvStartTime.setOnClickListener(v -> {
            mode = 0;
            if (mDatePickerDialog != null) {
                mDatePickerDialog.show();
            }
        });
        tvEndTime.setOnClickListener(v -> {
            mode = 1;
            if (mDatePickerDialog != null) {
                mDatePickerDialog.show();
            }
        });
        setContentView(dialogView);
    }
    private void initDataTime() {
        Calendar c = Calendar.getInstance();
        mDatePickerDialog = new MyDatePickerDialog(context,
                // 绑定监听器
                (view, year, monthOfYear, dayOfMonth) -> {
                    int month = monthOfYear + 1;
                    DateTime a = new DateTime(year, month, dayOfMonth, 0, 0);
//                    int month = monthOfYear + 1;
//                    DateTime a = new DateTime(year, month, dayOfMonth, 0, 0);
//                    DateTime curDate = DateTime.now();
//                    if (a.isAfter(curDate)) {
//                        year = curDate.getYear();
//                        month = curDate.getMonthOfYear();
//                        dayOfMonth = curDate.getDayOfMonth();
//                    }
                    if (mode == 0){
                        startTime = new DateTime(year, month, dayOfMonth, 0, 0,0).toString("yyyy-MM-dd HH:mm:ss");
                        tvStartTime.setText(year + "-" + month + "-" + dayOfMonth);
                    }else {
                        endTime = new DateTime(year, month, dayOfMonth, 23, 59,59).toString("yyyy-MM-dd HH:mm:ss");
                        tvEndTime.setText(year + "-" + month + "-" + dayOfMonth);
                    }
                }
                // 设置初始日期
                , c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
    }
}
