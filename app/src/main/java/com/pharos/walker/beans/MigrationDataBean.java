package com.pharos.walker.beans;

public class MigrationDataBean {

    /**
     * code : 0
     * data : {"evaluateRecord":"https://ewj-pharos.oss-cn-hangzhou.aliyuncs.com/productStockInfo/0c8c24b0e63b_evaluateRecord.json","plan":"https://ewj-pharos.oss-cn-hangzhou.aliyuncs.com/productStockInfo/0c8c24b0e63b_plan.json","subPlan":"https://ewj-pharos.oss-cn-hangzhou.aliyuncs.com/productStockInfo/0c8c24b0e63b_subPlan.json","trainData":"https://ewj-pharos.oss-cn-hangzhou.aliyuncs.com/productStockInfo/0c8c24b0e63b_trainData.json","trainRecord":"https://ewj-pharos.oss-cn-hangzhou.aliyuncs.com/productStockInfo/0c8c24b0e63b_trainRecord.json","user":"https://ewj-pharos.oss-cn-hangzhou.aliyuncs.com/productStockInfo/0c8c24b0e63b_user.json"}
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
         * evaluateRecord : https://ewj-pharos.oss-cn-hangzhou.aliyuncs.com/productStockInfo/0c8c24b0e63b_evaluateRecord.json
         * plan : https://ewj-pharos.oss-cn-hangzhou.aliyuncs.com/productStockInfo/0c8c24b0e63b_plan.json
         * subPlan : https://ewj-pharos.oss-cn-hangzhou.aliyuncs.com/productStockInfo/0c8c24b0e63b_subPlan.json
         * trainData : https://ewj-pharos.oss-cn-hangzhou.aliyuncs.com/productStockInfo/0c8c24b0e63b_trainData.json
         * trainRecord : https://ewj-pharos.oss-cn-hangzhou.aliyuncs.com/productStockInfo/0c8c24b0e63b_trainRecord.json
         * user : https://ewj-pharos.oss-cn-hangzhou.aliyuncs.com/productStockInfo/0c8c24b0e63b_user.json
         */

        private String evaluateRecord;
        private String plan;
        private String subPlan;
        private String trainData;
        private String trainRecord;
        private String user;

        public String getEvaluateRecord() {
            return evaluateRecord;
        }

        public void setEvaluateRecord(String evaluateRecord) {
            this.evaluateRecord = evaluateRecord;
        }

        public String getPlan() {
            return plan;
        }

        public void setPlan(String plan) {
            this.plan = plan;
        }

        public String getSubPlan() {
            return subPlan;
        }

        public void setSubPlan(String subPlan) {
            this.subPlan = subPlan;
        }

        public String getTrainData() {
            return trainData;
        }

        public void setTrainData(String trainData) {
            this.trainData = trainData;
        }

        public String getTrainRecord() {
            return trainRecord;
        }

        public void setTrainRecord(String trainRecord) {
            this.trainRecord = trainRecord;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }
    }
}
