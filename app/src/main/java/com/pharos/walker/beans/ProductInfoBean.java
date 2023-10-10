package com.pharos.walker.beans;


public class ProductInfoBean {
    /**
     * code : 0
     * data : {"iccId":"89861120285037042737","macAddress":"0c:8c:24:b0:e6:3b","productLockNum":0,"systemVersion":"rk3368-userdebug 9 PI 164141 test-keys 20220607","versionStr":"7"}
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
         * iccId : 89861120285037042737
         * macAddress : 0c:8c:24:b0:e6:3b
         * productLockNum : 0
         * systemVersion : rk3368-userdebug 9 PI 164141 test-keys 20220607
         * versionStr : 7
         */

        private String iccId;
        private String macAddress;
        private int productLockNum;
        private String systemVersion;
        private String versionStr;
        private String productSerialNumber;
        private String productDeviceType;
        private String productSn;
        private String ipAdd;

        public String getIpAdd() {
            return ipAdd;
        }

        public void setIpAdd(String ipAdd) {
            this.ipAdd = ipAdd;
        }

        public String getProductSn() {
            return productSn;
        }

        public void setProductSn(String productSn) {
            this.productSn = productSn;
        }

        public String getIccId() {
            return iccId;
        }

        public void setIccId(String iccId) {
            this.iccId = iccId;
        }

        public String getMacAddress() {
            return macAddress;
        }

        public void setMacAddress(String macAddress) {
            this.macAddress = macAddress;
        }

        public int getProductLockNum() {
            return productLockNum;
        }

        public void setProductLockNum(int productLockNum) {
            this.productLockNum = productLockNum;
        }

        public String getSystemVersion() {
            return systemVersion;
        }

        public void setSystemVersion(String systemVersion) {
            this.systemVersion = systemVersion;
        }

        public String getVersionStr() {
            return versionStr;
        }

        public void setVersionStr(String versionStr) {
            this.versionStr = versionStr;
        }

        public String getProductSerialNumber() {
            return productSerialNumber;
        }

        public void setProductSerialNumber(String productSerialNumber) {
            this.productSerialNumber = productSerialNumber;
        }

        public String getProductDeviceType() {
            return productDeviceType;
        }

        public void setProductDeviceType(String productDeviceType) {
            this.productDeviceType = productDeviceType;
        }
    }
}
