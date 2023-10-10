package com.pharos.walker.beans;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "BLE_DEVICE_INFO")
public class BLeDeviceInfoBean {
    @Id(autoincrement = true)
    private Long id;                 //ID
    private long createDate;
    private long interval;
    private int rssi;
    private int batteryVolume;
    @Generated(hash = 1243753790)
    public BLeDeviceInfoBean(Long id, long createDate, long interval, int rssi,
            int batteryVolume) {
        this.id = id;
        this.createDate = createDate;
        this.interval = interval;
        this.rssi = rssi;
        this.batteryVolume = batteryVolume;
    }
    @Generated(hash = 1565014653)
    public BLeDeviceInfoBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getCreateDate() {
        return this.createDate;
    }
    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
    public long getInterval() {
        return this.interval;
    }
    public void setInterval(long interval) {
        this.interval = interval;
    }
    public int getRssi() {
        return this.rssi;
    }
    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
    public int getBatteryVolume() {
        return this.batteryVolume;
    }
    public void setBatteryVolume(int batteryVolume) {
        this.batteryVolume = batteryVolume;
    }

}
