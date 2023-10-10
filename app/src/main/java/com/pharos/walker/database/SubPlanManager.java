package com.pharos.walker.database;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.pharos.walker.beans.SubPlanEntity;
import com.pharos.walker.constants.Global;
import com.pharos.walker.greendao.SubPlanEntityDao;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.GreenDaoHelper;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.SnowflakeIdUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanglun on 2021/5/7
 * Describe:
 */
public class SubPlanManager {
    private static volatile SubPlanManager instance = null;

    private SubPlanEntityDao subPlanEntityDao;
    private static final long dayMs = 24 * 60 * 60 * 1000;

    private SubPlanManager() {
        subPlanEntityDao = GreenDaoHelper.getDaoSession().getSubPlanEntityDao();
    }

    public static SubPlanManager getInstance() {
        if (instance == null) {
            synchronized (SubPlanManager.class) {
                if (instance == null) {
                    instance = new SubPlanManager();
                }
            }
        }
        return instance;
    }
    void update(SubPlanEntity subPlanEntity){
        subPlanEntityDao.update(subPlanEntity);
    }
    public List<SubPlanEntity> loadDataByUserId(long userId){
        return subPlanEntityDao.queryBuilder().where(SubPlanEntityDao.Properties.UserId.eq(userId)).orderAsc(SubPlanEntityDao.Properties.StartDate).list();
    }
    public List<SubPlanEntity> loadAll(){
        return subPlanEntityDao.loadAll();
    }
    public List<SubPlanEntity> loadDataByPlainId(long userId,long plainId){
        return subPlanEntityDao.queryBuilder().where(SubPlanEntityDao.Properties.UserId.eq(userId),SubPlanEntityDao.Properties.PlanId.eq(plainId)).list();
    }
    public void clearPlanByUserId(long userId){
        subPlanEntityDao.deleteInTx(loadDataByUserId(userId));
    }
    public void insertMany(List<SubPlanEntity> subPlanEntityList){
        if (subPlanEntityList != null && subPlanEntityList.size() > 0){
            clearPlanByUserId(subPlanEntityList.get(0).getUserId());
            subPlanEntityDao.insertOrReplaceInTx(subPlanEntityList);
        }
    }
    public void insert(List<SubPlanEntity> subPlanEntityList){
        if (subPlanEntityList == null)
            return;
        for (SubPlanEntity subPlanEntity : subPlanEntityList){
            subPlanEntity.setId(null);
            subPlanEntityDao.insertOrReplace(subPlanEntity);
        }
    }
    public void insertManyTest(List<SubPlanEntity> subPlanEntityList){
        if (subPlanEntityList != null && subPlanEntityList.size() > 0){
            subPlanEntityDao.insertOrReplaceInTx(subPlanEntityList);
        }
    }
    public void clearAllSubTrainPlan(){
        subPlanEntityDao.deleteAll();
    }
    public List<SubPlanEntity> insertTest(String startDate, int loadWeight, int classId, int weekTotal,String planFinishLoad){
        int diff = Integer.parseInt(planFinishLoad) - loadWeight;
        Log.e("Insert", "insert loadWeight: " +loadWeight);
//        int diff = Integer.parseInt(SPHelper.getUser().getWeight()) - (int)SPHelper.getUserEvaluateWeight();
        List<SubPlanEntity> subPlanEntityList = new ArrayList<>();
        for (int i = 0; i < weekTotal; i++) {
            SubPlanEntity subPlanEntity = new SubPlanEntity();
            subPlanEntity.setKeyId(SnowflakeIdUtil.getUniqueId());
            subPlanEntity.setClassId(classId);
            subPlanEntity.setUserId(SPHelper.getUserId());
            subPlanEntity.setLoad(loadWeight + diff * i/(weekTotal - 1));
            subPlanEntity.setWeekNum(i+1);
            subPlanEntity.setDayNum(0);
            subPlanEntity.setPlanStatus(0);
            subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i*7,startDate));
            if (i == weekTotal-1){
                subPlanEntity.setEndDate(DateFormatUtil.increaseOneDayOneSecondLess((i+1)*7-1,startDate));
            }else {
                subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate((i+1)*7,startDate));
            }
            Log.e("Insert", "insert load: " + subPlanEntity.getLoad());
//            subPlanEntityDao.insertOrReplace(subPlanEntity);
            subPlanEntityList.add(subPlanEntity);
        }
        return subPlanEntityList;
    }
    public void insert(String startDate,int loadWeight,int classId, int weekTotal){
        int diff = Integer.parseInt(SPHelper.getUser().getWeight()) - loadWeight;
        Log.e("Insert", "insert loadWeight: " +loadWeight);
//        int diff = Integer.parseInt(SPHelper.getUser().getWeight()) - (int)SPHelper.getUserEvaluateWeight();
        List<SubPlanEntity> subPlanEntityList = subPlanEntityDao.queryBuilder().where(SubPlanEntityDao.Properties.UserId.eq(SPHelper.getUserId()),SubPlanEntityDao.Properties.ClassId.eq(classId)).list();
        for (int i = 0; i < weekTotal; i++) {
            SubPlanEntity subPlanEntity = null;
            if (subPlanEntityList.size() > 0){
                subPlanEntity = subPlanEntityList.get(i);
            }
            if (subPlanEntity == null){
                subPlanEntity = new SubPlanEntity();
                subPlanEntity.setKeyId(SnowflakeIdUtil.getUniqueId());
            }
            subPlanEntity.setClassId(classId);
            subPlanEntity.setUserId(SPHelper.getUserId());
            subPlanEntity.setLoad(loadWeight + diff * i/(weekTotal - 1));
            subPlanEntity.setWeekNum(i+1);
            subPlanEntity.setDayNum(0);
            subPlanEntity.setPlanStatus(0);
            subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i*7,startDate));
            if (i == weekTotal-1){
                subPlanEntity.setEndDate(DateFormatUtil.increaseOneDayOneSecondLess((i+1)*7-1,startDate));
            }else {
                subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate((i+1)*7,startDate));
            }
            Log.e("Insert", "insert load: " + subPlanEntity.getLoad());
            subPlanEntityDao.insertOrReplace(subPlanEntity);
        }
    }
    public void insert2(String startDate,int loadWeight,int classId, int weekTotal){
        List<SubPlanEntity> subPlanEntityList = subPlanEntityDao.queryBuilder().where(SubPlanEntityDao.Properties.UserId.eq(SPHelper.getUserId()),SubPlanEntityDao.Properties.ClassId.eq(classId)).list();
        for (int i = 0; i < weekTotal; i++) {
            SubPlanEntity subPlanEntity = null;
            if (subPlanEntityList.size() > 0){
                subPlanEntity = subPlanEntityList.get(i);
            }
            if (subPlanEntity == null){
                subPlanEntity = new SubPlanEntity();
                subPlanEntity.setKeyId(SnowflakeIdUtil.getUniqueId());
            }
            subPlanEntity.setClassId(classId);
            subPlanEntity.setUserId(SPHelper.getUserId());
            subPlanEntity.setLoad(loadWeight);
            subPlanEntity.setWeekNum(i+1);
            subPlanEntity.setDayNum(0);
            subPlanEntity.setPlanStatus(0);
            subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i*7,startDate) );
            if (i == weekTotal-1){
                subPlanEntity.setEndDate(DateFormatUtil.increaseOneDayOneSecondLess((i+1)*7-1,startDate));
            }else {
                subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate((i+1)*7,startDate));
            }
            subPlanEntityDao.insertOrReplace(subPlanEntity);
        }
    }
    public List<SubPlanEntity> insert2Test(String startDate,int loadWeight,int classId, int weekTotal){
        List<SubPlanEntity> subPlanEntityList = new ArrayList<>();
        for (int i = 0; i < weekTotal; i++) {
            SubPlanEntity subPlanEntity = new SubPlanEntity();
            subPlanEntity.setKeyId(SnowflakeIdUtil.getUniqueId());
            subPlanEntity.setClassId(classId);
            subPlanEntity.setUserId(SPHelper.getUserId());
            subPlanEntity.setLoad(loadWeight);
            subPlanEntity.setWeekNum(i+1);
            subPlanEntity.setDayNum(0);
            subPlanEntity.setPlanStatus(0);
            subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i*7,startDate) );
            if (i == weekTotal-1){
                subPlanEntity.setEndDate(DateFormatUtil.increaseOneDayOneSecondLess((i+1)*7-1,startDate));
            }else {
                subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate((i+1)*7,startDate));
            }
            subPlanEntityList.add(subPlanEntity);
        }
        return subPlanEntityList;
    }
    public void insert3(String startDate,int desWeight,int loadWeight,int classId, int weekTotal){
        List<SubPlanEntity> subPlanEntityList = subPlanEntityDao.queryBuilder().where(SubPlanEntityDao.Properties.UserId.eq(SPHelper.getUserId()),SubPlanEntityDao.Properties.ClassId.eq(classId)).list();
        for (int i = 0; i < weekTotal; i++) {
            SubPlanEntity subPlanEntity = null;
            if (subPlanEntityList.size() > 0){
                subPlanEntity = subPlanEntityList.get(i);
            }
            if (subPlanEntity == null){
                subPlanEntity = new SubPlanEntity();
                subPlanEntity.setKeyId(SnowflakeIdUtil.getUniqueId());
            }
            subPlanEntity.setClassId(classId);
            subPlanEntity.setUserId(SPHelper.getUserId());
            if (weekTotal <= 1){
                subPlanEntity.setLoad(loadWeight);
            }else {
                subPlanEntity.setLoad(loadWeight + (desWeight - loadWeight) * i / (weekTotal - 1));
            }
            subPlanEntity.setPlanStatus(0);
            if (classId == 1){
                subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i,startDate));
                if (i == weekTotal-1){
                    subPlanEntity.setEndDate(DateFormatUtil.increaseOneDayOneSecondLess((i+1)*7-1,startDate));
                }else {
                    subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate((i+1)*7,startDate));
                }
                subPlanEntity.setWeekNum(1);
//                subPlanEntity.setDayNum(i+1);
            }else {
                subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i*7,startDate));
                if (i == weekTotal-1){
                    subPlanEntity.setEndDate(DateFormatUtil.increaseOneDayOneSecondLess((i+1)*7-1,startDate));
                }else {
                    subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate((i+1)*7,startDate));
                }
                subPlanEntity.setWeekNum(i+1);
                subPlanEntity.setDayNum(0);
            }
            subPlanEntityDao.insertOrReplace(subPlanEntity);
        }
    }
    public List<SubPlanEntity> insert3Test(String startDate,int desWeight,int loadWeight,int classId, int weekTotal){
        List<SubPlanEntity> subPlanEntityList = new ArrayList<>();
        for (int i = 0; i < weekTotal; i++) {
            SubPlanEntity subPlanEntity = new SubPlanEntity();
            subPlanEntity.setKeyId(SnowflakeIdUtil.getUniqueId());
            subPlanEntity.setClassId(classId);
            subPlanEntity.setUserId(SPHelper.getUserId());
            if (weekTotal <= 1){
                subPlanEntity.setLoad(loadWeight);
            }else {
                subPlanEntity.setLoad(loadWeight + (desWeight - loadWeight) * i / (weekTotal - 1));
            }
            subPlanEntity.setPlanStatus(0);
            if (classId == 1){
                subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i,startDate));
                if (i == weekTotal-1){
                    subPlanEntity.setEndDate(DateFormatUtil.increaseOneDayOneSecondLess((i+1)*7-1,startDate));
                }else {
                    subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate((i+1)*7,startDate));
                }
                subPlanEntity.setWeekNum(1);
//                subPlanEntity.setDayNum(i+1);
            }else {
                subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i*7,startDate));
                if (i == weekTotal-1){
                    subPlanEntity.setEndDate(DateFormatUtil.increaseOneDayOneSecondLess((i+1)*7-1,startDate));
                }else {
                    subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate((i+1)*7,startDate));
                }
                subPlanEntity.setWeekNum(i+1);
                subPlanEntity.setDayNum(0);
            }
            subPlanEntityList.add(subPlanEntity);
//            subPlanEntityDao.insertOrReplace(subPlanEntity);
        }
        return subPlanEntityList;
    }
    public void insert4(String startDate, int desWeight, int loadWeight,int classId, int weekTotal){
        List<SubPlanEntity> subPlanEntityList = subPlanEntityDao.queryBuilder().where(SubPlanEntityDao.Properties.UserId.eq(SPHelper.getUserId()),SubPlanEntityDao.Properties.ClassId.eq(classId)).list();
        for (int i = 0; i < weekTotal; i++) {
            SubPlanEntity subPlanEntity = null;
            if (subPlanEntityList.size() > 0){
                subPlanEntity = subPlanEntityList.get(i);
            }
            if (subPlanEntity == null){
                subPlanEntity = new SubPlanEntity();
                subPlanEntity.setKeyId(SnowflakeIdUtil.getUniqueId());
            }

            subPlanEntity.setClassId(classId);
            subPlanEntity.setUserId(SPHelper.getUserId());
            if (weekTotal == 1){
                subPlanEntity.setLoad(loadWeight + (desWeight - loadWeight) * i);
            }else {
                subPlanEntity.setLoad(loadWeight + (desWeight - loadWeight) * i /(weekTotal - 1));
            }
            subPlanEntity.setWeekNum(i+1);
            subPlanEntity.setDayNum(0);
            subPlanEntity.setPlanStatus(0);
            subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i*7,startDate));
            if (i == weekTotal-1){
                subPlanEntity.setEndDate(DateFormatUtil.increaseOneDayOneSecondLess((i+1)*7-1,startDate));
            }else {
                subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate((i+1)*7,startDate));
            }
            subPlanEntity.setKeyId(SnowflakeIdUtil.getUniqueId());
            subPlanEntityDao.insertOrReplace(subPlanEntity);
        }
    }
    public List<SubPlanEntity> insert4Test(String startDate, int desWeight, int loadWeight,int classId, int weekTotal){
        List<SubPlanEntity> subPlanEntityList = new ArrayList<>();
        for (int i = 0; i < weekTotal; i++) {
            SubPlanEntity subPlanEntity = new SubPlanEntity();
            subPlanEntity.setKeyId(SnowflakeIdUtil.getUniqueId());
            subPlanEntity.setClassId(classId);
            subPlanEntity.setUserId(SPHelper.getUserId());
            if (weekTotal == 1){
                subPlanEntity.setLoad(loadWeight + (desWeight - loadWeight) * i);
            }else {
                subPlanEntity.setLoad(loadWeight + (desWeight - loadWeight) * i /(weekTotal - 1));
            }
            subPlanEntity.setWeekNum(i+1);
            subPlanEntity.setDayNum(0);
            subPlanEntity.setPlanStatus(0);
            subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i*7,startDate));
            if (i == weekTotal-1){
                subPlanEntity.setEndDate(DateFormatUtil.increaseOneDayOneSecondLess((i+1)*7-1,startDate));
            }else {
                subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate((i+1)*7,startDate));
            }
            subPlanEntity.setKeyId(SnowflakeIdUtil.getUniqueId());
            subPlanEntityList.add(subPlanEntity);
        }
        return subPlanEntityList;
    }
    public void insert5(String startDate,int desWeight,int loadWeight,int classId, int weekTotal){
        List<SubPlanEntity> subPlanEntityList = subPlanEntityDao.queryBuilder().where(SubPlanEntityDao.Properties.UserId.eq(SPHelper.getUserId()),SubPlanEntityDao.Properties.ClassId.eq(classId)).list();
//        if (weekTotal/2 == 1)
//            weekTotal = weekTotal + 1;
        for (int i = 0; i < weekTotal; i++) {
            SubPlanEntity subPlanEntity = null;
            if (subPlanEntityList.size() > 0){
                subPlanEntity = subPlanEntityList.get(i);
            }
            if (subPlanEntity == null){
                subPlanEntity = new SubPlanEntity();
                subPlanEntity.setKeyId(SnowflakeIdUtil.getUniqueId());
            }
            subPlanEntity.setClassId(classId);
            subPlanEntity.setUserId(SPHelper.getUserId());
            if (weekTotal == 1){
                subPlanEntity.setLoad(loadWeight + (desWeight - loadWeight) * i);
            }else {
                subPlanEntity.setLoad(loadWeight + (desWeight - loadWeight) * i /(weekTotal - 1));
            }
            subPlanEntity.setWeekNum(i+1);
            subPlanEntity.setDayNum(0);
            subPlanEntity.setPlanStatus(0);
            subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i*7,startDate));
            if (i == weekTotal-1){
                subPlanEntity.setEndDate(DateFormatUtil.increaseOneDayOneSecondLess((i+1)*7-1,startDate));
            }else {
                subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate((i+1)*7,startDate));
            }
            subPlanEntityDao.insertOrReplace(subPlanEntity);
//            subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i*14 + 7,startDate));
//            subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate((i+1)*14,startDate));
//            subPlanEntity.setWeekNum(i*2+2);
//            subPlanEntityDao.insertOrReplace(subPlanEntity);
        }
    }
    public List<SubPlanEntity> insert5Test(String startDate,int desWeight,int loadWeight,int classId, int weekTotal){
        List<SubPlanEntity> subPlanEntityList = new ArrayList<>();
//        if (weekTotal/2 == 1)
//            weekTotal = weekTotal + 1;
        for (int i = 0; i < weekTotal; i++) {
            SubPlanEntity subPlanEntity = new SubPlanEntity();
            subPlanEntity.setKeyId(SnowflakeIdUtil.getUniqueId());
            subPlanEntity.setClassId(classId);
            subPlanEntity.setUserId(SPHelper.getUserId());
            if (weekTotal == 1){
                subPlanEntity.setLoad(loadWeight + (desWeight - loadWeight) * i);
            }else {
                subPlanEntity.setLoad(loadWeight + (desWeight - loadWeight) * i /(weekTotal - 1));
            }
            subPlanEntity.setWeekNum(i+1);
            subPlanEntity.setDayNum(0);
            subPlanEntity.setPlanStatus(0);
            subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i*7,startDate));
            if (i == weekTotal-1){
                subPlanEntity.setEndDate(DateFormatUtil.increaseOneDayOneSecondLess((i+1)*7-1,startDate));
            }else {
                subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate((i+1)*7,startDate));
            }
            subPlanEntityList.add(subPlanEntity);
        }
        return subPlanEntityList;
    }
    public void insertJieGu(String startDate,int desWeight,int loadWeight,int classId, int weekTotal){
        List<SubPlanEntity> subPlanEntityList = subPlanEntityDao.queryBuilder().where(SubPlanEntityDao.Properties.UserId.eq(SPHelper.getUserId()),SubPlanEntityDao.Properties.ClassId.eq(classId)).list();
//        if (weekTotal/2 == 1)
//            weekTotal = weekTotal + 1;
        for (int i = 0; i < weekTotal; i++) {
            SubPlanEntity subPlanEntity = null;
            if (subPlanEntityList.size() > 0){
                subPlanEntity = subPlanEntityList.get(i);
            }
            if (subPlanEntity == null){
                subPlanEntity = new SubPlanEntity();
                subPlanEntity.setKeyId(SnowflakeIdUtil.getUniqueId());
            }
            subPlanEntity.setClassId(classId);
            subPlanEntity.setUserId(SPHelper.getUserId());
            if (weekTotal == 1){
                subPlanEntity.setLoad(loadWeight + (desWeight - loadWeight) * i);
            }else {
                subPlanEntity.setLoad(loadWeight + (desWeight - loadWeight) * i /(weekTotal - 1));
            }
            subPlanEntity.setWeekNum(i+1);
            subPlanEntity.setDayNum(0);
            subPlanEntity.setPlanStatus(0);
            subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i*7,startDate));
            if (i == weekTotal-1){
                subPlanEntity.setEndDate(DateFormatUtil.increaseOneDayOneSecondLess((i+1)*7-1,startDate));
            }else {
                subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate((i+1)*7,startDate));
            }
            subPlanEntityDao.insertOrReplace(subPlanEntity);
        }
    }
    public List<SubPlanEntity> insertJieGuTest(String startDate,int desWeight,int loadWeight,int classId, int weekTotal){
        List<SubPlanEntity> subPlanEntityList = new ArrayList<>();
//        if (weekTotal/2 == 1)
//            weekTotal = weekTotal + 1;
        for (int i = 0; i < weekTotal; i++) {
            SubPlanEntity subPlanEntity = new SubPlanEntity();
            subPlanEntity.setKeyId(SnowflakeIdUtil.getUniqueId());
            subPlanEntity.setClassId(classId);
            subPlanEntity.setUserId(SPHelper.getUserId());
            if (weekTotal == 1){
                subPlanEntity.setLoad(loadWeight + (desWeight - loadWeight) * i);
            }else {
                subPlanEntity.setLoad(loadWeight + (desWeight - loadWeight) * i /(weekTotal - 1));
            }
            subPlanEntity.setWeekNum(i+1);
            subPlanEntity.setDayNum(0);
            subPlanEntity.setPlanStatus(0);
            subPlanEntity.setStartDate(DateFormatUtil.getBeforeOrAfterDate(i*7,startDate));
            if (i == weekTotal-1){
                subPlanEntity.setEndDate(DateFormatUtil.increaseOneDayOneSecondLess((i+1)*7-1,startDate));
            }else {
                subPlanEntity.setEndDate(DateFormatUtil.getBeforeOrAfterDate((i+1)*7,startDate));
            }
            subPlanEntityList.add(subPlanEntity);
        }
        return subPlanEntityList;
    }
    public int getThisWeekLoad(long userId){
        List<SubPlanEntity> subPlanEntityList = subPlanEntityDao.queryBuilder().where(SubPlanEntityDao.Properties.UserId.eq(userId)).list();
        for (SubPlanEntity subPlanEntity : subPlanEntityList){
            if (System.currentTimeMillis() >= DateFormatUtil.getString2Date(subPlanEntity.getStartDate()) &&
                    System.currentTimeMillis() < DateFormatUtil.getString2Date(subPlanEntity.getEndDate())){
                subPlanEntity.setPlanStatus(1);
                return subPlanEntity.getLoad();
            }else if (System.currentTimeMillis() < DateFormatUtil.getString2Date(subPlanEntity.getStartDate())){
                subPlanEntity.setPlanStatus(0);
            }else {
                subPlanEntity.setPlanStatus(2);
            }
            update(subPlanEntity);
        }
        return 0;
    }
    public SubPlanEntity getThisWeekLoadEntity(long userId){
        List<SubPlanEntity> subPlanEntityList = loadDataByUserId(userId);
        for (SubPlanEntity subPlanEntity : subPlanEntityList){
            if (System.currentTimeMillis() >= DateFormatUtil.getString2Date(subPlanEntity.getStartDate()) &&
                    System.currentTimeMillis() < DateFormatUtil.getString2Date(subPlanEntity.getEndDate())){
                subPlanEntity.setPlanStatus(1);
                return subPlanEntity;
            }
        }
        return null;
    }
    @TargetApi(Build.VERSION_CODES.N)
    public SubPlanEntity getThisDayLoadEntity(long userId){
        List<SubPlanEntity> subPlanEntityList = loadDataByUserId(userId);
        SubPlanEntity currentEntity = null;
        SubPlanEntity nextEntity = null;
        for (int i = 0; i < subPlanEntityList.size(); i++){
            SubPlanEntity subPlanEntity = subPlanEntityList.get(i);
            if (System.currentTimeMillis() >= DateFormatUtil.getString2Date(subPlanEntity.getStartDate()) &&
                    System.currentTimeMillis() < DateFormatUtil.getString2Date(subPlanEntity.getEndDate())){
                subPlanEntity.setPlanStatus(1);
                currentEntity = new SubPlanEntity();
                currentEntity.setKeyId(subPlanEntity.getKeyId());
                currentEntity.setLoad(subPlanEntity.getLoad());
                currentEntity.setPlanStatus(subPlanEntity.getPlanStatus());
                currentEntity.setModifyStatus(subPlanEntity.getModifyStatus());
                currentEntity.setTrainStep(subPlanEntity.getTrainStep());
                currentEntity.setTrainTime(subPlanEntity.getTrainTime());
                currentEntity.setClassId(subPlanEntity.getClassId());
                currentEntity.setDayNum(subPlanEntity.getDayNum());
                currentEntity.setEndDate(subPlanEntity.getEndDate());
                currentEntity.setId(subPlanEntity.getId());
                currentEntity.setPlanId(subPlanEntity.getPlanId());
                currentEntity.setStartDate(subPlanEntity.getStartDate());
                currentEntity.setUserId(subPlanEntity.getUserId());
                currentEntity.setWeekNum(subPlanEntity.getWeekNum());
                if ((i + 1) < subPlanEntityList.size()){
                    nextEntity = subPlanEntityList.get(i+1);
                }
                break;
            }
        }
        int dayCount;
        float diffWeekLoad;
        float diffWeekStep;
        if (currentEntity != null){
            dayCount = Math.toIntExact((DateFormatUtil.getDateDiff(currentEntity.getStartDate(), currentEntity.getEndDate()) / dayMs));
            Log.e("subPlan", "getThisDayLoadEntity: " + dayCount);
            if (dayCount > 1 && nextEntity != null){
                diffWeekLoad = nextEntity.getLoad() - currentEntity.getLoad();
                diffWeekStep = nextEntity.getTrainStep() - currentEntity.getTrainStep();
                float diffLoad = diffWeekLoad / dayCount;
                float diffStep = diffWeekStep / dayCount;
                long startIntervalDate = System.currentTimeMillis() - DateFormatUtil.getString2Date(currentEntity.getStartDate());
                double currentDays = Math.ceil(startIntervalDate* 1.0f/dayMs);
                long load = Math.round((currentDays -1) * diffLoad);
                long step = Math.round((currentDays -1) * diffStep);
                currentEntity.setLoad((int) (currentEntity.getLoad() + load));
                currentEntity.setTrainStep((int) (currentEntity.getTrainStep() + step));
                currentEntity.setKeyId(SnowflakeIdUtil.getUniqueId());
                Log.e("subPlan", "getThisDayLoadEntity:差值负重 " + diffLoad);
                Log.e("subPlan", "getThisDayLoadEntity:差值步数 " + diffStep);
                Log.e("subPlan", "getThisDayLoadEntity:当前第 " + currentDays + "天");
                Log.e("subPlan", "getThisDayLoadEntity:推算值负重 " + load);
                Log.e("subPlan", "getThisDayLoadEntity:推算值步数 " + step);

            }
            return currentEntity;
        }
        return null;
    }
    @TargetApi(Build.VERSION_CODES.N)
    public SubPlanEntity getThisDayLoadEntity(List<SubPlanEntity> subPlanEntityList,long planStartTime){
        SubPlanEntity currentEntity = null;
        SubPlanEntity nextEntity = null;
        for (int i = 0; i < subPlanEntityList.size(); i++){
            SubPlanEntity subPlanEntity = subPlanEntityList.get(i);
            if (planStartTime >= DateFormatUtil.getString2Date(subPlanEntity.getStartDate()) &&
                    planStartTime < DateFormatUtil.getString2Date(subPlanEntity.getEndDate())){
                subPlanEntity.setPlanStatus(1);
                currentEntity = new SubPlanEntity();
                currentEntity.setKeyId(subPlanEntity.getKeyId());
                currentEntity.setLoad(subPlanEntity.getLoad());
                currentEntity.setPlanStatus(subPlanEntity.getPlanStatus());
                currentEntity.setModifyStatus(subPlanEntity.getModifyStatus());
                currentEntity.setTrainStep(subPlanEntity.getTrainStep());
                currentEntity.setTrainTime(subPlanEntity.getTrainTime());
                currentEntity.setClassId(subPlanEntity.getClassId());
                currentEntity.setDayNum(subPlanEntity.getDayNum());
                currentEntity.setEndDate(subPlanEntity.getEndDate());
                currentEntity.setId(subPlanEntity.getId());
                currentEntity.setPlanId(subPlanEntity.getPlanId());
                currentEntity.setStartDate(subPlanEntity.getStartDate());
                currentEntity.setUserId(subPlanEntity.getUserId());
                currentEntity.setWeekNum(subPlanEntity.getWeekNum());
                if ((i + 1) < subPlanEntityList.size()){
                    nextEntity = subPlanEntityList.get(i+1);
                }
                break;
            }
        }
        int dayCount;
        float diffWeek;
        if (currentEntity != null){
            dayCount = Math.toIntExact((DateFormatUtil.getDateDiff(currentEntity.getStartDate(), currentEntity.getEndDate()) / dayMs));
            Log.e("subPlan", "getThisDayLoadEntity: " + dayCount);
            if (dayCount > 1 && nextEntity != null){
                diffWeek = nextEntity.getLoad() - currentEntity.getLoad();
                float diff = diffWeek / dayCount;
                long startIntervalDate = planStartTime - DateFormatUtil.getString2Date(currentEntity.getStartDate());
                double currentDays = Math.ceil(startIntervalDate* 1.0f/dayMs);
                long value = Math.round((currentDays -1) * diff);
                currentEntity.setLoad((int) (currentEntity.getLoad() + value));
                currentEntity.setKeyId(SnowflakeIdUtil.getUniqueId());
                Log.e("subPlan", "getThisDayLoadEntity:差值 " + diff);
                Log.e("subPlan", "getThisDayLoadEntity:当前第 " + currentDays + "天");
                Log.e("subPlan", "getThisDayLoadEntity:推算值 " + value);

            }
            return currentEntity;
        }
        return null;
    }
    public void modifySubPlanData(int defaultTrainTime,int minTrainStep,int maxTrainStep){//子计划增加训练步数和训练时间
        List<SubPlanEntity> subPlanEntityList = loadDataByUserId(SPHelper.getUserId());
        if (subPlanEntityList.size() <= 1)
            return;
        if (minTrainStep >= maxTrainStep){
            minTrainStep = maxTrainStep;
        }
        int diffPerStep = (maxTrainStep - minTrainStep)/(subPlanEntityList.size() -1);
        for (int i=0; i<subPlanEntityList.size(); i++){
            SubPlanEntity subPlanEntity = subPlanEntityList.get(i);
            int trainStep;
            if (i == subPlanEntityList.size() -1){
                trainStep = maxTrainStep;
            }else {
                trainStep = minTrainStep + diffPerStep*i;
            }
            subPlanEntity.setTrainStep(trainStep);
            int trainTime = (int) Math.ceil(trainStep*1.0/Global.TrainCountMinute);
            if (trainTime < defaultTrainTime){
                trainTime = defaultTrainTime;
            }
            subPlanEntity.setTrainTime(trainTime);
            subPlanEntity.setModifyStatus(0);
            update(subPlanEntity);
        }
    }
    public void modifySubPlanData(int defaultTrainTime,List<SubPlanEntity> subPlanEntityList){
        for (int i=0; i<subPlanEntityList.size(); i++) {
            SubPlanEntity subPlanEntity = subPlanEntityList.get(i);
            int trainStep = subPlanEntity.getTrainStep();
            int trainTime = (int) Math.ceil(trainStep*1.0/Global.TrainCountMinute);
            if (trainTime < defaultTrainTime){
                trainTime = defaultTrainTime;
            }
            subPlanEntity.setTrainTime(trainTime);
            subPlanEntity.setModifyStatus(0);
            update(subPlanEntity);
        }
    }

}
