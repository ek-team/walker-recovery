package com.pharos.walker.beans;

public class CacheBean {
    private String msg;
    private long timestamp;
    private float value;

    public CacheBean(long timestamp, float value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public CacheBean(String msg, long timestamp) {
        this.msg = msg;
        this.timestamp = timestamp;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
