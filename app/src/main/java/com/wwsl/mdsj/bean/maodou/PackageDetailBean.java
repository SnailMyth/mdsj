package com.wwsl.mdsj.bean.maodou;

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
public class PackageDetailBean {
    /**
     * type : 1
     * name : 系统消息
     * content : 系统消息
     * unreadNum : 1
     * time : 2020-06-30 15:15:00
     */
    private int type;//0.毛豆 1.豆丁
    private String title;
    private String time;
    private boolean isIncome;
    private String num;
    private String per;
}
