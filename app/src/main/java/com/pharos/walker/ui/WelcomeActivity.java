package com.pharos.walker.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.pharos.walker.MainActivity;
import com.pharos.walker.R;
import com.pharos.walker.beans.ActivationCodeBean;
import com.pharos.walker.bluetooth.BluetoothController;
import com.pharos.walker.constants.Global;
import com.pharos.walker.customview.rxdialog.RxDialogLogin;
import com.pharos.walker.database.ActivationCodeManager;
import com.pharos.walker.database.TrainPlanManager;
import com.pharos.walker.database.UserManager;
import com.pharos.walker.services.ForegroundWorkService;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.NetworkUtils;
import com.pharos.walker.utils.OneShotUtil;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.ToastUtils;

import java.io.DataOutputStream;
import java.io.IOException;


/**
 * Created by zhanglun on 2020/6/4
 * Describe:
 */
public class WelcomeActivity extends BaseActivity {

    private static final long DELAY_TIME = 1000;
    private Handler mHandler = new Handler();
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ForegroundWorkService.launch();
        BluetoothController.getInstance().initBle();
        UserManager mUserManager = UserManager.getInstance();
        if (!NetworkUtils.gprsIsOpenMethod(getApplicationContext())) {
            NetworkUtils.gprsEnabled(getApplicationContext(), true);
        }
        if (SPHelper.getUser().getId() <= 0) {
            SPHelper.saveUser(mUserManager.initUser(0L));//创建初始用户，并保存到本地
            if (SPHelper.getUser().getId() == 0) {
                mUserManager.changeGuest("快速模式", SPHelper.getUser().getCaseHistoryNo());
            }
            Global.USER_MODE = false;
        } else {
            mUserManager.changeGuest("快速模式", SPHelper.getUser().getCaseHistoryNo());
        }
        ActivationCodeBean localCodeBean = ActivationCodeManager.getInstance().getCodeBean();
        if (localCodeBean == null || TextUtils.isEmpty(localCodeBean.getMacAddress())) {
            ActivationCodeBean codeBean = new ActivationCodeBean();
            codeBean.setId(0L);
            codeBean.setCreateDate(System.currentTimeMillis());
            codeBean.setPublicKey("productStock");
            codeBean.setRecordDate(System.currentTimeMillis());
            codeBean.setMacAddress(MyUtil.getMac());
            ActivationCodeManager.getInstance().insertCodeBean(codeBean);
        } else if (TextUtils.isEmpty(localCodeBean.getMacAddress()) || !TextUtils.isEmpty(MyUtil.getMac())) {
            localCodeBean.setMacAddress(MyUtil.getMac());
            ActivationCodeManager.getInstance().insertCodeBean(localCodeBean);
//            ActivationCodeBean codeBean =  ActivationCodeManager.getInstance().getCodeBean();
//            codeBean.setActivationCode("1adbf93d94d7bd37b817a56e37d0fa47");
//            ActivationCodeManager.getInstance().insertCodeBean(codeBean);
        }
        if (!TextUtils.isEmpty(SPHelper.getSettingPassword())){
            RxDialogLogin rxDialogLogin = new RxDialogLogin(this);
            rxDialogLogin.setCancelable(false);
            rxDialogLogin.setCanceledOnTouchOutside(false);
            rxDialogLogin.setSureListener(v -> {
                String password = rxDialogLogin.getPassword();
                if (password.equals(SPHelper.getSettingPassword()) || password.equals(Global.InitPassword)){
                    startTargetActivity(MainActivity.class, true);
                    rxDialogLogin.dismiss();
                }else {
                    ToastUtils.showShort("密码错误");
                }
            });
            rxDialogLogin.show();
        }else {
            mHandler.postDelayed(() -> startTargetActivity(MainActivity.class, true), DELAY_TIME);
        }
//        TrainPlanManager.getInstance().clearTrainPlanDatabaseByUserId(SPHelper.getUserId());
//        mHandler.postDelayed(() -> startTargetActivity(FeedbackActivity.class,true), DELAY_TIME);
//        OneShotUtil.getInstance(this);
//        if (Build.VERSION.SDK_INT >= 21) {
//            final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkRequest.Builder builder = new NetworkRequest.Builder();
//
//            // 设置指定的网络传输类型(蜂窝传输) 等于手机网络
//            builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
//
//            NetworkRequest request = builder.build();
//            ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
//                /**
//                 * Called when the framework connects and has declared a new network ready for use.
//                 * This callback may be called more than once if the {@link Network} that is
//                 * satisfying the request changes.
//                 */
//                @TargetApi(Build.VERSION_CODES.M)
//                @Override
//                public void onAvailable(Network network) {
//                    super.onAvailable(network);
//                    Log.i("test", "已根据功能和传输类型找到合适的网络");
//
//                    // 可以通过下面代码将app接下来的请求都绑定到这个网络下请求
//                    if (Build.VERSION.SDK_INT >= 23) {
//                        connectivityManager.bindProcessToNetwork(network);
//                    } else {
//                        // 23后这个方法舍弃了
//                        ConnectivityManager.setProcessDefaultNetwork(network);
//                    }
//
//                    // 也可以在将来某个时间取消这个绑定网络的设置
//                    // if (Build.VERSION.SDK_INT >= 23) {
//                    //      onnectivityManager.bindProcessToNetwork(null);
//                    //} else {
//                    //     ConnectivityManager.setProcessDefaultNetwork(null);
//                    //}
//
//                    // 只要一找到符合条件的网络就注销本callback
//                    // 你也可以自己进行定义注销的条件
//                    connectivityManager.unregisterNetworkCallback(this);
//                }
//
//
//            };
////            connectivityManager.requestNetwork(request, callback);
//
//
//
//        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_welcome;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(null);
    }
}
