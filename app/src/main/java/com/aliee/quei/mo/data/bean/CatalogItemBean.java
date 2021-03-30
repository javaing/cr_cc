package com.aliee.quei.mo.data.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Administrator on 2018/4/24 0024.
 */

public class CatalogItemBean  extends RealmObject{
    @PrimaryKey
    private int id;
    private int bookid;
    private String name;
    private String thumb;
    private int bookBean;
    private int sort;
    public boolean isUnlocked;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookid() {
        return bookid;
    }

    public void setBookid(int bookid) {
        this.bookid = bookid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getBookBean() {
        return bookBean;
    }

    public void setBookBean(int bookBean) {
        this.bookBean = bookBean;
    }

    public int getSort() {
        return sort;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public void setSort(int sort) {


        this.sort = sort;
    }
}
