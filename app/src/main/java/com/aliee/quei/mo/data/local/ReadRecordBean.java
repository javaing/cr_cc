package com.aliee.quei.mo.data.local;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Administrator on 2018/4/24 0024.
 */

public class ReadRecordBean extends RealmObject{
    @PrimaryKey
    private int bookId;
    private int page; //第几页
    private long updateAt; //毫秒 时间戳
    private int chapterId;
    private String name;

    public ReadRecordBean(int bookId,int chapterId,String name ,int page, long updateAt) {
        this.bookId = bookId;
        this.chapterId = chapterId;
        this.page = page;
        this.name = name;
        this.updateAt = updateAt;
    }

    public ReadRecordBean() {
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
