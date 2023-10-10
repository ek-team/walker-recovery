package com.pharos.walker.database;

import com.pharos.walker.beans.LocalDoctorNameBean;
import com.pharos.walker.greendao.LocalDoctorNameBeanDao;
import com.pharos.walker.utils.GreenDaoHelper;

import java.util.List;

public class LocalDoctorNameManager {
    private static volatile LocalDoctorNameManager instance = null;

    private LocalDoctorNameBeanDao localDoctorNameBeanDao;

    private LocalDoctorNameManager() {
        localDoctorNameBeanDao = GreenDaoHelper.getDaoSession().getLocalDoctorNameBeanDao();
    }

    public static LocalDoctorNameManager getInstance() {
        if (instance == null) {
            synchronized (LocalDoctorNameManager.class) {
                if (instance == null) {
                    instance = new LocalDoctorNameManager();
                }
            }
        }
        return instance;
    }
    public void insert(LocalDoctorNameBean doctorNameBean){
        localDoctorNameBeanDao.insertOrReplace(doctorNameBean);
    }
    public List<LocalDoctorNameBean> loadAll(){
        return localDoctorNameBeanDao.loadAll();
    }
}
