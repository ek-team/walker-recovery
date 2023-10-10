package com.pharos.walker.constants;

/**
 * Created by zhanglun on 2021/4/9
 * Describe:
 */
public class Global {
    public static final String Header = ":";
    public static final String Header_S = "FFFF";
    public static volatile boolean isReconnectBle = false;
    public static volatile boolean isConnected = false;
    public static volatile boolean isStartReadData = false;
    public static volatile String ConnectStatus = "unconnected";
    public static volatile String ConnectedAddress = null;
    public static volatile String TempConnectedAddress = null;
    public static String ConnectedName = null;
    public static boolean isSendHeart = true;
    public static int VOICE_SWITCH = 0;          //语音开关	开0 关1
    public static volatile int BLE_BATTERY = 0;          //蓝牙鞋电量
    public static volatile boolean USER_MODE = true;
    public static volatile int ConnectMainMode = 0;          //训练模式
    public static volatile int ConnectEvaluateMode = 1;      //评估模式
    public static volatile int ConnectUserMode = 2;          //用户模式
    public static volatile int ConnectSetMode = 3;          //设置模式
    public static volatile int ReleaseVersion = 0;          //0康复科版,1骨科版，2服务端版，3家庭版
    public static final int RecoveryVersion = 0;//康复科版
    public static final int OrthopedicsVersion = 1;//骨科版
    public static final int ServerVersion = 2;
    public static final int HomeVersion = 3;

    public static final int UploadStatus = 0;
    public static final int UploadLocalStatus = 1;
    public static final int UploadNetStatus = 2;
    public static final String Delimiter = "[";
    public static final String Comma = ",";
    public static boolean isChangSha = false;
    public static final int ChangSha = 101;//长沙下肢
    public static final int AnHui = 102;//安徽下肢
    public static final int TrainTime = 5;//默认训练时间
    public static final int TrainCountMinute = 10;//每分钟训练步数
    public static int MinTrainStep = 20;//最小训练步数
    public static final int MaxTrainStep = 200;//最大训练步数
    public static volatile int ReadCount = 0;
    public static int NoPlanUserEvaluateInterval = 2;//2天

    public static volatile int HomeSetting = 0;          //0 功能选择作为主页 1用户管理作为首页
    public static final int HomeMain = 0;
    public static final int HomeUser = 1;
    public static volatile boolean isOpenTest = false;
    public static int maxWeight = 100;
    public static int minWeight = 35;
    public static final String InitPassword = "12345678";
    public static volatile boolean isUploadAllData = false;
    public static volatile boolean isDownloadAllData = false;
    public static volatile String downloadSourceSerial = "";
}
