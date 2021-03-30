package com.aliee.quei.mo.data.local;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TaskRewardBean extends RealmObject {
    @PrimaryKey
    private long time;
    private int amount;
    private int taskId;

    public TaskRewardBean() {
    }

    public TaskRewardBean(long time, int amount, int taskId) {
        this.time = time;
        this.amount = amount;
        this.taskId = taskId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
}
