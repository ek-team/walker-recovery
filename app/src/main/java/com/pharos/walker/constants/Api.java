package com.pharos.walker.constants;

/**
 * Created by zhanglun on 2021/6/15
 * Describe:
 */
public class Api {
//    public static final String baseUrl = "http://pharos.ewj100.com";
    public static final String domain = "pharos3.ewj100.com";
    public static final String baseUrl = "https://" + domain;
//    public static final String baseNewUrl = "https://pharos3.ewj100.com";
//    public static final String baseUrl = "https://api.jhxiao-school.com";
//    public static final String baseUrl = "http://tyuftw.natappfree.cc";
//    public static final String testUrl = "http://tyuftw.natappfree.cc";
    public static final String updateUrl = "https://pharos3.ewj100.com/walknew.apk";
    public static final String getNewVersion = baseUrl + "/deviceVersion/newVersion";//get  参数type  传数字 101 长沙下肢 102 安徽下肢  201 长沙气动 202 安徽气动
    public static final String uploadVersionInfo = baseUrl + "/productStock/updateVersion"; //put "参数 versionStr（版本）  macAddress（mac地址）";
    public static final String downloadAppUrl = baseUrl + "/file/"; //get "参数 {date}/{fileName}";
    public static final String tokenUrl = baseUrl + "/oauth/token";//参数username :test Password：test
    public static final String uploadUser = baseUrl + "/palnUser/saveBatch";//post 上传用户  后台判断 账号存在 则覆盖
    public static final String uploadUserRegister = baseUrl + "/palnUser/newSaveBatch";//post 上传用户 后台判断账号存在则返回
    public static final String getUserInfo = baseUrl + "/palnUser/getByUId/";//{uid } get
    public static final String uploadPlan = baseUrl +  "/plan/save";//post 上传计划
    public static final String getPlan = baseUrl + "/plan/listByUid/";//{uid }用户id get 查询计划
    public static final String uploadTrainRecord = baseUrl + "/planUserTrainRecord/save";//post 上传训练计划
    public static final String getRecord = baseUrl + "/planUserTrainRecord/pageByUid/";//{uid } 用户id get 获取训练记录
    public static final String getActivationCode = baseUrl + "/productStock/getByMac/";// {macAddress}  get 获取激活码
    public static final String qrUrl = baseUrl + "/user/srBindAdress/";// {uid}  用户二位码链接
    public static final String qrRegisterUrl = baseUrl + "/palnUser/registerPlanUser/";// {uid} 注册二位码链接
    public static final String getServerTimestamp = baseUrl + "/sys/getTime";// {uid}  获取服务器时间
    public static final String getHospitalByAddress = baseUrl + "/HospitalInfo/listCompatible";// get  获取当前地址下的医院  参数 province（省） city（市） area（区）
    public static final String getDoctorByHospital = baseUrl + "/HospitalInfo/listByDoctor";// get  获取当前医院下的医生  参数 id（医院id)
    public static final String getPlatformQr = baseUrl + "/liveQrCode/qrcodeByMac/";// get  获取平台二维码
    public static final String getPlatformQrScanUser = baseUrl + "/deviceScanSignLog/list";// 参数macAddress get  获取平台二维码扫描用户
    public static final String deletePlatformQrScanUser = baseUrl + "/deviceScanSignLog/deleteByMacAddress/";// {macAddress} get  删除平台二维码扫描用户
    public static final String clearPlatformQrScanUser = baseUrl + "/palnUser/cleanBindInfo";// {macAdd} get  删除平台二维码扫描用户
    public static final String getHospitalByMac = baseUrl + "/productStock/getBindHospital/";// {macAddress} get 获取设备绑定的医院
    public static final String uploadEvaluateRecord = baseUrl + "/evaluationRecords/addEvaluationRecords";// 上传评估记录
    public static final String getEvaluateRecord = baseUrl + "/evaluationRecords/getEvaluationRecords";// 获取评估记录
    public static final String uploadFiles = baseUrl + "/file/upload";// 上传文件  参数 file：文件。dir：路径可以写死 log
    public static final String addLinkToDevice = baseUrl + "/devicelog/add";// 链接添加到mac对应的日志  post请求 参数。macAdd   logUrl：接口返回的url
    public static final String uploadDataFiles = baseUrl + "/file/uploadProductStockInfo";// 上传文件  参数 file：文件数组。
    public static final String downloadDataFiles = baseUrl + "/file/downloadProductStockInfo";// 下载文件 get请求  参数 macAdd。
    public static final String getQrCodeLink = baseUrl + "/liveQrCode/getLiveQrCodeUrl";// 获取设备二维码链接
    public static final String getDoctorTeam = baseUrl + "/doctorTeam/getByMacAdd";// 获取设备医生团队
    public static final String getCloudDeviceStatus = baseUrl + "/productStock/getByMacAddress";//get 参数macAdd 根据mac地址查询设备
    public static final String getHospitalNameByMacAddress = baseUrl + "/productStock/getByMacAddressHospitalInfo";//get 参数macAddress 根据mac地址查询对应医院
    public static final String updateCloudDeviceStatus = baseUrl + "/productStock/updateDataById";// put 请求修改设备标识参数id mac地址  tag ：1 打开 0关闭
    public static final String updateDeviceTrainStatus = baseUrl + "/productStock/updateDataByMacAdd";//put 参数macAddress planUserTrainRecordStatus;//1-训练 2-未训练
    public static final String updatePlanRecord = baseUrl + "/updateSubPlanRecord/save";// post请求  参数 macAdd userId  用户id afterVersion 修改之后的版本号  createTime 创建时间 macAdd  设备mac地址 productSn 设备序列号。
    public static final String getUserInfoByUserId = baseUrl + "/palnUser/getUserInfoByUserId";//get 参数userId
//    public static final String updateUrl = "https://www.flssh.cn/jiqiren2/public/uploads/20210609/7738d738127aeeb377cac86d3ad03dd7.apk";
}
