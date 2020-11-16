package com.wwsl.mdsj.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class WishBillBean {
    private String id;
    private String uid;
    private String giftid;
    private String num;
    private String sendnum;
    private String status;
    private String addtime;
    private String type;
    private String mark;
    private String giftname;
    private String needcoin;
    private String gifticon;
    private List<SendUser> sendUsers;

    @JSONField(name = "send_users")
    public List<SendUser> getSendUsers() {
        return sendUsers;
    }

    @JSONField(name = "send_users")
    public void setSendUsers(List<SendUser> sendUsers) {
        this.sendUsers = sendUsers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getGiftid() {
        return giftid;
    }

    public void setGiftid(String giftid) {
        this.giftid = giftid;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    @JSONField(name = "send_num")
    public String getSendnum() {
        return sendnum;
    }

    @JSONField(name = "send_num")
    public void setSendnum(String sendnum) {
        this.sendnum = sendnum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getGiftname() {
        return giftname;
    }

    public void setGiftname(String giftname) {
        this.giftname = giftname;
    }

    public String getNeedcoin() {
        return needcoin;
    }

    public void setNeedcoin(String needcoin) {
        this.needcoin = needcoin;
    }

    public String getGifticon() {
        return gifticon;
    }

    public void setGifticon(String gifticon) {
        this.gifticon = gifticon;
    }

    public static class SendUser {
        private String id;
        private String uid;
        private String avatarThumb;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        @JSONField(name = "avatar_thumb")
        public String getAvatarThumb() {
            return avatarThumb;
        }

        @JSONField(name = "avatar_thumb")
        public void setAvatarThumb(String avatarThumb) {
            this.avatarThumb = avatarThumb;
        }
    }
}
