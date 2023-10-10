package com.pharos.walker.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

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
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pharos.walker.R;
import com.pharos.walker.adapter.TitleFragmentAdapter;
import com.pharos.walker.adapter.TrainPlanAdapter;
import com.pharos.walker.beans.ActivationCodeBean;
import com.pharos.walker.beans.InfoBean;
import com.pharos.walker.beans.OriginalSubPlanEntity;
import com.pharos.walker.beans.PlanEntity;
import com.pharos.walker.beans.PlanRecordBean;
import com.pharos.walker.beans.ProductInfoBean;
import com.pharos.walker.beans.SubPlanEntity;
import com.pharos.walker.beans.TokenBean;
import com.pharos.walker.constants.Api;
import com.pharos.walker.constants.Global;
import com.pharos.walker.customview.ChartTodayMarkerView;
import com.pharos.walker.customview.MyProgressDialog;
import com.pharos.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharos.walker.customview.rxdialog.RxTrainDataEditDialog;
import com.pharos.walker.database.ActivationCodeManager;
import com.pharos.walker.database.OriginalSubPlanManager;
import com.pharos.walker.database.PlanGenerateManager;
import com.pharos.walker.database.SubPlanManager;
import com.pharos.walker.database.TrainPlanManager;
import com.pharos.walker.ui.PlanActivity;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.NetworkUtils;
import com.pharos.walker.utils.OkHttpUtils;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.SnowflakeIdUtil;
import com.pharos.walker.utils.SpeechUtil;
import com.pharos.walker.utils.ToastUtils;
import com.tencent.mars.xlog.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

/**
 * Created by zhanglun on 2021/4/26
 * Describe:
 */
public class TrainPlanFragment extends Fragment {
    @BindView(R.id.tv_info)
    TextView tvInfo;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_train_time_day)
    TextView tvTrainTimeDay;
    @BindView(R.id.tv_train_count_time)
    TextView tvTrainCountTime;
    @BindView(R.id.btn_save_plan)
    TextView btnSavePlan;
    @BindView(R.id.btn_restore_plan)
    TextView btnRestorePlan;
    @BindView(R.id.btn_reset_plan)
    TextView btnResetPlan;
    @BindView(R.id.chart)
    CombinedChart chart;
    @BindView(R.id.top_tab)
    TabLayout topTab;
    @BindView(R.id.react_in_chain_switch)
    Switch reactInChainSwitch;
    @BindView(R.id.view_pager_top)
    ViewPager viewPagerTop;
    private TrainPlanAdapter adapter;
    private int axisFontSize = 22;
    public  static List<SubPlanEntity> subPlanEntityList;
    private List<OriginalSubPlanEntity> originalSubPlanEntityList;
    private Gson gson = new Gson();
    public boolean isClickModify = false;
    private PlanLoadFragment planLoadFragment;
    private PlanStepFragment planStepFragment;
    public static boolean isReactInChain = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = inflater.inflate(R.layout.fragment_train_plan, null);
        ButterKnife.bind(this, contentView);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        contentView.setLayoutParams(lp);
        initView();
        return contentView;
    }
    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage( Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                ToastUtils.showShort("保存成功");
            }else if (msg.what == 1){
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                ToastUtils.showShort("上传失败，错误码：" + msg.obj);
            }
        }
    };
    private void initView() {
        subPlanEntityList = SubPlanManager.getInstance().loadDataByUserId(SPHelper.getUserId());
        originalSubPlanEntityList = OriginalSubPlanManager.getInstance().loadDataByUserId(SPHelper.getUserId());
        List<Fragment> fragments = new ArrayList<>();
        planLoadFragment = new PlanLoadFragment(originalSubPlanEntityList);
        planStepFragment = new PlanStepFragment(originalSubPlanEntityList);
        fragments.add(planLoadFragment);
        fragments.add(planStepFragment);
        TitleFragmentAdapter adapter = new TitleFragmentAdapter(getActivity().getSupportFragmentManager(), fragments, new String[]{"负重计划","步数计划"},BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPagerTop.setOffscreenPageLimit(2);
        viewPagerTop.setAdapter(adapter);
        viewPagerTop.setCurrentItem(0);
        topTab.setupWithViewPager(viewPagerTop);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tvInfo.setText(MessageFormat.format("总体规划：{0}{1}。手术时间{2}", SPHelper.getUser().getDiagnosis(), MyUtil.getPlanSummary(), SPHelper.getUser().getDate().substring(0,SPHelper.getUser().getDate().indexOf(" "))));
        if (subPlanEntityList.size()<=0){
            btnSavePlan.setText("生成计划");
            btnRestorePlan.setVisibility(View.INVISIBLE);
            btnResetPlan.setVisibility(View.INVISIBLE);
        }
        reactInChainSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> isReactInChain = isChecked);

    }




    @OnClick({R.id.btn_save_plan, R.id.btn_restore_plan,R.id.btn_reset_plan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_save_plan:
                if (DateFormatUtil.avoidFastClick(2000)) {
                    if (btnSavePlan.getText().toString().contains("保存")){
                        SubPlanManager.getInstance().insertMany(subPlanEntityList);
                        List<PlanEntity> planEntityList = TrainPlanManager.getInstance().getPlanListByUserId(SPHelper.getUserId());
                        for (PlanEntity entity : planEntityList) {
                            entity.setUpdateDate(DateFormatUtil.getNowDate());
                            TrainPlanManager.getInstance().update(entity);
                        }
                         SubPlanManager.getInstance().modifySubPlanData(Global.TrainTime,subPlanEntityList);
                        int versionNum = subPlanEntityList.get(0).getVersion();
                        if (versionNum > 0){
                            versionNum = versionNum+1;
                        }
                        if (NetworkUtils.isConnected()){
                            showWaiting("处理数据","正在保存计划");
                            updatePlanRecord(versionNum);
                        }
                        planLoadFragment.refreshData();
                        planStepFragment.refreshData();
                        isClickModify = true;
                        ToastUtils.showShort("保存成功");
                    }else {
                        generatePlanDialog();
                    }

                }
                break;
            case R.id.btn_restore_plan:
                if (DateFormatUtil.avoidFastClick(2000)) {
                    Gson gson = new Gson();
                    String json = gson.toJson(originalSubPlanEntityList);
                    Type typeRecord = new TypeToken<List<SubPlanEntity>>() {
                    }.getType();
                    List<SubPlanEntity> subPlanEntityList1 = gson.fromJson(json, typeRecord);
                    if (subPlanEntityList != null) {
                        subPlanEntityList.clear();
                    }
                    subPlanEntityList = gson.fromJson(json, typeRecord);
                    List<PlanEntity> planEntityList = TrainPlanManager.getInstance().getPlanListByUserId(SPHelper.getUserId());
                    for (PlanEntity entity : planEntityList) {
                        entity.setUpdateDate(DateFormatUtil.getNowDate());
                        TrainPlanManager.getInstance().update(entity);
                    }
                    planLoadFragment.refreshData(subPlanEntityList1);
                    planStepFragment.refreshData(subPlanEntityList1);
                    SubPlanManager.getInstance().insertMany(subPlanEntityList1);
                    isClickModify = true;
                    ToastUtils.showShort("恢复完成");
                }
                break;
            case R.id.btn_reset_plan:
                RxDialogSureCancel dialogSureCancel = new RxDialogSureCancel(getContext());
                dialogSureCancel.setContent("重新生成计划会删除当前计划，是否重新生成");
                dialogSureCancel.setSure("重新生成");
                dialogSureCancel.setSureListener(v -> {
                    dialogSureCancel.dismiss();
                    generatePlanDialog();
                });
                dialogSureCancel.show();
                break;
        }
    }
    private void generatePlanDialog(){
        String date2String = DateFormatUtil.getDate2String(System.currentTimeMillis(),"yyyy-MM-dd");
//                        date2String = date2String + " 00:00:00";
        String startLoad = "3";
        if (SPHelper.getNoPlanUserEvaluateWeight(SPHelper.getUserId()) >0){
            startLoad = String.valueOf((int)SPHelper.getNoPlanUserEvaluateWeight(SPHelper.getUserId()));
        }
        RxTrainDataEditDialog dataEditDialog = new RxTrainDataEditDialog(getContext(),date2String,startLoad,date2String,SPHelper.getUser().getWeight());
        dataEditDialog.setSureListener(v -> {
            String startDate;
            if (dataEditDialog.startTime.length() > "yyyy-MM-dd".length()){
                startDate = dataEditDialog.startTime;
            }else {
                startDate = dataEditDialog.startTime + " 00:00:00";
            }
            String startWeight = dataEditDialog.getStartLoad();
            String endDate;
            if (dataEditDialog.endTime.length() > "yyyy-MM-dd".length()){
                endDate = dataEditDialog.endTime;
            }else {
                endDate = dataEditDialog.endTime + " 00:00:00";
            }
            String endLoad = dataEditDialog.getEndLoad();
            String initStep = dataEditDialog.getInitStep();
            if (TextUtils.isEmpty(endLoad)){
                ToastUtils.showShort("结束负重不能为空");
                return;
            }
            if (Integer.parseInt(endLoad) <=0){
                ToastUtils.showShort("结束负重不能小于0");
                return;
            }
            if (Integer.parseInt(initStep) <=0){
                ToastUtils.showShort("初始步数不能小于0");
                return;
            }
            if (Integer.parseInt(initStep) > Global.MaxTrainStep){
                ToastUtils.showShort("初始步数不能大于"+ Global.MaxTrainStep);
                return;
            }
            TrainPlanManager.getInstance().clearTrainPlanDatabaseByUserId(SPHelper.getUserId());
            PlanGenerateManager.getInstance().generateDefaultPlan(startDate,startWeight,endDate,endLoad,DateFormatUtil.getString2Date(startDate,null),Integer.parseInt(initStep));
            if (subPlanEntityList != null && subPlanEntityList.size()>0){
                subPlanEntityList.clear();
            }
            OriginalSubPlanManager.getInstance().clearPlanByUserId(SPHelper.getUserId());
            originalSubPlanEntityList.clear();
            subPlanEntityList = SubPlanManager.getInstance().loadDataByUserId(SPHelper.getUserId());
            for (SubPlanEntity subPlanEntity : subPlanEntityList){
                String json = gson.toJson(subPlanEntity);
                OriginalSubPlanEntity originalSubPlanEntity = gson.fromJson(json,OriginalSubPlanEntity.class);
                OriginalSubPlanManager.getInstance().insert(originalSubPlanEntity);
                originalSubPlanEntityList.add(originalSubPlanEntity);
            }
            planLoadFragment.refreshData(subPlanEntityList);
            planStepFragment.refreshData(subPlanEntityList);
            dataEditDialog.dismiss();
            btnSavePlan.setText("保存修改");
            btnRestorePlan.setVisibility(View.VISIBLE);
            btnResetPlan.setVisibility(View.VISIBLE);
        });
        dataEditDialog.show();

    }
    private void updatePlanRecord(int version){
        ActivationCodeBean activationCodeBean = ActivationCodeManager.getInstance().getCodeBean();
        PlanRecordBean planRecordBean = new PlanRecordBean();
        planRecordBean.setCreateTime(DateFormatUtil.getNowDate());
        planRecordBean.setUserId(SPHelper.getUserId());
        planRecordBean.setMacAdd(activationCodeBean.getMacAddress());
        planRecordBean.setProductSn(SPHelper.getSerialNumber());
        planRecordBean.setAfterVersion(version);
        OkHttpUtils.postJsonAsync(Api.updatePlanRecord, new Gson().toJson(planRecordBean), new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                Message msg = new Message();
                msg.what = 1;
                msg.obj = "请求失败";
                myHandler.sendMessage(msg);
            }


            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Setting Activity", "--->上传plan记录返回结果" + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0){
                    myHandler.sendEmptyMessage(0);
                } else if (code == 401) {
                    getToken(version);
                }else {
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = code;
                    myHandler.sendMessage(msg);
                }

            }
        });
    }
    private void getToken(int version) {
        OkHttpUtils.getAsyncToken(Api.tokenUrl + "?grant_type=client_credentials", true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                ToastUtils.showShort(e.getMessage());

            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("MainActivity", "requestSuccess: " + result);

                TokenBean tokenBean = new Gson().fromJson(result, TokenBean.class);
                if (tokenBean.getCode() == 0) {
                    SPHelper.saveToken(tokenBean.getData().getAccess_token());
                    updatePlanRecord(version);
                } else {
                    ToastUtils.showShort("Token 获取失败");
                }

            }
        });

    }
    private MyProgressDialog progressDialog;
    /**
     * 圆圈加载进度的 dialog
     */
    private void showWaiting(String title,String msg) {
        progressDialog = new MyProgressDialog(getActivity());
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setTitle(title);
        progressDialog.setMessage(msg);
        progressDialog.setIndeterminate(true);// 是否形成一个加载动画  true表示不明确加载进度形成转圈动画  false 表示明确加载进度
        progressDialog.setCancelable(false);//点击返回键或者dialog四周是否关闭dialog  true表示可以关闭 false表示不可关闭
        progressDialog.show();
    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        try {
//            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
//            childFragmentManager.setAccessible(true);
//            childFragmentManager.set(this, null);
//
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
