package com.pharos.walker.database;

import com.pharos.walker.beans.BLeDeviceInfoBean;
import com.pharos.walker.greendao.BLeDeviceInfoBeanDao;
import com.pharos.walker.utils.GreenDaoHelper;

import java.util.List;

public class BleDeviceInfoManager {
    private static volatile BleDeviceInfoManager instance = null;

    private BLeDeviceInfoBeanDao bLeDeviceInfoBeanDao;

    private BleDeviceInfoManager() {
        bLeDeviceInfoBeanDao = GreenDaoHelper.getDaoSession().getBLeDeviceInfoBeanDao();
    }

    public static BleDeviceInfoManager getInstance() {
        if (instance == null) {
            synchronized (BleDeviceInfoManager.class) {
                if (instance == null) {
                    instance = new BleDeviceInfoManager();
                }
            }
        }
        return instance;
    }
    public void insert(BLeDeviceInfoBean bLeDeviceInfoBean){
        bLeDeviceInfoBeanDao.insertOrReplace(bLeDeviceInfoBean);
    }
    public void insert(int rssi,long interval,long currentTime){
        List<BLeDeviceInfoBean> beanList = loadAll();
        if (beanList.size() > 3000){
            delete(beanList.get(0));
        }
        BLeDeviceInfoBean bLeDeviceInfoBean = new BLeDeviceInfoBean();
        bLeDeviceInfoBean.setCreateDate(currentTime);
        bLeDeviceInfoBean.setRssi(rssi);
        bLeDeviceInfoBean.setInterval(interval);
        bLeDeviceInfoBeanDao.insertOrReplace(bLeDeviceInfoBean);
    }
    public void insert(List<BLeDeviceInfoBean> bLeDeviceInfoBeans){
        bLeDeviceInfoBeanDao.insertOrReplaceInTx(bLeDeviceInfoBeans);
    }
    public List<BLeDeviceInfoBean> loadAll(){
        return bLeDeviceInfoBeanDao.loadAll();
    }
    public void delete(){
        bLeDeviceInfoBeanDao.deleteAll();
    }
    public void delete(BLeDeviceInfoBean bLeDeviceInfoBean){
        bLeDeviceInfoBeanDao.delete(bLeDeviceInfoBean);
    }
}
