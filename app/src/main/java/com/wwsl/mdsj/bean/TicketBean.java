package com.wwsl.mdsj.bean;

import java.io.Serializable;

public class TicketBean implements Serializable {
    private String id;
    private String title;
    private String content;
    private String uid;
    private String price;
    private String buy_num;
    private String num;
    private String stime;
    private String etime;
    private String status;
    private String user_nicename;
    private String avatar_thumb;
    private String sex;
    //0 当前用户没买票，1买了票
    private int isTicket;
    //0未关注 1关注了该主播
    private int isAttention;
    private String image;
    //我的门票，主播ID
    private String touid;
    //我的门票，门票状态
    private String status_msg;

    private String addtime;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBuy_num() {
        return buy_num;
    }

    public void setBuy_num(String buy_num) {
        this.buy_num = buy_num;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getStime() {
        return stime;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public String getEtime() {
        return etime;
    }

    public void setEtime(String etime) {
        this.etime = etime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser_nicename() {
        return user_nicename;
    }

    public void setUser_nicename(String user_nicename) {
        this.user_nicename = user_nicename;
    }

    public String getAvatar_thumb() {
        return avatar_thumb;
    }

    public void setAvatar_thumb(String avatar_thumb) {
        this.avatar_thumb = avatar_thumb;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getIsTicket() {
        return isTicket;
    }

    public void setIsTicket(int isTicket) {
        this.isTicket = isTicket;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public int getIsAttention() {
        return isAttention;
    }

    public void setIsAttention(int isAttention) {
        this.isAttention = isAttention;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTouid() {
        return touid;
    }

    public void setTouid(String touid) {
        this.touid = touid;
    }

    public String getStatus_msg() {
        return status_msg;
    }

    public void setStatus_msg(String status_msg) {
        this.status_msg = status_msg;
    }
}
