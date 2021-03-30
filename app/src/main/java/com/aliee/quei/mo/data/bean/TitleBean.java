package com.aliee.quei.mo.data.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:sunfuyi
 * 时间:2019-08-10
 * 描述:
 */
public class TitleBean {
    private int id;
    private String title;

    public TitleBean(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;


    }

    public static String getTitles(int id) {
        String title = null;
        List<TitleBean> mTitleBeans = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            mTitleBeans.add(new TitleBean(2222, "噓！姊姊的誘惑"));
            mTitleBeans.add(new TitleBean(2027, "调教性奴少妇"));
            mTitleBeans.add(new TitleBean(2001, "他的女人"));
            mTitleBeans.add(new TitleBean(2160, "性奴养成计划"));
            mTitleBeans.add(new TitleBean(2025, "神秘骚穴"));
        }

        for (int i = 0; i < mTitleBeans.size(); i++) {
            if (mTitleBeans.get(i).id == id) {
                title = mTitleBeans.get(i).title;
            }
        }
        return title;
    }
}
