package com.aliee.quei.mo.data;

import com.google.gson.annotations.SerializedName;

public class BulletinInfo {

    @SerializedName("id")
    private String ID = "";

    @SerializedName("imagePath")
    private String Imagepath= "";

    @SerializedName("tag")
    private String TAG = "";

    @SerializedName("title")
    private String Title = "";

    @SerializedName("content")
    private String Content = "";

    @SerializedName("releaseDate")
    private String ReleaseDate = "";

    public String getID() {
        return ID;
    }

    public void setID(String Id) {
        this.ID = Id;
    }

    public String getImagepath() {
        return Imagepath;
    }

    public void setImagepath(String ImagePath) {
        this.Imagepath = Imagepath;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        this.Content = content;
    }
    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public String getTag() {
        return TAG;
    }

    public void setTag(String tag) {
        this.TAG = tag;
    }

    public String getReleaseDate() {
        return ReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.ReleaseDate = releaseDate;
    }



}
