package com.pharos.walker.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.pharos.walker.BuildConfig;
import com.pharos.walker.MainActivity;
import com.pharos.walker.R;
import com.pharos.walker.beans.Battery;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.customview.NoScrollViewPager;
import com.pharos.walker.fragment.EvaluateRecordFragment;
import com.pharos.walker.fragment.InfoFragment;
import com.pharos.walker.fragment.RecycleFragment;
import com.pharos.walker.fragment.TrainPlanFragment;
import com.pharos.walker.fragment.TrainRecordFragment;
import com.pharos.walker.utils.AppUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/4/26
 * Describe:
 */
public class PlanActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tab)
    SegmentTabLayout tab;
    @BindView(R.id.pager)
    NoScrollViewPager pager;
    private TrainPlanFragment trainPlanFragment;
    private TrainRecordFragment recordFragment;
    private InfoFragment infoFragment;
    private RecycleFragment recycleFragment;
    private EvaluateRecordFragment evaluateFragment;
    private List<Fragment> mFragment = new ArrayList<>(2);

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        initView();

    }

    private void initView() {
        String[] mTitles;
        tab.setTabWidth(80);
        if (Global.ReleaseVersion == Global.HomeVersion){
            mTitles = new String[]{getResources().getString(R.string.plan), getResources().getString(R.string.record), getResources().getString(R.string.recycle)};
            tab.setTabData(mTitles);
            recycleFragment = new RecycleFragment();
            trainPlanFragment = new TrainPlanFragment();
            recordFragment = new TrainRecordFragment();
            mFragment.add(trainPlanFragment);
            mFragment.add(recordFragment);
            mFragment.add(recycleFragment);
        }else {
            mTitles = new String[]{getResources().getString(R.string.info),
                    getResources().getString(R.string.record),
                    getResources().getString(R.string.evaluate)};
            tab.setTabData(mTitles);
            infoFragment = new InfoFragment();
//            trainPlanFragment = new TrainPlanFragment();
            recordFragment = new TrainRecordFragment();
            evaluateFragment = new EvaluateRecordFragment();
            mFragment.add(infoFragment);
//            mFragment.add(trainPlanFragment);
            mFragment.add(recordFragment);
            mFragment.add(evaluateFragment);
        }

        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            @Override
            public int getCount() {
                return mFragment.size();
            }
        };
        pager.setAdapter(fragmentPagerAdapter);
        pager.setOffscreenPageLimit(2);
        tab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                pager.setCurrentItem(position, false);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    private void initData() {

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getAction()) {
            case MessageEvent.ACTION_GATT_CONNECTED:
                setPoint(true);
                break;
            case MessageEvent.ACTION_GATT_DISCONNECTED:
                setPoint(false);
                break;
            case MessageEvent.BATTERY_REFRESH:
                Battery battery = (Battery) event.getData();
                setBattery(battery.getBatteryVolume(),battery.getBatteryStatus());
                break;
            default:
                break;
        }
    }
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_plan;
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
//        finish();
        Bundle bundle = new Bundle();
        if (trainPlanFragment != null && trainPlanFragment.isClickModify){
            bundle.putInt("SelectUser",1);
        }else {
            bundle.putInt("SelectUser",0);
        }
        startTargetActivity(bundle, MainActivity.class,true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}
