package com.wwsl.mdsj.bean.maodou;

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
public class TaskProcessBean {
    /**
     * type : 1
     * name : 系统消息
     * content : 系统消息
     * unreadNum : 1
     * time : 2020-06-30 15:15:00
     */
    private int type;
    private String iconUrl;
    private String name;
    private String iconName;
    private String activeTime;//活跃有效期
    private String active;//活跃度
    private String expireTime;//过期时间
    private long percent;
    private String time;
}
