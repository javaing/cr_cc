package com.aliee.quei.mo.database.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LimitFreeBookBean extends RealmObject {
    @PrimaryKey
    private long id;
    private String title;
    private String cover;
    private long endTime;
    private String descr;

    public LimitFreeBookBean(long id, String title, String cover, long endTime, String descr) {
        this.id = id;
        this.title = title;
        this.cover = cover;
        this.endTime = endTime;
        this.descr = descr;
    }

    public LimitFreeBookBean() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
}
