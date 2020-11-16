package com.wwsl.mdsj.im;

import cn.jpush.im.android.api.event.MessageEvent;

/**
 * Created by cxf on 2018/10/24.
 */

public class ImNotifyMsgEvent {

    private String uid;
    private String lastMessage;
    private int unReadCount;
    private String lastTime;
    private String username;
    private MessageEvent event;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public MessageEvent getEvent() {
        return event;
    }

    public void setEvent(MessageEvent event) {
        this.event = event;
    }
}
