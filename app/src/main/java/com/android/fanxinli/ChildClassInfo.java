package com.android.fanxinli;

import java.io.Serializable;

public class ChildClassInfo implements Serializable {

    private String title;
    private String name;
    private boolean playStatus;
    private int playProgress;
    private int classTimer;
    private boolean isCollection;
    private String playUrl;
    private boolean isSubtitle;

    public boolean isSubtitle() {
        return isSubtitle;
    }

    public void setSubtitle(boolean subtitle) {
        isSubtitle = subtitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPlayStatus() {
        return playStatus;
    }

    public void setPlayStatus(boolean playStatus) {
        this.playStatus = playStatus;
    }

    public int getPlayProgress() {
        return playProgress;
    }

    public void setPlayProgress(int playProgress) {
        this.playProgress = playProgress;
    }

    public int getClassTimer() {
        return classTimer;
    }

    public void setClassTimer(int classTimer) {
        this.classTimer = classTimer;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }
}
