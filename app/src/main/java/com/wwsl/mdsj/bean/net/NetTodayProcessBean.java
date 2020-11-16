package com.wwsl.mdsj.bean.net;

import com.alibaba.fastjson.annotation.JSONField;
import com.wwsl.mdsj.bean.ActiveShowBean;
import com.wwsl.mdsj.bean.maodou.ViewVideoHistoryBean;

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
public class NetTodayProcessBean {

    private String id;
    private String activity_num;
    @JSONField(name = "watch_time")
    private String watchTime;
    @JSONField(name = "addtime")
    private String time;
    private String uid;
    private String num;
    private String nowtime;
    private String status;
    @JSONField(name = "watchtime")
    private String des;
    @JSONField(name = "watchprogress")
    private String progress;

    public static List<ViewVideoHistoryBean> parse(List<NetTodayProcessBean> data) {
        List<ViewVideoHistoryBean> beans = new ArrayList<>();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                NetTodayProcessBean bean = data.get(i);
                beans.add(ViewVideoHistoryBean.builder()
                        .title(bean.getDes())
                        .percent(bean.getProgress())
                        .time(bean.getTime())
                        .build());
            }
        }
        return beans;
    }
}
