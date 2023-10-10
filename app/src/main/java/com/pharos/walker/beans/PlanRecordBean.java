package com.pharos.walker.beans;

public class PlanRecordBean {
    private long userId;
    private int afterVersion;
    private String macAdd;
    private String createTime;
    private String productSn;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getAfterVersion() {
        return afterVersion;
    }

    public void setAfterVersion(int afterVersion) {
        this.afterVersion = afterVersion;
    }

    public String getMacAdd() {
        return macAdd;
    }

    public void setMacAdd(String macAdd) {
        this.macAdd = macAdd;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getProductSn() {
        return productSn;
    }

    public void setProductSn(String productSn) {
        this.productSn = productSn;
    }
}
