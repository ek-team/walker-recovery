package com.pharos.walker.database;

import com.pharos.walker.beans.OriginalPlanEntity;
import com.pharos.walker.beans.PlanEntity;
import com.pharos.walker.greendao.OriginalPlanEntityDao;
import com.pharos.walker.utils.GreenDaoHelper;

import java.util.ArrayList;
import java.util.List;

public class OriginalPlanManager {
    private static volatile OriginalPlanManager instance = null;

    private OriginalPlanEntityDao planEntityDao;

    private OriginalPlanManager() {
        planEntityDao = GreenDaoHelper.getDaoSession().getOriginalPlanEntityDao();
    }

    public static OriginalPlanManager getInstance() {
        if (instance == null) {
            synchronized (OriginalPlanManager.class) {
                if (instance == null) {
                    instance = new OriginalPlanManager();
                }
            }
        }
        return instance;
    }
    public List<OriginalPlanEntity> getPlanListByUserId(long userId){
        return planEntityDao.queryBuilder().where(OriginalPlanEntityDao.Properties.UserId.eq(userId)).list();
    }
    public List<OriginalPlanEntity> getMasterPlanListByUserId(long userId){
//        planEntityDao.detachAll();
        List<OriginalPlanEntity> tempPlanEntityList = planEntityDao.queryBuilder().where(OriginalPlanEntityDao.Properties.UserId.eq(userId)).list();
        List<OriginalPlanEntity> resultPlanEntityList = new ArrayList<>();
        for (int i = 0; i < tempPlanEntityList.size(); i++){//转换成服务端需要的entity
            if (i == 0){//添加子训练计划
                OriginalPlanEntity entity =  tempPlanEntityList.get(0);
                entity.setSubPlanEntityList(OriginalSubPlanManager.getInstance().loadDataByUserId(userId));
                resultPlanEntityList.add(entity);
            }else {
                resultPlanEntityList.add(tempPlanEntityList.get(i));
            }
        }
        return resultPlanEntityList;
    }
    public boolean isPlanEmpty(long userId){
        List<OriginalPlanEntity> planEntityList = getPlanListByUserId(userId);
        return planEntityList.size() <= 0;
    }
    public void insert(OriginalPlanEntity entity){
        planEntityDao.insertOrReplace(entity);
    }
    public void clearDataByUserId(long userId){
        planEntityDao.deleteInTx(planEntityDao.queryBuilder().where(OriginalPlanEntityDao.Properties.UserId.eq(userId)).list());
        OriginalSubPlanManager.getInstance().clearPlanByUserId(userId);
    }
}
