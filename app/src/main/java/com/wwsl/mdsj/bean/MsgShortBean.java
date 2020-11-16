package com.wwsl.mdsj.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@AllArgsConstructor()
@NoArgsConstructor
@ToString
public class MsgShortBean implements MultiItemEntity {
    private int type;
    private int subType;
    private String name;
    private String uid;
    private String avatar;
    private String unreadNum;
    private String time;
    private String content;
    private String age;
    private String city;
    private String originDes;
    private int sex;// 0 未设置 1 男 2 女
    private boolean isFollow;
    private String adUrl;
    private String adThumb;

    @Override
    public int getItemType() {
        return type;
    }
}
