package com.wwsl.mdsj.bean.net;

import com.wwsl.mdsj.bean.SystemMessageBean;

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
public class SysMsgBean {
    /**
     * type : 1
     * name : 系统消息
     * content : 系统消息
     * unreadNum : 1
     * time : 2020-06-30 15:15:00
     */
    private int type;
    private String name;
    private String content;
    private String time;

    public static List<SysMsgBean> parse(List<SystemMessageBean> data, int type) {
        List<SysMsgBean> beans = new ArrayList<>();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                beans.add(SysMsgBean.builder().time(data.get(i).getAddtime()).content(data.get(i).getContent()).type(type).build());
            }
        }
        return beans;
    }
}
