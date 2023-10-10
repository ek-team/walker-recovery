package com.pharos.walker.beans;

import java.util.List;

public class ServerPlanEntity {

    /**
     * code : 0
     * data : []
     */

    private int code;
    private List<PlanEntity> data;
    private List<OriginalPlanEntity> originalData;

    public List<OriginalPlanEntity> getOriginalData() {
        return originalData;
    }

    public void setOriginalData(List<OriginalPlanEntity> originalData) {
        this.originalData = originalData;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<PlanEntity> getData() {
        return data;
    }

    public void setData(List<PlanEntity> data) {
        this.data = data;
    }
}
