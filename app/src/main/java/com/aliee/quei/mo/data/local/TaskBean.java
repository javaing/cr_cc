package com.aliee.quei.mo.data.local;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TaskBean extends RealmObject {

    public TaskBean() {
    }

    public TaskBean(int id, int reward, int desc, String title, int type, int period, String event) {
        this.id = id;
        this.reward = reward;
        this.desc = desc;
        this.title = title;
        this.type = type;
        this.period = period;
        this.event = event;
    }

    @PrimaryKey
    private int id;
    private int reward;
    private int desc;
    private String title;
    private int type;// 0 登录任务//1阅读时长任务//2阅读漫画数
    private int period; //任务周期 0 日任务 //1周任务 //2 新手任务
    private int status;

    private String event;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public int getDesc() {
        return desc;
    }

    public void setDesc(int desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
