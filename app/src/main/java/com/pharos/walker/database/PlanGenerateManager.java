package com.pharos.walker.database;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.widget.Spinner;

import com.pharos.walker.beans.PlanEntity;
import com.pharos.walker.beans.SubPlanEntity;
import com.pharos.walker.constants.Global;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.SPHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlanGenerateManager {
    private static volatile PlanGenerateManager instance = null;
    private static final long dayMs = 24 * 60 * 60 * 1000;
    public static PlanGenerateManager getInstance() {
        if (instance == null) {
            synchronized (PlanGenerateManager.class) {
                if (instance == null) {
                    instance = new PlanGenerateManager();
                }
            }
        }
        return instance;
    }

    public static boolean getLowerLimitValue(int targetLoad,List<SubPlanEntity> subPlanEntityList,long planStartTime){
        SubPlanEntity currentEntity = SubPlanManager.getInstance().getThisDayLoadEntity(subPlanEntityList,planStartTime);
        return currentEntity.getLoad() <= targetLoad;
    }
    public static boolean getUpLimitValue(int targetLoad,List<SubPlanEntity> subPlanEntityList,long planStartTime){
        SubPlanEntity currentEntity = SubPlanManager.getInstance().getThisDayLoadEntity(subPlanEntityList,planStartTime);
        return currentEntity.getLoad() > targetLoad;
    }
    @TargetApi(Build.VERSION_CODES.N)
    public static int calcInitLoad(int targetLoad,long planStartTime,String planFinishLoad) {
        String date = SPHelper.getUser().getDate();
        int lowerLimit = -100;
        int upLimit = 100;
        for (int m = 0;m<100;m++){
            List<SubPlanEntity> subPlanEntityList = SubPlanManager.getInstance().insertTest(date, lowerLimit, 1, 6,planFinishLoad);
            try{
                if (getLowerLimitValue(targetLoad,subPlanEntityList,planStartTime)){
                    Log.e("MyUtil", "calcInitLoadLowerLimit: " + lowerLimit);
                    break;
                }
            }catch (Exception e){
                if (e.toString().contains("null object reference")){
                    return Integer.MAX_VALUE;
                }
            }
            lowerLimit = lowerLimit -50;
        }
        if (lowerLimit <= -5100){
            return Integer.MAX_VALUE;
        }
        for (int m = 0;m<100;m++){
            List<SubPlanEntity> subPlanEntityList = SubPlanManager.getInstance().insertTest(date, upLimit, 1, 6,planFinishLoad);
            if (getUpLimitValue(targetLoad,subPlanEntityList,planStartTime)){
                Log.e("MyUtil", "calcInitLoadUpLimit: " + upLimit);
                break;
            }
            upLimit = upLimit +50;
        }
        int lastLoad = 0;
        for (int i = lowerLimit; i < upLimit; i++) {
            List<SubPlanEntity> subPlanEntityList = SubPlanManager.getInstance().insertTest(date, i, 1, 6,planFinishLoad);
            SubPlanEntity currentEntity = SubPlanManager.getInstance().getThisDayLoadEntity(subPlanEntityList,planStartTime);
            Log.e("MyUtil", "calcInitLoad: " + i + "  " + currentEntity.getLoad());
            if (lastLoad < currentEntity.getLoad() && currentEntity.getLoad() >= targetLoad) {
                Log.e("MyUtil", "calcInitLoadResult: " + i);
                return i;
            }
        }

        return 0;
    }
    @TargetApi(Build.VERSION_CODES.N)
    public static int calcInitLoad3(int targetLoad,long planStartTime,String planFinishLoad){
        int lowerLimit = -100;
        int upLimit = 100;
        for (int m = 0;m<100;m++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList3Test(lowerLimit,planFinishLoad);
            try{
                if (getLowerLimitValue(targetLoad,subPlanEntityList,planStartTime)){
                    Log.e("MyUtil", "calcInitLoadLowerLimit: " + lowerLimit);
                    break;
                }
            }catch (Exception e){
                if (e.toString().contains("null object reference")){
                    return Integer.MAX_VALUE;
                }
            }
            lowerLimit = lowerLimit -50;
        }
        if (lowerLimit <= -5100){
            return Integer.MAX_VALUE;
        }
        for (int m = 0;m<100;m++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList3Test(upLimit,planFinishLoad);
            if (getUpLimitValue(targetLoad,subPlanEntityList,planStartTime)){
                Log.e("MyUtil", "calcInitLoadUpLimit: " + upLimit);
                break;
            }
            upLimit = upLimit +50;
        }
        int lastLoad = 0;
        for (int i = lowerLimit; i< upLimit;i++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList3Test(i,planFinishLoad);
            SubPlanEntity currentEntity = SubPlanManager.getInstance().getThisDayLoadEntity(subPlanEntityList,planStartTime);
            Log.e("MyUtil", "calcInitLoad: " + i + "  " + currentEntity.getLoad());
            if (lastLoad < currentEntity.getLoad() && currentEntity.getLoad() >= targetLoad){
                Log.e("MyUtil", "calcInitLoadResult: " + i);
                return i;
            }
            lastLoad = currentEntity.getLoad();
        }
        return 0;
    }
    @TargetApi(Build.VERSION_CODES.N)
    public static int calcInitLoad4(int targetLoad,long planStartTime,String planFinishLoad){
        int lowerLimit = -100;
        int upLimit = 100;
        for (int m = 0;m<100;m++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList4Test(lowerLimit,planFinishLoad);
            try{
                if (getLowerLimitValue(targetLoad,subPlanEntityList,planStartTime)){
                    Log.e("MyUtil", "calcInitLoadLowerLimit: " + lowerLimit);
                    break;
                }
            }catch (Exception e){
                if (e.toString().contains("null object reference")){
                    return Integer.MAX_VALUE;
                }
            }
            lowerLimit = lowerLimit -50;
        }
        if (lowerLimit <= -5100){
            return Integer.MAX_VALUE;
        }
        for (int m = 0;m<100;m++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList4Test(upLimit,planFinishLoad);
            if (getUpLimitValue(targetLoad,subPlanEntityList,planStartTime)){
                Log.e("MyUtil", "calcInitLoadUpLimit: " + upLimit);
                break;
            }
            upLimit = upLimit +50;
        }
        int lastLoad = 0;
        for (int i = lowerLimit; i< upLimit;i++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList4Test(i,planFinishLoad);
            SubPlanEntity currentEntity = SubPlanManager.getInstance().getThisDayLoadEntity(subPlanEntityList,planStartTime);
            Log.e("MyUtil", "calcInitLoad: " + i + "  " + currentEntity.getLoad());
            if (lastLoad < currentEntity.getLoad() && currentEntity.getLoad() >= targetLoad){
                Log.e("MyUtil", "calcInitLoadResult: " + i);
                return i;
            }
        }
        return 0;
    }
    @TargetApi(Build.VERSION_CODES.N)
    public static int calcInitLoad5(int targetLoad,long planStartTime,String planFinishLoad){
        int lowerLimit = -100;
        int upLimit = 100;
        for (int m = 0;m<100;m++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList5Test(lowerLimit,planFinishLoad);
            try{
                if (getLowerLimitValue(targetLoad,subPlanEntityList,planStartTime)){
                    Log.e("MyUtil", "calcInitLoadLowerLimit: " + lowerLimit);
                    break;
                }
            }catch (Exception e){
                if (e.toString().contains("null object reference")){
                    return Integer.MAX_VALUE;
                }
            }
            lowerLimit = lowerLimit -50;
        }
        if (lowerLimit <= -5100){
            return Integer.MAX_VALUE;
        }
        for (int m = 0;m<100;m++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList5Test(upLimit,planFinishLoad);
            if (getUpLimitValue(targetLoad,subPlanEntityList,planStartTime)){
                Log.e("MyUtil", "calcInitLoadUpLimit: " + upLimit);
                break;
            }
            upLimit = upLimit +50;
        }
        int lastLoad = 0;
        for (int i = lowerLimit; i< upLimit;i++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList5Test(i,planFinishLoad);
            SubPlanEntity currentEntity = SubPlanManager.getInstance().getThisDayLoadEntity(subPlanEntityList,planStartTime);
            Log.e("MyUtil", "calcInitLoad: " + i + "  " + currentEntity.getLoad());
            if (lastLoad < currentEntity.getLoad() && currentEntity.getLoad() >= targetLoad){
                Log.e("MyUtil", "calcInitLoadResult: " + i);
                return i;
            }
        }
        return 0;
    }
    @TargetApi(Build.VERSION_CODES.N)
    public static int calcInitLoad6(int targetLoad,long planStartTime,String planFinishLoad){
        int lowerLimit = -100;
        int upLimit = 100;
        for (int m = 0;m<100;m++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList6Test(lowerLimit,planFinishLoad);
            try{
                if (getLowerLimitValue(targetLoad,subPlanEntityList,planStartTime)){
                    Log.e("MyUtil", "calcInitLoadLowerLimit: " + lowerLimit);
                    break;
                }
            }catch (Exception e){
                if (e.toString().contains("null object reference")){
                    return Integer.MAX_VALUE;
                }
            }
            lowerLimit = lowerLimit -50;
        }
        if (lowerLimit <= -5100){
            return Integer.MAX_VALUE;
        }
        for (int m = 0;m<100;m++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList6Test(upLimit,planFinishLoad);
            if (getUpLimitValue(targetLoad,subPlanEntityList,planStartTime)){
                Log.e("MyUtil", "calcInitLoadUpLimit: " + upLimit);
                break;
            }
            upLimit = upLimit +50;
        }
        int lastLoad = 0;
        for (int i = lowerLimit; i< upLimit;i++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList6Test(i,planFinishLoad);
            SubPlanEntity currentEntity = SubPlanManager.getInstance().getThisDayLoadEntity(subPlanEntityList,planStartTime);
            Log.e("MyUtil", "calcInitLoad: " + i + "  " + currentEntity.getLoad());
            if (lastLoad < currentEntity.getLoad() && currentEntity.getLoad() >= targetLoad){
                Log.e("MyUtil", "calcInitLoadResult: " + i);
                return i;
            }
        }
        return 0;
    }
    @TargetApi(Build.VERSION_CODES.N)
    public static int calcInitLoad7(int targetLoad,long planStartTime,String planFinishLoad){
        int lowerLimit = -100;
        int upLimit = 100;
        for (int m = 0;m<100;m++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList7Test(lowerLimit,planFinishLoad);
            try{
                if (getLowerLimitValue(targetLoad,subPlanEntityList,planStartTime)){
                    Log.e("MyUtil", "calcInitLoadLowerLimit: " + lowerLimit);
                    break;
                }
            }catch (Exception e){
                if (e.toString().contains("null object reference")){
                    return Integer.MAX_VALUE;
                }
            }
            lowerLimit = lowerLimit -50;
        }
        if (lowerLimit <= -5100){
            return Integer.MAX_VALUE;
        }
        for (int m = 0;m<100;m++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList7Test(upLimit,planFinishLoad);
            if (getUpLimitValue(targetLoad,subPlanEntityList,planStartTime)){
                Log.e("MyUtil", "calcInitLoadUpLimit: " + upLimit);
                break;
            }
            upLimit = upLimit +50;
        }
        int lastLoad = 0;
        for (int i = lowerLimit; i< upLimit;i++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList7Test(i,planFinishLoad);
            SubPlanEntity currentEntity = SubPlanManager.getInstance().getThisDayLoadEntity(subPlanEntityList,planStartTime);
            Log.e("MyUtil", "calcInitLoad: " + i + "  " + currentEntity.getLoad());
            if (lastLoad < currentEntity.getLoad() && currentEntity.getLoad() >= targetLoad){
                Log.e("MyUtil", "calcInitLoadResult: " + i);
                return i;
            }
        }
        return 0;
    }
    @TargetApi(Build.VERSION_CODES.N)
    public static int calcInitLoad8(int targetLoad,long planStartTime,String planFinishLoad){
        int lowerLimit = -100;
        int upLimit = 100;
        for (int m = 0;m<100;m++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList8Test(lowerLimit,planFinishLoad);
            try{
                if (getLowerLimitValue(targetLoad,subPlanEntityList,planStartTime)){
                    Log.e("MyUtil", "calcInitLoadLowerLimit: " + lowerLimit);
                    break;
                }
            }catch (Exception e){
                if (e.toString().contains("null object reference")){
                    return Integer.MAX_VALUE;
                }
            }
            lowerLimit = lowerLimit -50;
        }
        if (lowerLimit <= -5100){
            return Integer.MAX_VALUE;
        }
        for (int m = 0;m<100;m++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList8Test(upLimit,planFinishLoad);
            if (getUpLimitValue(targetLoad,subPlanEntityList,planStartTime)){
                Log.e("MyUtil", "calcInitLoadUpLimit: " + upLimit);
                break;
            }
            upLimit = upLimit +50;
        }
        int lastLoad = 0;
        for (int i = lowerLimit; i< upLimit;i++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList8Test(i,planFinishLoad);
            SubPlanEntity currentEntity = SubPlanManager.getInstance().getThisDayLoadEntity(subPlanEntityList,planStartTime);
            Log.e("MyUtil", "calcInitLoad: " + i + "  " + currentEntity.getLoad());
            if (lastLoad < currentEntity.getLoad() && currentEntity.getLoad() >= targetLoad){
                Log.e("MyUtil", "calcInitLoadResult: " + i);
                return i;
            }
        }
        return 0;
    }
    @TargetApi(Build.VERSION_CODES.N)
    public static int calcInitLoad10(int targetLoad,long planStartTime,String planFinishLoad){
        int lowerLimit = -100;
        int upLimit = 100;
        for (int m = 0;m<100;m++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList10Test(lowerLimit,planFinishLoad);
            try{
                if (getLowerLimitValue(targetLoad,subPlanEntityList,planStartTime)){
                    Log.e("MyUtil", "calcInitLoadLowerLimit: " + lowerLimit);
                    break;
                }
            }catch (Exception e){
                if (e.toString().contains("null object reference")){
                    return Integer.MAX_VALUE;
                }
            }
            lowerLimit = lowerLimit -50;
        }
        if (lowerLimit <= -5100){
            return Integer.MAX_VALUE;
        }
        for (int m = 0;m<100;m++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList10Test(upLimit,planFinishLoad);
            if (getUpLimitValue(targetLoad,subPlanEntityList,planStartTime)){
                Log.e("MyUtil", "calcInitLoadUpLimit: " + upLimit);
                break;
            }
            upLimit = upLimit +50;
        }
        int lastLoad = 0;
        for (int i = lowerLimit; i< upLimit;i++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList10Test(i,planFinishLoad);
            SubPlanEntity currentEntity = SubPlanManager.getInstance().getThisDayLoadEntity(subPlanEntityList,planStartTime);
            Log.e("MyUtil", "calcInitLoad: " + i + "  " + currentEntity.getLoad());
            if (lastLoad < currentEntity.getLoad() && currentEntity.getLoad() >= targetLoad){
                Log.e("MyUtil", "calcInitLoadResult: " + i);
                return i;
            }
        }
        return 0;
    }
    @TargetApi(Build.VERSION_CODES.N)
    public static int calcInitLoad11(int targetLoad,long planStartTime,String planFinishLoad){
        int lowerLimit = -100;
        int upLimit = 100;
        for (int m = 0;m<100;m++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList11Test(lowerLimit,planFinishLoad);
            try{
                if (getLowerLimitValue(targetLoad,subPlanEntityList,planStartTime)){
                    Log.e("MyUtil", "calcInitLoadLowerLimit: " + lowerLimit);
                    break;
                }
            }catch (Exception e){
                if (e.toString().contains("null object reference")){
                    return Integer.MAX_VALUE;
                }
            }
            lowerLimit = lowerLimit -50;
        }
        if (lowerLimit <= -5100){
            return Integer.MAX_VALUE;
        }
        for (int m = 0;m<100;m++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList11Test(upLimit,planFinishLoad);
            if (getUpLimitValue(targetLoad,subPlanEntityList,planStartTime)){
                Log.e("MyUtil", "calcInitLoadUpLimit: " + upLimit);
                break;
            }
            upLimit = upLimit +50;
        }
        int lastLoad = 0;
        for (int i = lowerLimit; i< upLimit;i++){
            List<SubPlanEntity> subPlanEntityList = TrainPlanManager.insertList11Test(i,planFinishLoad);
            SubPlanEntity currentEntity = SubPlanManager.getInstance().getThisDayLoadEntity(subPlanEntityList,planStartTime);
            Log.e("MyUtil", "calcInitLoad: " + i + "  " + currentEntity.getLoad());
            if (lastLoad < currentEntity.getLoad() && currentEntity.getLoad() >= targetLoad){
                Log.e("MyUtil", "calcInitLoadResult: " + i);
                return i;
            }
        }
        return 0;
    }
    public int generatePlan1(int targetLoad,long planStartTime,String planFinishLoad){
        int initValue = calcInitLoad(targetLoad,planStartTime,planFinishLoad);
        if (initValue == Integer.MAX_VALUE)
            return initValue;
        String startDate = SPHelper.getUser().getDate();
        String endDate = DateFormatUtil.increaseOneDayOneSecondLess(6*7-1,startDate);
        PlanEntity planEntity = TrainPlanManager.getInstance().insert(initValue,1,startDate,endDate,8,6*7);
        if (planStartTime < DateFormatUtil.getString2Date(endDate)){
            planEntity.setStartDate(DateFormatUtil.getDate2String(planStartTime,"yyyy-MM-dd") + " 00:00:00");
            TrainPlanManager.getInstance().insert(planEntity);
        }else {
            String date2String = DateFormatUtil.getDate2String(planStartTime,"yyyy-MM-dd");
            endDate = date2String + " 00:00:00";
        }
        String classTwoStartDate = DateFormatUtil.getString2DateIncreaseOneDay(endDate,null);
        int newLoadWeight = (int)(((Float.parseFloat(planFinishLoad) - initValue)/6) * 4 + initValue);//体重-评估负重 6周平均分配
        PlanEntity planEntity1 = TrainPlanManager.getInstance().insert(newLoadWeight,2,classTwoStartDate,DateFormatUtil.increaseOneDayOneSecondLess(2*7-1,classTwoStartDate),25,6*7);
        TrainPlanManager.getInstance().insert(planEntity1);

        List<SubPlanEntity> subPlanEntityList = SubPlanManager.getInstance().insertTest(startDate,initValue,1,6,planFinishLoad);
        List<SubPlanEntity> modifySubPlanList = modifySubPlanData(subPlanEntityList,25, Global.MinTrainStep,Global.MaxTrainStep);
        SubPlanManager.getInstance().insertMany(modifyStartEndDate(planStartTime,modifySubPlanList));
        return initValue;
    }
    public int generatePlan2(int targetLoad,long planStartTime,String planFinishLoad){
        int initValue = calcInitLoad(targetLoad,planStartTime,planFinishLoad);
        if (initValue == Integer.MAX_VALUE)
            return initValue;
        String startDate = SPHelper.getUser().getDate();
        String endDate = DateFormatUtil.increaseOneDayOneSecondLess(6*7-1,startDate);
        PlanEntity planEntity = TrainPlanManager.getInstance().insert(initValue,1,startDate,endDate,Global.TrainTime,6*7);
        planEntity.setStartDate(DateFormatUtil.getNowDate());
        TrainPlanManager.getInstance().insert(planEntity);

        List<SubPlanEntity> subPlanEntityList = SubPlanManager.getInstance().insertTest(startDate,initValue,1,6,planFinishLoad);
        List<SubPlanEntity> modifySubPlanList = modifySubPlanData(subPlanEntityList,Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
        SubPlanManager.getInstance().insertMany(modifyStartEndDate(planStartTime,modifySubPlanList));
        return initValue;
    }
    public int generatePlan3(int targetLoad,long planStartTime,String planFinishLoad){
        int weight = Integer.parseInt(planFinishLoad);
        List<SubPlanEntity> subPlanEntityList = new ArrayList<>();
        int newLoadWeight1 = (int) (weight * 0.87);
        int newLoadWeight2 = (int) (weight * 0.51);
        float weekDiff = (float)(weight * (0.87-0.51))/11;
        int weekCount = (int) Math.ceil((weight * (1-0.87)/weekDiff));//推算剩余的负重对应结束的时间
        if (weekCount == 0){
            weekCount = 1;
        }
        String startDate = DateFormatUtil.getDate2String(planStartTime,"yyyy-MM-dd");
        startDate = startDate + " 00:00:00";
        if (targetLoad <= newLoadWeight2){
            String endDate = DateFormatUtil.increaseOneDayOneSecondLess(7-1, startDate);
            PlanEntity planEntity = TrainPlanManager.getInstance().insert(targetLoad, 1, startDate, endDate, Global.TrainTime, (12 + weekCount) * 7);
            TrainPlanManager.getInstance().insert(planEntity);
            subPlanEntityList.addAll(SubPlanManager.getInstance().insert3Test(startDate, newLoadWeight2, targetLoad, 1, 1));
            String classTwoStartDate = DateFormatUtil.getString2DateIncreaseOneDay(endDate,null);
            PlanEntity planEntity1 = TrainPlanManager.getInstance().insert(newLoadWeight2, 2, classTwoStartDate, DateFormatUtil.increaseOneDayOneSecondLess(11 * 7-1, classTwoStartDate), Global.TrainTime, (12 + weekCount));
            TrainPlanManager.getInstance().insert(planEntity1);
            subPlanEntityList.addAll(SubPlanManager.getInstance().insert3Test(classTwoStartDate, newLoadWeight1, newLoadWeight2, 2, 11));
            String startDate1 = DateFormatUtil.increaseOneDayOneSecondLess(11 * 7-1, classTwoStartDate);
            String classThreeStartDate = DateFormatUtil.getString2DateIncreaseOneDay(startDate1,null);
            PlanEntity planEntity2 = TrainPlanManager.getInstance().insert(newLoadWeight1, 3, classThreeStartDate, DateFormatUtil.increaseOneDayOneSecondLess(weekCount * 7-1, classThreeStartDate), Global.TrainTime, (12 + weekCount));
            TrainPlanManager.getInstance().insert(planEntity2);
            subPlanEntityList.addAll(SubPlanManager.getInstance().insert3Test(classThreeStartDate, weight, newLoadWeight1, 3, weekCount));
        }else {
            startDate = DateFormatUtil.getBeforeOrAfterDate(7,SPHelper.getUser().getDate());
            int weekNumer = (int) Math.ceil((weight * 0.87 - targetLoad)/weekDiff);//推算剩余的负重对应结束的时间
            if (weekNumer == 0){
                weekNumer = 1;
            }
            int initValue = calcInitLoad3(targetLoad,planStartTime,planFinishLoad);
            PlanEntity planEntity =  TrainPlanManager.getInstance().insert(initValue, 1, startDate, DateFormatUtil.increaseOneDayOneSecondLess(weekNumer * 7-1, startDate), Global.TrainTime, weekNumer);
            TrainPlanManager.getInstance().insert(planEntity);
            subPlanEntityList.addAll(SubPlanManager.getInstance().insert3Test(startDate, newLoadWeight1, initValue, 2, weekNumer));
            String startDate1 = DateFormatUtil.increaseOneDayOneSecondLess(weekNumer * 7-1, startDate);
            PlanEntity planEntity1 = TrainPlanManager.getInstance().insert(newLoadWeight1, 2, startDate1, DateFormatUtil.increaseOneDayOneSecondLess(weekCount * 7-1, startDate1), Global.TrainTime, (12 + weekCount));
            TrainPlanManager.getInstance().insert(planEntity1);
            subPlanEntityList.addAll(SubPlanManager.getInstance().insert3Test(startDate1, weight, newLoadWeight1, 3, weekCount));
        }
        List<SubPlanEntity> modifySubPlanList = modifySubPlanData(subPlanEntityList,Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
        List<SubPlanEntity> subPlanEntities1 = modifyStartEndDate(planStartTime,modifySubPlanList);
        SubPlanManager.getInstance().insertMany(subPlanEntities1);
        if (subPlanEntities1 == null || subPlanEntities1.size() <= 0){
            TrainPlanManager.getInstance().clearTrainPlanDatabaseByUserId(SPHelper.getUserId());
            return Integer.MAX_VALUE;
        }else{
            return 0;
        }

    }
    public int generatePlan4(int targetLoad,long planStartTime,String planFinishLoad){
        int initValue = calcInitLoad4(targetLoad,planStartTime,planFinishLoad);
        if (initValue == Integer.MAX_VALUE)
            return initValue;
        List<SubPlanEntity> subPlanEntityList = new ArrayList<>();
        String startDate = SPHelper.getUser().getDate();
        String endDate = DateFormatUtil.increaseOneDayOneSecondLess(6*7-1,startDate);
        String classTwoStartDate = null;
        if (targetLoad < 20) {
            subPlanEntityList.addAll(SubPlanManager.getInstance().insert2Test(startDate, initValue,1,6));
            PlanEntity planEntity = TrainPlanManager.getInstance().insert(initValue,1,startDate,endDate,Global.TrainTime,6*7);
            if (planStartTime < DateFormatUtil.getString2Date(endDate)){
                String date2String = DateFormatUtil.getDate2String(planStartTime,"yyyy-MM-dd");
                date2String = date2String + " 00:00:00";
                planEntity.setStartDate(date2String);
                TrainPlanManager.getInstance().insert(planEntity);
            }else {
                String date2String = DateFormatUtil.getDate2String(planStartTime,"yyyy-MM-dd");
                endDate = date2String + " 00:00:00";
            }
            classTwoStartDate = DateFormatUtil.getString2DateIncreaseOneDay(endDate,null);
        }else {
            classTwoStartDate = DateFormatUtil.getDate2String(planStartTime,"yyyy-MM-dd");
            classTwoStartDate = classTwoStartDate + " 00:00:00";
        }
        if (initValue < 0)
            initValue = targetLoad;
        PlanEntity planEntity1 = TrainPlanManager.getInstance().insert(initValue,2,classTwoStartDate,DateFormatUtil.increaseOneDayOneSecondLess(10*7-1,classTwoStartDate),Global.TrainTime,10*7);
        TrainPlanManager.getInstance().insert(planEntity1);
        subPlanEntityList.addAll(SubPlanManager.getInstance().insert4Test(classTwoStartDate,Integer.parseInt(planFinishLoad),initValue,2,10));
        List<SubPlanEntity> modifySubPlanList = modifySubPlanData(subPlanEntityList,Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
        SubPlanManager.getInstance().insertMany(modifyStartEndDate(planStartTime,modifySubPlanList));
        return initValue;
    }
    public int generatePlan5(int targetLoad,long planStartTime,String planFinishLoad){
        int initValue = calcInitLoad5(targetLoad,planStartTime,planFinishLoad);
        if (initValue == Integer.MAX_VALUE)
            return initValue;
        String startDate = SPHelper.getUser().getDate();
        PlanEntity planEntity = TrainPlanManager.getInstance().insert(initValue,1,startDate,DateFormatUtil.increaseOneDayOneSecondLess(24*7-1,startDate),Global.TrainTime,24*7);
        String date2String = DateFormatUtil.getDate2String(planStartTime,"yyyy-MM-dd");
        date2String = date2String + " 00:00:00";
        planEntity.setStartDate(date2String);
        TrainPlanManager.getInstance().insert(planEntity);

        List<SubPlanEntity> subPlanEntityList = SubPlanManager.getInstance().insert5Test(startDate,Integer.parseInt(planFinishLoad),initValue,1,24);
        List<SubPlanEntity> modifySubPlanList = modifySubPlanData(subPlanEntityList,Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
        SubPlanManager.getInstance().insertMany(modifyStartEndDate(planStartTime,modifySubPlanList));
        return initValue;
    }
    public int generatePlan6(int targetLoad,long planStartTime,String planFinishLoad){
        int initValue = calcInitLoad6(targetLoad,planStartTime,planFinishLoad);
        if (initValue == Integer.MAX_VALUE)
            return initValue;
        List<SubPlanEntity> subPlanEntityList = new ArrayList<>();
        String startDate = SPHelper.getUser().getDate();
        subPlanEntityList.addAll(SubPlanManager.getInstance().insert2Test(startDate, initValue,1,3));
        String endDate = DateFormatUtil.increaseOneDayOneSecondLess(3*7-1,startDate);
        PlanEntity planEntity = TrainPlanManager.getInstance().insert(initValue,1,startDate,endDate,Global.TrainTime,3*7);
        if (planStartTime < DateFormatUtil.getString2Date(endDate)){
            String date2String = DateFormatUtil.getDate2String(planStartTime,"yyyy-MM-dd");
            date2String = date2String + " 00:00:00";
            planEntity.setStartDate(date2String);
            TrainPlanManager.getInstance().insert(planEntity);
        }else {
//            String date2String = DateFormatUtil.getDate2String(System.currentTimeMillis(),"yyyy-MM-dd");
//            endDate = date2String + " 00:00:00";
//            initValue = targetLoad;
        }
        String classTwoStartDate = DateFormatUtil.getString2DateIncreaseOneDay(endDate,null);
        subPlanEntityList.addAll(SubPlanManager.getInstance().insert5Test(classTwoStartDate,Integer.parseInt(planFinishLoad),initValue,2,24));
        PlanEntity planEntity1 = TrainPlanManager.getInstance().insert(initValue,2,classTwoStartDate,DateFormatUtil.increaseOneDayOneSecondLess(24*7-1,classTwoStartDate),Global.TrainTime,24*7);
        TrainPlanManager.getInstance().insert(planEntity1);

        List<SubPlanEntity> modifySubPlanList = modifySubPlanData(subPlanEntityList,Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
        SubPlanManager.getInstance().insertMany(modifyStartEndDate(planStartTime,modifySubPlanList));
        return initValue;
    }
    public int generatePlan7(int targetLoad,long planStartTime,String planFinishLoad){
        int initValue = calcInitLoad7(targetLoad,planStartTime,planFinishLoad);
        if (initValue == Integer.MAX_VALUE)
            return initValue;
        String startDate = SPHelper.getUser().getDate();
        PlanEntity planEntity = TrainPlanManager.getInstance().insert(initValue,1,startDate,DateFormatUtil.increaseOneDayOneSecondLess(24*7-1,startDate),Global.TrainTime,24*7);
        List<SubPlanEntity> subPlanEntityList = SubPlanManager.getInstance().insert5Test(startDate,Integer.parseInt(planFinishLoad),initValue,1,24);
        TrainPlanManager.getInstance().insert(planEntity);
        List<SubPlanEntity> modifySubPlanList = modifySubPlanData(subPlanEntityList,Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
        SubPlanManager.getInstance().insertMany(modifyStartEndDate(planStartTime,modifySubPlanList));
        return initValue;
    }
    public int generatePlan8(int targetLoad,long planStartTime,String planFinishLoad){
        int initValue = calcInitLoad8(targetLoad,planStartTime,planFinishLoad);
        if (initValue == Integer.MAX_VALUE)
            return initValue;
        String startDate = SPHelper.getUser().getDate();
        PlanEntity planEntity = TrainPlanManager.getInstance().insert(initValue,1,startDate,DateFormatUtil.increaseOneDayOneSecondLess(16*7-1,startDate),Global.TrainTime,16*7);
        List<SubPlanEntity> subPlanEntityList = SubPlanManager.getInstance().insert4Test(startDate,Integer.parseInt(planFinishLoad),initValue,1,16);
        TrainPlanManager.getInstance().insert(planEntity);
        List<SubPlanEntity> modifySubPlanList = modifySubPlanData(subPlanEntityList,Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
        SubPlanManager.getInstance().insertMany(modifyStartEndDate(planStartTime,modifySubPlanList));
        return initValue;
    }
    public int generatePlan9(int targetLoad,long planStartTime,String planFinishLoad){
        List<SubPlanEntity> subPlanEntityList = new ArrayList<>();
        String startZero = SPHelper.getUser().getDate();
        String startDate = DateFormatUtil.getBeforeOrAfterDate(4*7,SPHelper.getUser().getDate());
        List<SubPlanEntity> subPlanEntities = SubPlanManager.getInstance().insert2Test(startZero, 0,1,4);
        List<SubPlanEntity> modifySubPlanEntities= modifySubPlanData(subPlanEntities,Global.TrainTime, 5,20);
        SubPlanManager.getInstance().insertMany(modifyStartEndDate(planStartTime,modifySubPlanEntities));
        String endDate = DateFormatUtil.increaseOneDayOneSecondLess(2*7-1,startDate);
        if (targetLoad < 10){
            subPlanEntityList.addAll(SubPlanManager.getInstance().insert4Test(startDate,10, targetLoad,1,2));
            PlanEntity planEntity = TrainPlanManager.getInstance().insert(10,1,startDate,endDate,Global.TrainTime,2*7);
            TrainPlanManager.getInstance().insert(planEntity);
            String classTwoStartDate = DateFormatUtil.getString2DateIncreaseOneDay(endDate,null);
            PlanEntity planEntity1 = TrainPlanManager.getInstance().insert(10,2,classTwoStartDate,DateFormatUtil.increaseOneDayOneSecondLess(2*7-1,classTwoStartDate),Global.TrainTime,2*7);
            TrainPlanManager.getInstance().insert(planEntity1);
            subPlanEntityList.addAll(SubPlanManager.getInstance().insert4Test(classTwoStartDate,10,10,2,2));
        }else {
//            String classTwoStartDate = DateFormatUtil.getString2DateIncreaseOneDay(endDate,null);
            PlanEntity planEntity1 = TrainPlanManager.getInstance().insert(10,2,endDate,DateFormatUtil.increaseOneDayOneSecondLess(4*7-1,endDate),Global.TrainTime,4*7);
            TrainPlanManager.getInstance().insert(planEntity1);
            subPlanEntityList.addAll(SubPlanManager.getInstance().insert4Test(endDate,10,10,2,4));
        }


        String startDate1 = DateFormatUtil.increaseOneDayOneSecondLess(4*7-1,DateFormatUtil.getString2DateIncreaseOneDay(endDate,null));
        String classThreeStartDate = DateFormatUtil.getString2DateIncreaseOneDay(startDate1,null);
        String endDate1 = DateFormatUtil.increaseOneDayOneSecondLess(2*7-1,classThreeStartDate);
        PlanEntity planEntity2 = TrainPlanManager.getInstance().insert(20,3,classThreeStartDate,endDate1,Global.TrainTime,2*7);
        TrainPlanManager.getInstance().insert(planEntity2);
        subPlanEntityList.addAll(SubPlanManager.getInstance().insert4Test(classThreeStartDate,20,20,3,2));
        String classFourStartDate = DateFormatUtil.getString2DateIncreaseOneDay(endDate1,null);
        int weight = Integer.parseInt(planFinishLoad);
        int weekCount = 0;
        PlanEntity planEntity3;
        if (weight <= 40){
            weekCount = 1;
            planEntity3 = TrainPlanManager.getInstance().insert(weight,4,classFourStartDate,DateFormatUtil.increaseOneDayOneSecondLess(weekCount*7-1,classFourStartDate),Global.TrainTime,weekCount*7);
            subPlanEntityList.addAll(SubPlanManager.getInstance().insert4Test(classFourStartDate,weight,weight,4,weekCount));
        }else {
            weekCount = (weight-40)/10 + 1;
            planEntity3 = TrainPlanManager.getInstance().insert(40,4,classFourStartDate,DateFormatUtil.increaseOneDayOneSecondLess(weekCount*7-1,classFourStartDate),Global.TrainTime,weekCount*7);
            subPlanEntityList.addAll(SubPlanManager.getInstance().insert4Test(classFourStartDate,weight,40,4,weekCount));
        }
        TrainPlanManager.getInstance().insert(planEntity3);
        List<SubPlanEntity> modifySubPlanList = modifySubPlanData(subPlanEntityList,Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
        List<SubPlanEntity> subPlanEntities1 = modifyStartEndDate(planStartTime,modifySubPlanList);
        SubPlanManager.getInstance().insertManyTest(subPlanEntities1);
        if (subPlanEntities1 == null || subPlanEntities1.size() <= 0){
            TrainPlanManager.getInstance().clearTrainPlanDatabaseByUserId(SPHelper.getUserId());
            return Integer.MAX_VALUE;
        }else{
            return 0;
        }
    }
    public int generatePlan10(int targetLoad,long planStartTime,String planFinishLoad){
        int initValue = calcInitLoad10(targetLoad,planStartTime,planFinishLoad);
        if (initValue == Integer.MAX_VALUE)
            return initValue;
        List<SubPlanEntity> subPlanEntityList = new ArrayList<>();
        String startDate = SPHelper.getUser().getDate();
        String endDate = DateFormatUtil.increaseOneDayOneSecondLess(4*7-1,startDate);
        String classTwoStartDate = DateFormatUtil.getString2DateIncreaseOneDay(endDate,null);
        if (initValue < 5) {
            initValue = 5;
            subPlanEntityList.addAll(SubPlanManager.getInstance().insert4Test(startDate,initValue,initValue,1,4));
            PlanEntity planEntity = TrainPlanManager.getInstance().insert(initValue,1,startDate,endDate,Global.TrainTime,4*7);
            if (planStartTime < DateFormatUtil.getString2Date(endDate)){
                String date2String = DateFormatUtil.getDate2String(planStartTime,"yyyy-MM-dd");
                date2String = date2String + " 00:00:00";
                planEntity.setStartDate(date2String);
                TrainPlanManager.getInstance().insert(planEntity);
            }else {
//                String date2String = DateFormatUtil.getDate2String(System.currentTimeMillis(),"yyyy-MM-dd");
//                endDate = date2String + " 00:00:00";
//                classTwoStartDate = DateFormatUtil.getString2DateIncreaseOneDay(endDate,null);
            }

        }else {
            classTwoStartDate = DateFormatUtil.getDate2String(planStartTime,"yyyy-MM-dd");
            classTwoStartDate = classTwoStartDate + " 00:00:00";
        }
        PlanEntity planEntity1 = TrainPlanManager.getInstance().insert(initValue,2,classTwoStartDate,DateFormatUtil.increaseOneDayOneSecondLess(12*7-1,classTwoStartDate),Global.TrainTime,12*7);
        TrainPlanManager.getInstance().insert(planEntity1);
        subPlanEntityList.addAll(SubPlanManager.getInstance().insert4Test(classTwoStartDate,Integer.parseInt(planFinishLoad),initValue,2,12));
        List<SubPlanEntity> modifySubPlanList = modifySubPlanData(subPlanEntityList,Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
        SubPlanManager.getInstance().insertMany(modifyStartEndDate(planStartTime,modifySubPlanList));
        return initValue;
    }
    public int generatePlan11(int targetLoad,long planStartTime,String planFinishLoad){
        int initValue = calcInitLoad11(targetLoad,planStartTime,planFinishLoad);
        if (initValue == Integer.MAX_VALUE)
            return initValue;
        List<SubPlanEntity> subPlanEntityList = new ArrayList<>();
        String startDate = SPHelper.getUser().getDate();
        String endDate = DateFormatUtil.increaseOneDayOneSecondLess(7*7-1,startDate);
        String classTwoStartDate = null;
        if (targetLoad < 12) {
            subPlanEntityList.addAll(SubPlanManager.getInstance().insert4Test(startDate,12,initValue,1,7));
            PlanEntity planEntity = TrainPlanManager.getInstance().insert(targetLoad,1,startDate,endDate,Global.TrainTime,7*7);
            if (planStartTime < DateFormatUtil.getString2Date(endDate)){
                String date2String = DateFormatUtil.getDate2String(planStartTime,"yyyy-MM-dd");
                date2String = date2String + " 00:00:00";
                planEntity.setStartDate(date2String);
                TrainPlanManager.getInstance().insert(planEntity);
            }else {
                String date2String = DateFormatUtil.getDate2String(planStartTime,"yyyy-MM-dd");
                endDate = date2String + " 00:00:00";
            }
            classTwoStartDate = DateFormatUtil.getString2DateIncreaseOneDay(endDate,null);
            initValue = 12;
        }else {
            classTwoStartDate = DateFormatUtil.getDate2String(planStartTime,"yyyy-MM-dd");
            classTwoStartDate = classTwoStartDate + " 00:00:00";
        }
        int weight = Integer.parseInt(planFinishLoad);
        int diff = 5;
        int weekCount = ((weight - 12)/diff)*2;
        PlanEntity planEntity1 = TrainPlanManager.getInstance().insert(12,2,classTwoStartDate,DateFormatUtil.increaseOneDayOneSecondLess(weekCount*7-1,classTwoStartDate),Global.TrainTime,weekCount*7);
        TrainPlanManager.getInstance().insert(planEntity1);
        subPlanEntityList.addAll(SubPlanManager.getInstance().insert5Test(classTwoStartDate,weight,initValue,2,weekCount));
        List<SubPlanEntity> modifySubPlanList = modifySubPlanData(subPlanEntityList,Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
        SubPlanManager.getInstance().insertMany(modifyStartEndDate(planStartTime,modifySubPlanList));
        return initValue;
    }
    public int generatePlanJieGu(int targetLoad,long planStartTime,String planFinishLoad){
        List<SubPlanEntity> subPlanEntityList = new ArrayList<>();
        String startDate = SPHelper.getUser().getDate();
        targetLoad = 5;
        String endDate = DateFormatUtil.increaseOneDayOneSecondLess(5*7-1,startDate);
        PlanEntity planEntity = TrainPlanManager.getInstance().insert(targetLoad,1,startDate,endDate,Global.TrainTime,5*7);
        String classTwoStartDate = DateFormatUtil.getString2DateIncreaseOneDay(endDate,null);
        PlanEntity planEntity1 = TrainPlanManager.getInstance().insert(10,2,classTwoStartDate,DateFormatUtil.increaseOneDayOneSecondLess(7*7-1,classTwoStartDate),Global.TrainTime,7*7);
        TrainPlanManager.getInstance().insert(planEntity);
        TrainPlanManager.getInstance().insert(planEntity1);
        subPlanEntityList.addAll(TrainPlanManager.insertListJieGuTest(targetLoad,planFinishLoad));
        List<SubPlanEntity> modifySubPlanList = modifySubPlanData(subPlanEntityList,Global.TrainTime, Global.MinTrainStep,Global.MaxTrainStep);
        List<SubPlanEntity> subPlanEntities1 = modifyStartEndDate(System.currentTimeMillis(),modifySubPlanList);
        SubPlanManager.getInstance().insertMany(subPlanEntities1);
        if (subPlanEntities1 == null || subPlanEntities1.size() <= 0){
            TrainPlanManager.getInstance().clearTrainPlanDatabaseByUserId(SPHelper.getUserId());
            return Integer.MAX_VALUE;
        }else{
            return 0;
        }

    }
    @TargetApi(Build.VERSION_CODES.N)
    public int generateDefaultPlan(String startDate, String startLoad, String endDate, String endLoad,long planStartTime, int initStep){
        List<SubPlanEntity> subPlanEntityList = new ArrayList<>();
//        endDate = DateFormatUtil.increaseOneDayOneSecondLess(0,endDate);
        long startTime = DateFormatUtil.getString2Date(startDate);
        long endTime = DateFormatUtil.getString2Date(endDate);
        int weekCount = (int) Math.ceil(((endTime-startTime) / (7.0 * dayMs)));
        if (weekCount == 0 || weekCount == 1)
            weekCount = 2;
        PlanEntity planEntity = TrainPlanManager.getInstance().insert(Integer.parseInt(startLoad),1,startDate,endDate,Global.TrainTime,weekCount*7);
        TrainPlanManager.getInstance().insert(planEntity);
        subPlanEntityList.addAll(SubPlanManager.getInstance().insert5Test(startDate,Integer.parseInt(endLoad),Integer.parseInt(startLoad),1,weekCount));
        List<SubPlanEntity> modifySubPlanList = modifySubPlanData(subPlanEntityList,Global.TrainTime, initStep,Global.MaxTrainStep);
        SubPlanManager.getInstance().insertMany(modifyStartEndDate(planStartTime,modifySubPlanList));
        return 0;
    }
    public List<SubPlanEntity> modifySubPlanData( List<SubPlanEntity> subPlanEntityList,int defaultTrainTime,int minTrainStep,int maxTrainStep){//子计划增加训练步数和训练时间
        List<SubPlanEntity> planEntityList = new ArrayList<>();
        if (subPlanEntityList.size() <= 1)
            return subPlanEntityList;
        if (minTrainStep >= maxTrainStep){
            minTrainStep = maxTrainStep;
        }
        float diffPerStep = (float) ((maxTrainStep*1.0 - minTrainStep*1.0)/(subPlanEntityList.size() -1));
        for (int i=0; i<subPlanEntityList.size(); i++){
            SubPlanEntity subPlanEntity = subPlanEntityList.get(i);
            int trainStep;
            if (i == subPlanEntityList.size() -1){
                trainStep = maxTrainStep;
            }else {
                trainStep = minTrainStep + Math.round(diffPerStep*i);
            }
            subPlanEntity.setTrainStep(trainStep);
            int trainTime = (int) Math.ceil(trainStep*1.0/Global.TrainCountMinute);
            if (trainTime < defaultTrainTime){
                trainTime = defaultTrainTime;
            }
//            if (subPlanEntity.getLoad() < 0){
//                subPlanEntity.setLoad(0);
//            }
            subPlanEntity.setTrainTime(trainTime);
            subPlanEntity.setModifyStatus(0);
            planEntityList.add(subPlanEntity);
        }
        return planEntityList;
    }
    @TargetApi(Build.VERSION_CODES.N)
    private List<SubPlanEntity> modifyStartEndDate(long startTime, List<SubPlanEntity> list){
        List<SubPlanEntity> listResult = new ArrayList<>();
        int i = 0;
        String startDate = DateFormatUtil.getDate2String(startTime,"yyyy-MM-dd");
        startDate = startDate + " 00:00:00";
        for (SubPlanEntity subPlanEntity:list){
            if (startTime <= DateFormatUtil.getString2Date(subPlanEntity.getEndDate())){
//                int diffDay = Math.toIntExact((startTime - DateFormatUtil.getString2Date(subPlanEntity.getStartDate())) / dayMs) + 1;
//                subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i*7,startDate));
//                subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate((i+1)*7,startDate));
//                i++;
                if (subPlanEntity.getLoad() <= 0)
                    subPlanEntity.setLoad(2);
                listResult.add(subPlanEntity);
            }
        }
        return listResult;
    }
}
