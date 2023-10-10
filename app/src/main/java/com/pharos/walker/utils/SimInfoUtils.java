package com.pharos.walker.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.List;

public class SimInfoUtils {
    private static String TAG = "SimInfoUtils";

    private TelephonyManager telephonyManager;
    //移动运营商编号
    private String NetworkOperator;
    private Context context;

    public SimInfoUtils(Context context) {
        this.context = context;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    //获取sim卡iccid
    @SuppressLint("HardwareIds")
    public String getIccid() {
        String iccid = "N/A";
//        if (Build.VERSION.SDK_INT  >= Build.VERSION_CODES.Q) {
//            SubscriptionManager sm = SubscriptionManager.from(context);
//            List<SubscriptionInfo> sis =sm.getActiveSubscriptionInfoList();
//            if (sis.size() >= 1)
//            {
//                SubscriptionInfo si1 = sis.get(0);
//                iccid = String.valueOf(si1.getCardId());
//                String phoneNum1 = si1.getNumber();
//            }
//            if (sis.size() >= 2)
//            {
//                SubscriptionInfo si2 = sis.get(1);
//                String iccId2 = si2.getIccId();
//                String phoneNum2 = si2.getNumber();
//            }
////            // 获取SIM卡数量相关信息：
////            int count = sm.getActiveSubscriptionInfoCount();//当前实际插卡数量
////            int max   = sm.getActiveSubscriptionInfoCountMax();//当前卡槽数量
//         }else {
//            try {
//                iccid = telephonyManager.getSimSerialNumber();
//            }catch (SecurityException e){
//                Log.e(TAG, "getIccid: 未获取到权限");
//            }
//        }
//        if (Build.VERSION.SDK_INT  >= Build.VERSION_CODES.R){
//            String cmd = "service call iphonesubinfo 12 | grep -o '[0-9a-f]\\{8\\} ' | tail -n+3 | while read a; do echo -n \\\\u${a:4:4}\\\\u${a:0:4}; done";
//            ShellUtils.CommandResult commandResult = ShellUtils.execCmdGeneralUser(cmd,true,true);
//            if (commandResult != null && commandResult.successMsg.length() >=20){
//                iccid = commandResult.successMsg.substring(0,20);
//            }
//            Log.e(TAG, iccid);
//        }else {
//            try {
//                iccid = telephonyManager.getSimSerialNumber();
//            }catch (SecurityException e){
//                Log.e(TAG, "getIccid: 未获取到权限");
//            }
//        }
        try {
            iccid = telephonyManager.getSimSerialNumber();
        }catch (SecurityException e){
            Log.e(TAG, "getIccid: 未获取到权限");
        }
        return iccid;
    }


    //获取电话号码
    public String getNativePhoneNumber() {
        String nativePhoneNumber = "N/A";
        nativePhoneNumber = telephonyManager.getLine1Number();
        return nativePhoneNumber;
    }

    //获取手机服务商信息
    public String getProvidersName() {
        String providersName = "N/A";
        NetworkOperator = telephonyManager.getNetworkOperator();
        //IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
//        Flog.d(TAG,"NetworkOperator=" + NetworkOperator);
        if (NetworkOperator.equals("46000") || NetworkOperator.equals("46002")) {
            providersName = "中国移动";//中国移动
        } else if(NetworkOperator.equals("46001")) {
            providersName = "中国联通";//中国联通
        } else if (NetworkOperator.equals("46003")) {
            providersName = "中国电信";//中国电信
        }
        return providersName;

    }

    public String getPhoneInfo() {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        StringBuffer sb = new StringBuffer();

        sb.append("\nLine1Number = " + tm.getLine1Number());
        sb.append("\nNetworkOperator = " + tm.getNetworkOperator());//移动运营商编号
        sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());//移动运营商名称
        sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
        sb.append("\nSimOperator = " + tm.getSimOperator());
        sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
        sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
        sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
        return  sb.toString();
    }
}
