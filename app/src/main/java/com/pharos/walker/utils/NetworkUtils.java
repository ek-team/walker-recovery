package com.pharos.walker.utils;

/**
 * Created by zhanglun on 2021/6/16
 * Describe:
 */
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.pharos.walker.application.MyApplication;
import com.pharos.walker.constants.Api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * <pre>
 *     desc  : 网络相关工具类
 * </pre>
 */
public class NetworkUtils {
    private static final String TAG = "NetworkUtils";

    private NetworkUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public enum NetworkType {
        NETWORK_WIFI,
        NETWORK_4G,
        NETWORK_3G,
        NETWORK_2G,
        NETWORK_UNKNOWN,
        NETWORK_NO
    }

    /**
     * 打开网络设置界面
     * <p>3.0以下打开设置界面</p>
     */
    public static void openWirelessSettings() {
        if (android.os.Build.VERSION.SDK_INT > 10) {
            MyApplication.getInstance().startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            MyApplication.getInstance().startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    /**
     * 获取活动网络信息
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @return NetworkInfo
     */
    private static NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager)MyApplication.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * 判断网络是否连接
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isConnected() {
        NetworkInfo info = getActiveNetworkInfo();
        return info != null && info.isConnected() && isAvailableByPing();
    }

    /**
     * 判断网络是否可用
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.INTERNET"/>}</p>
     *
     * @return {@code true}: 可用<br>{@code false}: 不可用
     */
    public static boolean isAvailableByPing() {
        String pingStr = "ping -c 1 -w 1 " + Api.domain;
        ShellUtils.CommandResult result = ShellUtils.execCmd("ping -c 1 -w 1 "+pingStr, false);//ping 易网健ip
//        ShellUtils.CommandResult result = ShellUtils.execCmd("ping -c 1 -w 1 223.5.5.5", false);
        boolean ret = result.result == 0;
        if (result.errorMsg != null) {
            Log.d(TAG , "isAvailableByPing errorMsg" +  result.toString());
        }
        if (result.successMsg != null) {
            Log.d(TAG ,"isAvailableByPing successMsg" + result.toString());
        }
        return ret;
    }
    public static ShellUtils.CommandResult pingResult(){
//        return ShellUtils.execCmd("ping -c 1 -w 1 119.37.193.94", false);//ping 易网健ip
        return ShellUtils.execCmd("ping -c 1 -w 1 223.5.5.5", false);//
    }

    /**
     * 判断移动数据是否打开
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean getDataEnabled() {
        try {
            TelephonyManager tm = (TelephonyManager) MyApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            Method getMobileDataEnabledMethod = tm.getClass().getDeclaredMethod("getDataEnabled");
            if (null != getMobileDataEnabledMethod) {
                return (boolean) getMobileDataEnabledMethod.invoke(tm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 打开或关闭移动数据
     * <p>需系统应用 需添加权限{@code <uses-permission android:name="android.permission.MODIFY_PHONE_STATE"/>}</p>
     *
     * @param enabled {@code true}: 打开<br>{@code false}: 关闭
     */
    public static void setDataEnabled(boolean enabled) {
        try {
            TelephonyManager tm = (TelephonyManager) MyApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            Method setMobileDataEnabledMethod = tm.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
            if (null != setMobileDataEnabledMethod) {
                setMobileDataEnabledMethod.invoke(tm, enabled);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断网络是否是4G
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean is4G() {
        NetworkInfo info = getActiveNetworkInfo();
        return info != null && info.isAvailable() && info.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE;
    }

    //检测GPRS是否打开
    public static boolean gprsIsOpenMethod(Context context) {

        ConnectivityManager mCM = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class cmClass = mCM.getClass();
        Class[] argClasses = null;
        Object[] argObject = null;

        Boolean isOpen = false;

        try {
            Method method = cmClass.getMethod("getMobileDataEnabled", argClasses);
            isOpen = (Boolean) method.invoke(mCM, argObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isOpen;
    }

    //开启/关闭GPRS
    public static void setGprsEnabled(Context context, boolean isEnable) {

        ConnectivityManager mCM = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class cmClass = mCM.getClass();
        Class[] argClasses = new Class[1];
        argClasses[0] = boolean.class;

        try {
            Method method = cmClass.getMethod("setMobileDataEnabled", argClasses);
            method.invoke(mCM, isEnable);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static boolean gprsEnabled(Context context,boolean bEnable)
    {
        if (Build.VERSION.SDK_INT>=21 ){
            setGprsEnabled(bEnable,context);
        }else {
            setGprsEnabled(context, bEnable);
        }
        return bEnable;
    }
    private final static String COMMAND_L_ON = "svc data enable\n ";
    private final static String COMMAND_L_OFF = "svc data disable\n ";
    private final static String COMMAND_SU = "su";
    public static void setGprsEnabled(boolean enable,Context context){

        String command;
        if(enable)
            command = COMMAND_L_ON;
        else
            command = COMMAND_L_OFF;

        try{
            Process su = Runtime.getRuntime().exec(COMMAND_SU);
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            outputStream.writeBytes(command);
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            try {
                su.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }

            outputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 判断wifi是否打开
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>}</p>
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean getWifiEnabled() {
        WifiManager wifiManager = (WifiManager) MyApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    /**
     * 打开或关闭wifi
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>}</p>
     *
     * @param enabled {@code true}: 打开<br>{@code false}: 关闭
     */
    public static void setWifiEnabled( boolean enabled) {
        WifiManager wifiManager = (WifiManager)MyApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (enabled) {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
        } else {
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
        }
    }

    /**
     * 判断wifi是否连接状态
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @return {@code true}: 连接<br>{@code false}: 未连接
     */
    public static boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) MyApplication.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 判断wifi数据是否可用
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>}</p>
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.INTERNET"/>}</p>
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isWifiAvailable() {
        return getWifiEnabled() && isAvailableByPing();
    }

    /**
     * 获取网络运营商名称
     * <p>中国移动、如中国联通、中国电信</p>
     *
     * @return 运营商名称
     */
    public static String getNetworkOperatorName() {
        TelephonyManager tm = (TelephonyManager) MyApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null ? tm.getNetworkOperatorName() : null;
    }

    private static final int NETWORK_TYPE_GSM      = 16;
    private static final int NETWORK_TYPE_TD_SCDMA = 17;
    private static final int NETWORK_TYPE_IWLAN    = 18;

    /**
     * 获取当前网络类型
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @return 网络类型
     * <ul>
     * <li>{@link NetworkType#NETWORK_WIFI   } </li>
     * <li>{@link NetworkType#NETWORK_4G     } </li>
     * <li>{@link NetworkType#NETWORK_3G     } </li>
     * <li>{@link NetworkType#NETWORK_2G     } </li>
     * <li>{@link NetworkType#NETWORK_UNKNOWN} </li>
     * <li>{@link NetworkType#NETWORK_NO     } </li>
     * </ul>
     */
    public static NetworkType getNetworkType() {
        NetworkType netType = NetworkType.NETWORK_NO;
        NetworkInfo info = getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {

            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                netType = NetworkType.NETWORK_WIFI;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {

                    case NETWORK_TYPE_GSM:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        netType = NetworkType.NETWORK_2G;
                        break;

                    case NETWORK_TYPE_TD_SCDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        netType = NetworkType.NETWORK_3G;
                        break;

                    case NETWORK_TYPE_IWLAN:
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        netType = NetworkType.NETWORK_4G;
                        break;
                    default:
                        String subtypeName = info.getSubtypeName();
                        if (subtypeName.equalsIgnoreCase("TD-SCDMA")
                                || subtypeName.equalsIgnoreCase("WCDMA")
                                || subtypeName.equalsIgnoreCase("CDMA2000")) {
                            netType = NetworkType.NETWORK_3G;
                        } else {
                            netType = NetworkType.NETWORK_UNKNOWN;
                        }
                        break;
                }
            } else {
                netType = NetworkType.NETWORK_UNKNOWN;
            }
        }
        return netType;
    }

    /**
     * 获取IP地址
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.INTERNET"/>}</p>
     *
     * @param useIPv4 是否用IPv4
     * @return IP地址
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            for (Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces(); nis.hasMoreElements(); ) {
                NetworkInterface ni = nis.nextElement();
                // 防止小米手机返回10.0.2.15
                if (!ni.isUp()) continue;
                for (Enumeration<InetAddress> addresses = ni.getInetAddresses(); addresses.hasMoreElements(); ) {
                    InetAddress inetAddress = addresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String hostAddress = inetAddress.getHostAddress();
                        boolean isIPv4 = hostAddress.indexOf(':') < 0;
                        if (useIPv4) {
                            if (isIPv4) return hostAddress;
                        } else {
                            if (!isIPv4) {
                                int index = hostAddress.indexOf('%');
                                return index < 0 ? hostAddress.toUpperCase() : hostAddress.substring(0, index).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String GetNetIp() {
        URL infoUrl = null;
        InputStream inStream = null;
        String line = "";
        try {
            infoUrl = new URL("http://pv.sohu.com/cityjson?ie=utf-8");
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
                StringBuilder strber = new StringBuilder();
                while ((line = reader.readLine()) != null)
                    strber.append(line + "\n");
                inStream.close();
                // 从反馈的结果中提取出IP地址
                int start = strber.indexOf("{");
                int end = strber.indexOf("}");
                String json = strber.substring(start, end + 1);
                if (json != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        line = jsonObject.optString("cip");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    /**
     * 获取域名ip地址
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.INTERNET"/>}</p>
     *
     * @param domain 域名
     * @return ip地址
     */
    public static String getDomainAddress(final String domain) {
        try {
            ExecutorService exec = Executors.newCachedThreadPool();
            Future<String> fs = exec.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    InetAddress inetAddress;
                    try {
                        inetAddress = InetAddress.getByName(domain);
                        return inetAddress.getHostAddress();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
            return fs.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 判断是否包含SIM卡
     *
     * @return 状态
     */
    public static boolean hasSimCard(Context context) {
        TelephonyManager telMgr = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        boolean result = true;
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
            case TelephonyManager.SIM_STATE_UNKNOWN:
                result = false; // 没有SIM卡
                break;
        }
        return result;
    }
}