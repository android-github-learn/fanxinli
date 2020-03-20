package com.android.fanxinli;

import java.io.Serializable;
import java.util.List;

public class ClassInfo implements Serializable {

    private boolean isVip;
    private String imgUrl;
    private int childAudioTotalClass;
    private int playNumber;
    private String audioDescribe;
    private String teacherPhoto;
    private String teacherIntroduction;
    private List<ChildClassInfo> childClassInfoList;

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getChildAudioTotalClass() {
        return childAudioTotalClass;
    }

    public void setChildAudioTotalClass(int childAudioTotalClass) {
        this.childAudioTotalClass = childAudioTotalClass;
    }

    public int getPlayNumber() {
        return playNumber;
    }

    public void setPlayNumber(int playNumber) {
        this.playNumber = playNumber;
    }

    public String getAudioDescribe() {
        return audioDescribe;
    }

    public void setAudioDescribe(String audioDescribe) {
        this.audioDescribe = audioDescribe;
    }

    public String getTeacherPhoto() {
        return teacherPhoto;
    }

    public void setTeacherPhoto(String teacherPhoto) {
        this.teacherPhoto = teacherPhoto;
    }

    public String getTeacherIntroduction() {
        return teacherIntroduction;
    }

    public void setTeacherIntroduction(String teacherIntroduction) {
        this.teacherIntroduction = teacherIntroduction;
    }

    public List<ChildClassInfo> getChildClassInfoList() {
        return childClassInfoList;
    }

    public void setChildClassInfoList(List<ChildClassInfo> childClassInfoList) {
        this.childClassInfoList = childClassInfoList;
    }

}
