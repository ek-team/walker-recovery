package com.pharos.walker.utils;

import android.annotation.TargetApi;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.pharos.walker.application.MyApplication;
import com.pharos.walker.beans.OriginalPlanEntity;
import com.pharos.walker.beans.OriginalSubPlanEntity;
import com.pharos.walker.beans.PlanEntity;
import com.pharos.walker.beans.SubPlanEntity;
import com.pharos.walker.beans.TokenBean;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.constants.Api;
import com.pharos.walker.constants.Global;
import com.pharos.walker.database.ActivationCodeManager;
import com.pharos.walker.database.OriginalPlanManager;
import com.pharos.walker.database.OriginalSubPlanManager;
import com.pharos.walker.database.PlanGenerateManager;
import com.pharos.walker.database.SubPlanManager;
import com.pharos.walker.database.TrainPlanManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import okhttp3.Request;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by zhanglun on 2021/5/7
 * Describe:
 */
public class MyUtil {
    private static final long dayMs = 24 * 60 * 60 * 1000;
    public static int getDiagnosticNum(String diag){
        switch (diag){
            case "请选择":
                return 0;
            case "全髋关节置换":
                return 1;
            case "全膝关节置换":
                return 2;
            case "股骨颈骨折":
                return 3;
            case "股骨转子间骨折":
                return 4;
            case "胫骨平台骨折（钢板固定）":
                return 5;
            case "胫骨平台骨折（钢板内固定）":
                return 6;
            case "胫骨中段骨折（石膏固定）":
                return 7;
            case "胫骨中段骨折（髓内钉）":
                return 8;
            case "胫骨中段骨折（桥接钢板）":
                return 9;
            case "髋关节截骨术":
                return 10;
            case "踝关节骨折（钢板内固定）":
                return 11;
            case "跟骨骨折（钢板固定）":
                return 12;
            case "踝关节韧带损伤（踝关节韧带重建术）":
                return 13;
            case "股骨头坏死（腓骨移植术）":
                return 14;
            default:
                return 15;

        }
    }
    public static int getPlanNum(String treatmentMethodId){
        switch (treatmentMethodId){
            case "请选择":
                return 0;
            case "6"://全髋关节置换术（THA）
            case "8"://全髋关节置换术（THA）
            case "19"://全髋关节置换术（THA）
            case "20"://半髋关节置换术
                return 1;
            case "39"://全膝关节置换术（TKA）
            case "40"://膝关节单髁置换术
            case "41"://膝关节融合术
                return 2;
            case "17"://空心钉内固定术
            case "18"://FNS内固定术
                return 3;
            case "21"://髓内钉内固定术
            case "22"://钢板内固定术
                return 4;
            case "43"://外固定支架固定术
                return 5;
            case "42"://切开复位钢板内固定术（ORIF）
                return 6;
            case "胫骨中段骨折（石膏固定）":
                return 7;
            case "46"://切开复位髓内钉固定术
                return 8;
            case "47"://骨不连翻修钢板内固定术（ORIF）
                return 9;
            case "7"://髋臼周围截骨术
                return 10;
            case "49"://踝关节骨折钢板螺钉内固定术
            case "50"://钢板螺钉内固定术
                return 11;
            case "56"://切开复位钢板内固定术（ORIF）
            case "57"://微创螺钉固定术
                return 12;
            case "51"://踝部韧带急性损伤修复与重建术
                return 13;
            case "10"://带血管游离腓骨移植术
                return 14;
            case "1"://外固定支架
            case "2"://切开复位内固定术（ORIF）
            case "3"://切开复位内固定术（ORIF）
            case "4"://切开复位内固定术（ORIF）
            case "9"://髓芯减压术
            case "12"://病灶清除松质骨植骨术
            case "23"://股骨近端截骨钢板内固定术
            case "24"://股骨近端截骨髓内钉内固定术
            case "25"://切开复位钢板内固定术（ORIF）
            case "26"://切开复位髓内钉内固定术（ORIF）
            case "27"://股骨干骨不连动力化术
            case "28"://股骨干骨不连翻修钢板内固定术
            case "29"://切开复位髓内钉内固定术（ORIF）
            case "30"://切开复位钢板内固定术（ORIF）
            case "31"://切开复位髓内钉内固定术
            case "32"://截骨矫形钢板内固定术（ORIF）
            case "33"://髌骨骨折切开复位张力带内固定术
            case "38"://胫骨结节移位术
            case "44"://截骨矫形钢板内固定术（ORIF）
            case "45"://切开复位钢板内固定术（ORIF）
            case "48"://外固定支架术
            case "52"://踝关节融合术
            case "58"://切开复位钢板内固定术（ORIF）
            case "59"://微创螺钉固定术
            case "60"://切开复位钢板内固定术（ORIF）
            case "61"://切开复位克氏针内固定术（ORIF）
            case "62"://切开复位钢板内固定术（ORIF）
            case "63"://克氏针内固定术
            case "64"://关节融合术
            case "65"://扁平足矫正钢板内固定术
            case "66"://踇内、外翻矫正钢板内固定术
                return 15;//四个月的训练计划
            case "5"://切开复位内固定术（ORIF）
            case "11"://带血管蒂髂骨移植术
                return 16;//6个月的训练计划
            case "13"://髋关节镜下修复术
            case "14"://髋关节镜下微骨折术
            case "15"://肋软骨移植术
            case "34"://膝关节韧带损伤修复与重建术
            case "35"://膝关节镜下半月板修整/成形术
            case "36"://关节镜下膝关节软骨微骨折术
            case "37"://软骨损伤移植/修复术
            case "53"://肌腱断裂修复术
            case "54"://关节镜下膝关节软骨微骨折术
            case "55"://软骨损伤移植/修复术
                return 17;//8周的训练计划
            case "16"://髋关节置换术后假体翻修术
                return 18;//6周的训练计划
            default:
                return 1012;

        }
    }
    public static boolean isNoPlanUser(){
        return getPlanNum(SPHelper.getUser().getTreatmentMethodId()) == 1012;
    }
    public static boolean isGeneratePlan(UserBean userBean){
        if (!isNoPlanUser() && (System.currentTimeMillis() > DateFormatUtil.getString2Date(userBean.getDate())) && (TrainPlanManager.getInstance().isPlanEmpty(userBean.getUserId()))){
            return true;
        }else if (!isNoPlanUser() && !TextUtils.isEmpty(userBean.getStr()) && userBean.getStr().equals("plan")){
            TrainPlanManager.getInstance().clearTrainPlanDatabaseByUserId(userBean.getUserId());
            SPHelper.saveEvaluateDate(userBean.getUserId(),0);
            return true;
        }
        return false;
    }
    public static int insertTemplate(int saveResult,long planStartTime,String planFinishLoad){
        int diagType = getPlanNum(SPHelper.getUser().getTreatmentMethodId());
//        saveResult = getCalcValue(saveResult);
//        saveResult = 33;
        int value = 0;
        String startDate;
        String finishDate;
        switch (diagType){
            case 0:
                break;
            case 1://全髋关节置换  术后第一天开始负重，逐步负重,6 周内达完全负重
                value = PlanGenerateManager.getInstance().generatePlan1(saveResult,planStartTime,planFinishLoad);
//                TrainPlanManager.getInstance().insertList1(saveResult);
//                SubPlanManager.getInstance().modifySubPlanData(25, Global.MinTrainStep,Global.MaxTrainStep);
                break;
            case 2://全膝关节置换 术后第一天开始负重，逐步负重,6 周内达完全负重
                value = PlanGenerateManager.getInstance().generatePlan2(saveResult,planStartTime,planFinishLoad);
//                TrainPlanManager.getInstance().insertList2(saveResult);
//                SubPlanManager.getInstance().modifySubPlanData(Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
                break;
            case 3://股骨颈骨折
            case 4://股骨转子间骨折 1周时为健侧 51%，逐步增加,12周时为健侧 87%，直至 100%
                 value = PlanGenerateManager.getInstance().generatePlan3(saveResult,planStartTime,planFinishLoad);
//                TrainPlanManager.getInstance().insertList3(saveResult);
//                SubPlanManager.getInstance().modifySubPlanData(Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
                break;
            case 5://胫骨平台骨折（钢板固定）6周时20kg，逐步增加，16周左右达到健侧100%
                value = PlanGenerateManager.getInstance().generatePlan4(saveResult,planStartTime,planFinishLoad);
//                TrainPlanManager.getInstance().insertList4(saveResult);
//                SubPlanManager.getInstance().modifySubPlanData(Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
                break;
            case 6://胫骨平台骨折（钢板内固定）2周为一个周期，逐步由起始重量增加至完全负重，总周期39周
                value = PlanGenerateManager.getInstance().generatePlan5(saveResult,planStartTime,planFinishLoad);
//                TrainPlanManager.getInstance().insertList5(saveResult);
//                SubPlanManager.getInstance().modifySubPlanData(Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
                break;
            case 7://胫骨中段骨折（石膏固定）2周为一个周期，逐步由起始重量增加至完全负重，总周期24周
                value = PlanGenerateManager.getInstance().generatePlan6(saveResult,planStartTime,planFinishLoad);
//                TrainPlanManager.getInstance().insertList6(saveResult);
//                SubPlanManager.getInstance().modifySubPlanData(Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
                break;
            case 8://胫骨中段骨折（髓内钉）
            case 9://胫骨中段骨折（桥接钢板）2周为一个周期，逐步由起始重量增加至完全负重，总周期24周
                value = PlanGenerateManager.getInstance().generatePlan7(saveResult,planStartTime,planFinishLoad);
//                TrainPlanManager.getInstance().insertList7(saveResult);
//                SubPlanManager.getInstance().modifySubPlanData(Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
                break;
            case 10://胫骨远端骨折  髋关节截骨术  1到5周训练5到10kg，6到12周达到体重的50%
                value = PlanGenerateManager.getInstance().generatePlanJieGu(saveResult,planStartTime,planFinishLoad);
//                TrainPlanManager.getInstance().insertListJieGu(saveResult);
//                SubPlanManager.getInstance().modifySubPlanData(Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
                break;
            case 11://踝关节骨折（钢板内固定）术后两天开始训练，逐渐 16 周后达到完全负重
                value = PlanGenerateManager.getInstance().generatePlan8(saveResult,planStartTime,planFinishLoad);
//                TrainPlanManager.getInstance().insertList8(saveResult);
//                SubPlanManager.getInstance().modifySubPlanData(Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
                break;
            case 12://跟骨骨折（钢板固定）手术四周后开始负重训练，8周内10kg，10周20kg，12周40kg，直至完全负重
                value = PlanGenerateManager.getInstance().generatePlan9(saveResult,planStartTime,planFinishLoad);
//                TrainPlanManager.getInstance().insertList9(saveResult);
//                SubPlanManager.getInstance().modifySubPlanData(Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
                break;
            case 13://踝关节韧带损伤（踝关节韧带重建术）5公斤开始，逐步负重直至完全负重
                value = PlanGenerateManager.getInstance().generatePlan10(saveResult,planStartTime,planFinishLoad);
//                TrainPlanManager.getInstance().insertList10(saveResult);
//                SubPlanManager.getInstance().modifySubPlanData(Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
                break;
            case 14://股骨头坏死（腓骨移植术）术后七周达到12公斤，每2周增加5公斤，直到完全负重
                value = PlanGenerateManager.getInstance().generatePlan11(saveResult,planStartTime,planFinishLoad);
//                TrainPlanManager.getInstance().insertList11(saveResult);
//                SubPlanManager.getInstance().modifySubPlanData(Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
                break;
            case 15://四个月的默认计划
                startDate = SPHelper.getUser().getDate();
                finishDate = DateFormatUtil.getBeforeOrAfterDate(4*30,startDate);
                value =  PlanGenerateManager.getInstance().generateDefaultPlan(startDate,String.valueOf(saveResult),finishDate,planFinishLoad,planStartTime,Global.MinTrainStep);
                break;
            case 16://六个月的默认计划
                startDate = SPHelper.getUser().getDate();
                finishDate = DateFormatUtil.getBeforeOrAfterDate(6*30,startDate);
                value =  PlanGenerateManager.getInstance().generateDefaultPlan(startDate,String.valueOf(saveResult),finishDate,planFinishLoad,planStartTime,Global.MinTrainStep);
                break;
            case 17://八周的默认计划
                startDate = SPHelper.getUser().getDate();
                finishDate = DateFormatUtil.getBeforeOrAfterDate(8*7,startDate);
                value =  PlanGenerateManager.getInstance().generateDefaultPlan(startDate,String.valueOf(saveResult),finishDate,planFinishLoad,planStartTime,Global.MinTrainStep);
                break;
            case 18://六周的默认计划
                startDate = SPHelper.getUser().getDate();
                finishDate = DateFormatUtil.getBeforeOrAfterDate(6*7,startDate);
                value =  PlanGenerateManager.getInstance().generateDefaultPlan(startDate,String.valueOf(saveResult),finishDate,planFinishLoad,planStartTime,Global.MinTrainStep);
                break;
        }
        if (value == Integer.MAX_VALUE)
            return value;
        List<PlanEntity> planEntityList = TrainPlanManager.getInstance().getPlanListByUserId(SPHelper.getUserId());
        Gson gson = new Gson();
        for (PlanEntity planEntity : planEntityList){
            String json = gson.toJson(planEntity);
            OriginalPlanEntity originalPlanEntity = gson.fromJson(json,OriginalPlanEntity.class);
//            originalPlanEntity.setClassId(planEntity.getClassId());
//            originalPlanEntity.setCountOfTime(planEntity.getCountOfTime());
//            originalPlanEntity.setCreateDate(planEntity.getCreateDate());
//            originalPlanEntity.setEndDate(planEntity.getEndDate());
//            originalPlanEntity.setId(planEntity.getId());
//            originalPlanEntity.setKeyId(planEntity.getKeyId());
//            originalPlanEntity.setLoad(planEntity.getLoad());
//            originalPlanEntity.setPlanId(planEntity.getPlanId());
//            originalPlanEntity.setPlanStatus(planEntity.getPlanStatus());
//            originalPlanEntity.setPlanTotalDay(planEntity.getPlanTotalDay());
//            originalPlanEntity.setPlanType(planEntity.getPlanType());
//            originalPlanEntity.setStartDate(planEntity.getStartDate());
//            originalPlanEntity.setTimeOfDay(planEntity.getTimeOfDay());
//            originalPlanEntity.setTrainTime(planEntity.getTrainTime());
//            originalPlanEntity.setTrainType(planEntity.getTrainType());
//            originalPlanEntity.setUpdateDate(planEntity.getUpdateDate());
//            originalPlanEntity.setUserId(planEntity.getUserId());
//            originalPlanEntity.setWeight(planEntity.getWeight());
            OriginalPlanManager.getInstance().insert(originalPlanEntity);
        }
        List<SubPlanEntity> subPlanEntityList = SubPlanManager.getInstance().loadDataByUserId(SPHelper.getUserId());
        for (SubPlanEntity subPlanEntity : subPlanEntityList){
            String json = gson.toJson(subPlanEntity);
            OriginalSubPlanEntity originalSubPlanEntity = gson.fromJson(json,OriginalSubPlanEntity.class);
            OriginalSubPlanManager.getInstance().insert(originalSubPlanEntity);
        }
        return value;
    }
    public static int getCalcValue(int loadWeight){
        List<PlanEntity> planEntityList = TrainPlanManager.getInstance().getPlanListByUserId(SPHelper.getUserId());
        SubPlanEntity currentSubPlanEntity = SubPlanManager.getInstance().getThisWeekLoadEntity(SPHelper.getUserId());
        if (planEntityList != null && planEntityList.size() > 0  && currentSubPlanEntity != null){
            int currentWeight =  currentSubPlanEntity.getLoad();
            List<SubPlanEntity> subPlanEntityList = SubPlanManager.getInstance().loadDataByUserId(SPHelper.getUserId());
            int startLoad = TrainPlanManager.getInstance().getInitLoad(SPHelper.getUserId());
            int diff = 0;
            boolean isEnd = false;
            if (loadWeight > currentWeight){
                for (int i = 0; i < 100; i++){
                    diff++;
                    for (SubPlanEntity entity: subPlanEntityList){
                        if ((entity.getLoad() >= currentWeight) && (currentWeight + diff >= loadWeight)){
                            isEnd = true;
                            break;
                        }
                    }
                    if (isEnd){
                        break;
                    }
                }
//                loadWeight = (startLoad + diff <= 0)? 1:startLoad + diff;//值最少1kg
                loadWeight = startLoad + diff;
            }
            if (loadWeight < currentWeight){
                for (int i = 0; i < 100; i++){
                    diff--;
                    for (SubPlanEntity entity: subPlanEntityList){
                        if ((entity.getLoad() <= currentWeight) && (currentWeight + diff <= loadWeight)){
                            isEnd = true;
                            break;
                        }
                    }
                    if (isEnd){
                        break;
                    }
                }
//                loadWeight = (startLoad + diff <= 0)? 1:startLoad + diff;//值最少1kg
                loadWeight = startLoad + diff;//值最少1kg
            }
            Log.e("Insert train plan", "insertList1:currentWeight =  " + currentWeight );
            Log.e("Insert train plan", "insertList1:startLoad =  " + startLoad );
            Log.e("Insert train plan", "insertList1:diff =  " + diff );
        }
        return loadWeight;
    }

    public static String getPlanSummary(){
        int diagType = getPlanNum(SPHelper.getUser().getTreatmentMethodId());
        switch (diagType){
            case 1:
            case 2:
                return "术后第一天开始负重，逐步负重,6 周内达完全负重";
            case 3:
            case 4:
                return "1周时为健侧 51%，逐步增加,12周时为健侧 87%，直至 100%";
            case 5:
                return "6周时20kg，逐步增加，16周左右达到健侧100%";
            case 6:
                return "2周为一个周期，逐步由起始重量增加至完全负重，总周期24周";
            case 7:
            case 8:
            case 9:
                return "2周为一个周期，逐步由起始重量增加至完全负重，总周期24周";
            case 10:
                return "1到5周训练5到10kg，6到12周达到体重的50%";
            case 11:
                return "术后两天开始训练，逐渐 16 周后达到完全负重";
            case 12:
                return "手术四周后开始负重训练，8周内10kg，10周20kg，12周40kg，直至完全负重";
            case 13:
                return "5公斤开始，逐步负重直至完全负重";
            case 14:
                return "术后七周达到12公斤，每2周增加5公斤，直到完全负重";
            case 15:
                return "术后4个月达到完全负重";
            case 16:
                return "术后6个月达到完全负重";
            case 17:
                return "术后8周达到完全负重";
            case 18:
                return "术后6周达到完全负重";
            default:
                return "根据评估结果生成训练方案";
        }
    }
    public static String getMac() {
        String macSerial = "";
        if (AppUtils.isAppRoot()){
            String str = "";
            try
            {
                Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
                InputStreamReader ir = new InputStreamReader(pp.getInputStream());
                LineNumberReader input = new LineNumberReader(ir);
                for (; null != str;)
                {
                    str = input.readLine();
                    if (str != null)
                    {
                        macSerial = str.trim();// 去空格
                        break;
                    }
                }
            } catch (IOException ex) {
                // 赋予默认值
                ex.printStackTrace();
            }
        }else {
            macSerial = getMacFromHardware();//Android11 targetSdkVersion 改成29可获得
//            Log.e("myUtil", "getMac1: " + getLocalMacAddressFromBusybox());//Android11 以上获取不到
//            Log.e("myUtil", "getMac2: " + getMachineHardwareAddress());//Android11 targetSdkVersion 改成29可获得
//            Log.e("myUtil", "getMac3: " + getLocalMacAddressFromIp());//Android11 targetSdkVersion 改成29可获得
//            macSerial = getLocalMacAddressFromBusybox();
//            macSerial = getMachineHardwareAddress();
//            macSerial = getLocalMacAddressFromIp();
        }

        if (macSerial != null) {
            return macSerial.toLowerCase();
        }else {
            return null;
        }
    }
    /**
     * 根据IP地址获取MAC地址
     *
     * @return
     */
    private static String getLocalMacAddressFromIp() {
        String strMacAddr = null;
        try {
            //获得IpD地址
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {

        }

        return strMacAddr;
    }
    /**
     * 获取移动设备本地IP
     *
     * @return
     */
    private static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            //列举
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {//是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();//得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();//得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }

                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {

            e.printStackTrace();
        }
        return ip;
    }

    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
             for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * android 7.0及以上 （2）扫描各个网络接口获取mac地址
     *
     */
    /**
     * 获取设备HardwareAddress地址
     *
     * @return
     */
    public static String getMachineHardwareAddress() {
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        String hardWareAddress = null;
        NetworkInterface iF = null;
         if (interfaces == null) {
            return null;
        }
         while (interfaces.hasMoreElements()) {
            iF = interfaces.nextElement();
            try {
                hardWareAddress = bytesToString(iF.getHardwareAddress());
                if (hardWareAddress != null && iF.getName().equalsIgnoreCase("wlan0"))
                    break;
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return hardWareAddress;
    }
    /**
     * 根据busybox获取本地Mac
     *
     * @return
     */
    public static String getLocalMacAddressFromBusybox() {
        String result = "";
        String Mac = "";
        result = callCmd("busybox ifconfig", "HWaddr");
        // 如果返回的result == null，则说明网络不可取
        if (result == null) {
            return "网络异常";
        }
        // 对该行数据进行解析
        // 例如：eth0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
        if (result.length() > 0 && result.contains("HWaddr")) {
            Mac = result.substring(result.indexOf("HWaddr") + 6,
                    result.length() - 1);
            result = Mac;
        }
        return result;
    }

    private static String callCmd(String cmd, String filter) {
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);

            while ((line = br.readLine()) != null
                    && !line.contains(filter)) {
                result += line;
            }

            result = line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /***
     * byte转为String
     *
     * @param bytes
     * @return
     */
    private static String bytesToString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(String.format("%02X:", b));
        }
        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }
    public static void getToken(){
        OkHttpUtils.getAsyncToken(Api.tokenUrl + "?grant_type=client_credentials" , true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();

            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("MainActivity", "requestSuccess: " + result);
                TokenBean tokenBean = new Gson().fromJson(result,TokenBean.class);
                if (tokenBean.getCode() == 0){
                    SPHelper.saveToken(tokenBean.getData().getAccess_token());
                }

            }
        });
    }
    public static void updateProductStatus(int recordStatus){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"macAddress\"");
        sb.append(":");
        sb.append("\"");
        sb.append(ActivationCodeManager.getInstance().getCodeBean().getMacAddress());
        sb.append("\"");
        sb.append(",");
        sb.append("\"planUserTrainRecordStatus\"");
        sb.append(":");
        sb.append(recordStatus);
        sb.append("}");
        OkHttpUtils.postJsonAsync(Api.updateDeviceTrainStatus, sb.toString(), new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                Log.e("StartActivity", "requestSuccess: "+ e );
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("StartActivity", "requestSuccess: "+ result );

            }
        });

    }
    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()){
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    deleteFile(f);
                }
                file.delete();
            }
        }
    }
    /**
     * 获取目录下所有文件
     * @param path 指定目录路径
     * @return
     */
    public static List<String> getFilesAllName(String path) {
        File file=new File(path);
        File[] files=file.listFiles();
        if (files == null){
            Log.e("error","空目录");
            return null;
        }
        List<String> s = new ArrayList<>();
        for(int i =0;i<files.length;i++){
            s.add(files[i].getAbsolutePath());
        }
        return s;
    }
    /**
     * 获取目录下指定格式的文件
     * @param path 指定目录路径
     * @return
     */
    public static List<String> getFilesAllName(String path,String formatStr) {
        File file=new File(path);
        File[] files=file.listFiles();
        if (files == null){
            Log.e("error","空目录");
            return null;
        }
        List<String> s = new ArrayList<>();
        for(int i =0;i<files.length;i++){
            if (files[i].getAbsolutePath().endsWith(formatStr)){
                s.add(files[i].getAbsolutePath());
            }
        }
        return s;
    }
}
