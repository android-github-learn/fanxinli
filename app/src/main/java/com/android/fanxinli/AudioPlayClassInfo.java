package com.android.fanxinli;

import java.io.Serializable;
import java.util.List;

public class AudioPlayClassInfo implements Serializable {

    private int id;
    private String name;
//    是否为vip课程 0-否 1-是 仅当is_limit_free=0时有效，如果is_limit_free=1则不显示是否为vip
    private boolean is_vip;
//    是否为限免 0-否 1-是
    private int is_limit_free;
    private String img_url;
    private int section;
    private int num;
    private String desc;
    private String lecturer_img;
    private String lecturer_name;
    private String lecturer_desc;
    private List<ChildClassInfo> audio_list;

    public boolean isIs_vip() {
        return is_vip;
    }

    public void setIs_vip(boolean is_vip) {
        this.is_vip = is_vip;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLecturer_img() {
        return lecturer_img;
    }

    public void setLecturer_img(String lecturer_img) {
        this.lecturer_img = lecturer_img;
    }

    public String getLecturer_desc() {
        return lecturer_desc;
    }

    public void setLecturer_desc(String lecturer_desc) {
        this.lecturer_desc = lecturer_desc;
    }

    public List<ChildClassInfo> getAudio_list() {
        return audio_list;
    }

    public void setAudio_list(List<ChildClassInfo> audio_list) {
        this.audio_list = audio_list;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIs_limit_free() {
        return is_limit_free;
    }

    public void setIs_limit_free(int is_limit_free) {
        this.is_limit_free = is_limit_free;
    }

    public String getLecturer_name() {
        return lecturer_name;
    }

    public void setLecturer_name(String lecturer_name) {
        this.lecturer_name = lecturer_name;
    }
}
