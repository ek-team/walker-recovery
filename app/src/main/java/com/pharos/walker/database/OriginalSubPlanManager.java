package com.pharos.walker.database;

import com.pharos.walker.beans.OriginalSubPlanEntity;
import com.pharos.walker.greendao.OriginalSubPlanEntityDao;
import com.pharos.walker.utils.GreenDaoHelper;

import java.util.List;

public class OriginalSubPlanManager {
    private static volatile OriginalSubPlanManager instance = null;

    private OriginalSubPlanEntityDao subPlanEntityDao;
    private static final long dayMs = 24 * 60 * 60 * 1000;

    private OriginalSubPlanManager() {
        subPlanEntityDao = GreenDaoHelper.getDaoSession().getOriginalSubPlanEntityDao();
    }

    public static OriginalSubPlanManager getInstance() {
        if (instance == null) {
            synchronized (OriginalSubPlanManager.class) {
                if (instance == null) {
                    instance = new OriginalSubPlanManager();
                }
            }
        }
        return instance;
    }
    public List<OriginalSubPlanEntity> loadDataByUserId(long userId){
        return subPlanEntityDao.queryBuilder().where(OriginalSubPlanEntityDao.Properties.UserId.eq(userId)).orderAsc(OriginalSubPlanEntityDao.Properties.StartDate).list();
    }
    public void insertMany(List<OriginalSubPlanEntity> subPlanEntityList){
        if (subPlanEntityList != null && subPlanEntityList.size() > 0){
            clearPlanByUserId(subPlanEntityList.get(0).getUserId());
            subPlanEntityDao.insertOrReplaceInTx(subPlanEntityList);
        }
    }
    public void insert(OriginalSubPlanEntity originalSubPlanEntity){
            subPlanEntityDao.insertOrReplace(originalSubPlanEntity);
    }
    public void clearPlanByUserId(long userId){
        subPlanEntityDao.deleteInTx(loadDataByUserId(userId));
    }
}
