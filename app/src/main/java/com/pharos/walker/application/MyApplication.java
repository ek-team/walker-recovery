package com.pharos.walker.application;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Environment;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.pharos.walker.BuildConfig;
import com.pharos.walker.R;
import com.pharos.walker.error.CrashHandler;
import com.pharos.walker.utils.GreenDaoHelper;
import com.tencent.mars.xlog.Log;
import com.tencent.mars.xlog.Xlog;

/**
 * Created by zhanglun on 2021/4/9
 * Describe:
 */
public class MyApplication extends Application {
    public static MyApplication instance;
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("c++_shared");
        System.loadLibrary("marsxlog");
    }
    public static MyApplication getInstance() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        CrashHandler.getInstance().init(instance);
        String param = "appid=" + getString(R.string.app_id) +
                "," +
                SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC;
        SpeechUtility.createUtility(MyApplication.this, param);
        GreenDaoHelper.initDatabase();
        String logPath = Environment.getExternalStorageDirectory().getPath() + "/FLSLog/xlog";
//        final String cachePath = this.getFilesDir() + "/xlog";
        final String cachePath = logPath;
        if (BuildConfig.DEBUG) {
            Xlog xlog = new Xlog();
            Log.setLogImp(xlog);
            Log.setConsoleLogOpen(true);
            Log.appenderOpen(Xlog.LEVEL_DEBUG, Xlog.AppednerModeAsync, cachePath, logPath, "WALKER", 0);
//            xlog.setMaxAliveTime(0,24*60*60);//log保存的时间 默认10天  s
//            Log.openLogInstance(Xlog.LEVEL_DEBUG, Xlog.AppednerModeAsync, "", logPath, "WALKER", 0);
        }else {
            Xlog xlog = new Xlog();
            Log.setLogImp(xlog);
            Log.setConsoleLogOpen(false);
            Log.appenderOpen(Xlog.LEVEL_ERROR, Xlog.AppednerModeAsync, cachePath, logPath, "WALKER", 0);//Xlog.LEVEL_ERROR  表示error等级才会记录到log
        }
    }
//    @Override
//    public Resources getResources() {
//        Resources res = super.getResources();
//        float fontSize = res.getConfiguration().fontScale;+---
//        if (fontSize != 1) {//非默认值
//            Configuration newConfig = new Configuration();
//            newConfig.setToDefaults();//设置默认
//            res.updateConfiguration(newConfig, res.getDisplayMetrics());
//        }
//        return res;
//    }
//    //设置字体为默认大小，不随系统字体大小改而改变
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        if (newConfig.fontScale != 1)//非默认值
//            getResources();
//        super.onConfigurationChanged(newConfig);
//    }
    /**
     * 重写 getResource 方法，防止系统字体影响
     */
    @Override
    public Resources getResources() {//禁止app字体大小跟随系统字体大小调节
        Resources resources = super.getResources();
        if (resources != null && resources.getConfiguration().fontScale != 1.0f) {
            android.content.res.Configuration configuration = resources.getConfiguration();
            configuration.fontScale = 1.0f;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        return resources;
    }
}
