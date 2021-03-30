package com.aliee.quei.mo.data.local;

import io.realm.BuildConfig;
import io.realm.RealmObject;

public class ReadStatBean extends RealmObject {
    private int year;
    private int month;
    private int dayOfMonth;
    private int weekOfYear;

    private int bookid;
    private int chapterCount;
    private int duration;

    public ReadStatBean() {
    }

    public ReadStatBean(int year, int month, int dayOfMonth, int weekOfYear, int bookid, int chapterCount, int duration) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.weekOfYear = weekOfYear;
        this.bookid = bookid;
        this.chapterCount = chapterCount;
        if (BuildConfig.DEBUG) {
            this.duration = duration * 10;
        } else {
            this.duration = duration;
        }
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getWeekOfYear() {
        return weekOfYear;
    }

    public void setWeekOfYear(int weekOfYear) {
        this.weekOfYear = weekOfYear;
    }

    public int getBookid() {
        return bookid;
    }

    public void setBookid(int bookid) {
        this.bookid = bookid;
    }

    public int getChapterCount() {
        return chapterCount;
    }

    public void setChapterCount(int chapterCount) {
        this.chapterCount = chapterCount;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
