package com.wwsl.mdsj.bean.net;

import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.bean.MsgShortBean;

import java.util.ArrayList;
import java.util.List;

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
public class RecommendUserBean {
    /**
     * type : 1
     * name : 系统消息
     * content : 系统消息
     * unreadNum : 1
     * time : 2020-06-30 15:15:00
     */
    private String name;
    private String uid;
    private String avatar;
    private String age;
    private String city;
    private String originDes;
    private int sex;// 0 未设置 1 男 2 女
}
