package com.android.fanxinli;

import java.io.Serializable;

public class ChildClassInfo implements Serializable {

    private int id;
    private String title;
    private String name;
    private boolean playStatus;
    private int progress;
    private int time;
    private boolean is_collect;
    private String playUrl;
    private boolean isSubtitle;
    private int pid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

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

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isIs_collect() {
        return is_collect;
    }

    public void setIs_collect(boolean is_collect) {
        this.is_collect = is_collect;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }
}
