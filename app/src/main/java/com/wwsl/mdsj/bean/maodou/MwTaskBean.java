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
public class MwTaskBean {
    /**
     * type : 1
     * name : 系统消息
     * content : 系统消息
     * unreadNum : 1
     * time : 2020-06-30 15:15:00
     */
    private String name;
    private String iconUrl;
    private String title;
    private String haveBuy;
    private String totalBuy;
    private String needNum;
    private String haveGetNum;
}
