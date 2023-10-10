package com.pharos.walker.beans;

public class ProductStockInfoBean {

    /**
     * code : 0
     * data : {"activationCode":"b07eec297fcf8f56e412e67de97505b0","activationDate":"2024-12-01","createDate":"2021-08-30 10:32:34","del":1,"deptId":1,"iccId":"89861120285037042745","id":173,"ipAdd":"127.0.0.1","lastBindUserTime":"2021-09-18 11:26:19","liveQrCodeId":"e3484c7dc7effe9e511947c7889cd1fc","locatorId":18,"macAddress":"40:24:b2:ef:44:5a","naniQrCodeUrl":"已绑定","productDeviceType":"下肢医用版","productId":1,"productLockNum":0,"productSn":"test-serial00001","salesmanId":289,"servicePackId":89,"status":20,"systemVersion":"rk3368-userdebug 9 PI 213509 test-keys 20220817","tag":0,"targetMacAdd":"40:24:b2:ef:44:a4","upload":0,"versionStr":"34"}
     */

    private int code;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * activationCode : b07eec297fcf8f56e412e67de97505b0
         * activationDate : 2024-12-01
         * createDate : 2021-08-30 10:32:34
         * del : 1
         * deptId : 1
         * iccId : 89861120285037042745
         * id : 173
         * ipAdd : 127.0.0.1
         * lastBindUserTime : 2021-09-18 11:26:19
         * liveQrCodeId : e3484c7dc7effe9e511947c7889cd1fc
         * locatorId : 18
         * macAddress : 40:24:b2:ef:44:5a
         * naniQrCodeUrl : 已绑定
         * productDeviceType : 下肢医用版
         * productId : 1
         * productLockNum : 0
         * productSn : test-serial00001
         * salesmanId : 289
         * servicePackId : 89
         * status : 20
         * systemVersion : rk3368-userdebug 9 PI 213509 test-keys 20220817
         * tag : 0
         * targetMacAdd : 40:24:b2:ef:44:a4
         * upload : 0
         * versionStr : 34
         */

        private String activationCode;
        private String activationDate;
        private String createDate;
        private int del;
        private int deptId;
        private String iccId;
        private int id;
        private String ipAdd;
        private String lastBindUserTime;
        private String liveQrCodeId;
        private int locatorId;
        private String macAddress;
        private String naniQrCodeUrl;
        private String productDeviceType;
        private int productId;
        private int productLockNum;
        private String productSn;
        private int salesmanId;
        private int servicePackId;
        private int status;
        private String systemVersion;
        private int tag;
        private String targetMacAdd;
        private int upload;
        private String versionStr;
        private String sourceProductSn;

        public String getSourceProductSn() {
            return sourceProductSn;
        }

        public void setSourceProductSn(String sourceProductSn) {
            this.sourceProductSn = sourceProductSn;
        }

        public String getActivationCode() {
            return activationCode;
        }

        public void setActivationCode(String activationCode) {
            this.activationCode = activationCode;
        }

        public String getActivationDate() {
            return activationDate;
        }

        public void setActivationDate(String activationDate) {
            this.activationDate = activationDate;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public int getDel() {
            return del;
        }

        public void setDel(int del) {
            this.del = del;
        }

        public int getDeptId() {
            return deptId;
        }

        public void setDeptId(int deptId) {
            this.deptId = deptId;
        }

        public String getIccId() {
            return iccId;
        }

        public void setIccId(String iccId) {
            this.iccId = iccId;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getIpAdd() {
            return ipAdd;
        }

        public void setIpAdd(String ipAdd) {
            this.ipAdd = ipAdd;
        }

        public String getLastBindUserTime() {
            return lastBindUserTime;
        }

        public void setLastBindUserTime(String lastBindUserTime) {
            this.lastBindUserTime = lastBindUserTime;
        }

        public String getLiveQrCodeId() {
            return liveQrCodeId;
        }

        public void setLiveQrCodeId(String liveQrCodeId) {
            this.liveQrCodeId = liveQrCodeId;
        }

        public int getLocatorId() {
            return locatorId;
        }

        public void setLocatorId(int locatorId) {
            this.locatorId = locatorId;
        }

        public String getMacAddress() {
            return macAddress;
        }

        public void setMacAddress(String macAddress) {
            this.macAddress = macAddress;
        }

        public String getNaniQrCodeUrl() {
            return naniQrCodeUrl;
        }

        public void setNaniQrCodeUrl(String naniQrCodeUrl) {
            this.naniQrCodeUrl = naniQrCodeUrl;
        }

        public String getProductDeviceType() {
            return productDeviceType;
        }

        public void setProductDeviceType(String productDeviceType) {
            this.productDeviceType = productDeviceType;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public int getProductLockNum() {
            return productLockNum;
        }

        public void setProductLockNum(int productLockNum) {
            this.productLockNum = productLockNum;
        }

        public String getProductSn() {
            return productSn;
        }

        public void setProductSn(String productSn) {
            this.productSn = productSn;
        }

        public int getSalesmanId() {
            return salesmanId;
        }

        public void setSalesmanId(int salesmanId) {
            this.salesmanId = salesmanId;
        }

        public int getServicePackId() {
            return servicePackId;
        }

        public void setServicePackId(int servicePackId) {
            this.servicePackId = servicePackId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getSystemVersion() {
            return systemVersion;
        }

        public void setSystemVersion(String systemVersion) {
            this.systemVersion = systemVersion;
        }

        public int getTag() {
            return tag;
        }

        public void setTag(int tag) {
            this.tag = tag;
        }

        public String getTargetMacAdd() {
            return targetMacAdd;
        }

        public void setTargetMacAdd(String targetMacAdd) {
            this.targetMacAdd = targetMacAdd;
        }

        public int getUpload() {
            return upload;
        }

        public void setUpload(int upload) {
            this.upload = upload;
        }

        public String getVersionStr() {
            return versionStr;
        }

        public void setVersionStr(String versionStr) {
            this.versionStr = versionStr;
        }
    }
}
