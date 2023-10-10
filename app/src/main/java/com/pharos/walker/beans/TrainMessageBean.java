package com.pharos.walker.beans;

public class TrainMessageBean {
    private int trainTime;
    private int countOfTime;
    private int timesOfDay;
    private int planStatus;

    public TrainMessageBean(int trainTime, int countOfTime, int timesOfDay,int planStatus) {
        this.trainTime = trainTime;
        this.countOfTime = countOfTime;
        this.timesOfDay = timesOfDay;
        this.planStatus = planStatus;
    }

    public TrainMessageBean(int planStatus) {
        this.planStatus = planStatus;
    }

    public int getPlanStatus() {
        return planStatus;
    }

    public void setPlanStatus(int planStatus) {
        this.planStatus = planStatus;
    }

    public TrainMessageBean(int trainTime, int countOfTime) {
        this.trainTime = trainTime;
        this.countOfTime = countOfTime;
    }
    public int getTimesOfDay() {
        return timesOfDay;
    }

    public void setTimesOfDay(int timesOfDay) {
        this.timesOfDay = timesOfDay;
    }
    public int getTrainTime() {
        return trainTime;
    }

    public void setTrainTime(int trainTime) {
        this.trainTime = trainTime;
    }

    public int getCountOfTime() {
        return countOfTime;
    }

    public void setCountOfTime(int countOfTime) {
        this.countOfTime = countOfTime;
    }
}
