package com.pharos.walker.database;

import com.pharos.walker.beans.EvaluateEntity;
import com.pharos.walker.constants.Global;
import com.pharos.walker.greendao.EvaluateEntityDao;
import com.pharos.walker.utils.GreenDaoHelper;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.SnowflakeIdUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanglun on 2021/5/8
 * Describe:
 */
public class EvaluateManager {
    private static volatile EvaluateManager instance = null;

    private EvaluateEntityDao evaluateEntityDao;

    private EvaluateManager() {
        evaluateEntityDao = GreenDaoHelper.getDaoSession().getEvaluateEntityDao();
    }

    public static EvaluateManager getInstance() {
        if (instance == null) {
            synchronized (EvaluateManager.class) {
                if (instance == null) {
                    instance = new EvaluateManager();
                }
            }
        }
        return instance;
    }
    public EvaluateEntity insert(int value,int vas,float firstValue,float secondValue,float thirdValue,float fourthValue,float fifthValue){
        EvaluateEntity evaluateEntity = new EvaluateEntity();
        evaluateEntity.setCreateDate(System.currentTimeMillis());
        evaluateEntity.setEvaluateResult(value);
        evaluateEntity.setVas(vas);
        evaluateEntity.setFirstValue(firstValue);
        evaluateEntity.setSecondValue(secondValue);
        evaluateEntity.setThirdValue(thirdValue);
        evaluateEntity.setFourthValue(fourthValue);
        evaluateEntity.setFifthValue(fifthValue);
        evaluateEntity.setKeyId(SnowflakeIdUtil.getUniqueId());
        evaluateEntity.setUserId(SPHelper.getUserId());
        evaluateEntity.setIsUpload(Global.UploadStatus);
        evaluateEntityDao.insert(evaluateEntity);
        return evaluateEntity;
    }
    public void insert(EvaluateEntity evaluateEntity){
        evaluateEntityDao.insertOrReplace(evaluateEntity);
    }
    public void insert(List<EvaluateEntity> evaluateEntityList){
        if (evaluateEntityList == null)
            return;
        for (EvaluateEntity entity : evaluateEntityList){
            entity.setId(null);
            insert(entity);
        }
//        evaluateEntityDao.insertOrReplaceInTx(evaluateEntityList);
    }
    public List<EvaluateEntity> loadAll(long userId){
        return evaluateEntityDao.queryBuilder().where(EvaluateEntityDao.Properties.UserId.eq(userId)).list();
    }
    public List<EvaluateEntity> loadAll(){
        return evaluateEntityDao.loadAll();
    }
    public void update(EvaluateEntity evaluateEntity){
        evaluateEntityDao.update(evaluateEntity);
    }

    public List<EvaluateEntity> loadNotUploadRecord(long userId){
        List<EvaluateEntity> evaluateEntityList = loadAll(userId);
        List<EvaluateEntity> newList = new ArrayList<>();
        for (EvaluateEntity evaluateEntity : evaluateEntityList){
            if (evaluateEntity.getIsUpload() != Global.UploadNetStatus){
                evaluateEntity.setIsUpload(Global.UploadNetStatus);
                newList.add(evaluateEntity);
            }
        }
        return newList;
    }
    public void updateEvaluateRecordStatus(List<EvaluateEntity> evaluateEntityList){
        if (evaluateEntityList == null)
            return;
        for (EvaluateEntity evaluateEntity : evaluateEntityList){
            evaluateEntity.setIsUpload(Global.UploadNetStatus);
            update(evaluateEntity);
        }
    }
    public void insertServerEntity(EvaluateEntity evaluateEntity){
        evaluateEntity.setIsUpload(Global.UploadNetStatus);
        evaluateEntityDao.insertOrReplace(evaluateEntity);
    }
}
