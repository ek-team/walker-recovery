package com.pharos.walker.beans;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "DOCTOR_NAME")
public class LocalDoctorNameBean {
    @Id
    private Long id;//ID
    private long keyId;//唯一ID
    private String macAddress;
    @Index(unique = true)
    private String doctorName;
    private String str;//保留字段
    private long createDate;
    @Generated(hash = 1147939797)
    public LocalDoctorNameBean(Long id, long keyId, String macAddress,
            String doctorName, String str, long createDate) {
        this.id = id;
        this.keyId = keyId;
        this.macAddress = macAddress;
        this.doctorName = doctorName;
        this.str = str;
        this.createDate = createDate;
    }
    @Generated(hash = 1166484432)
    public LocalDoctorNameBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getKeyId() {
        return this.keyId;
    }
    public void setKeyId(long keyId) {
        this.keyId = keyId;
    }
    public String getMacAddress() {
        return this.macAddress;
    }
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    public String getDoctorName() {
        return this.doctorName;
    }
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
    public String getStr() {
        return this.str;
    }
    public void setStr(String str) {
        this.str = str;
    }
    public long getCreateDate() {
        return this.createDate;
    }
    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
   
}
