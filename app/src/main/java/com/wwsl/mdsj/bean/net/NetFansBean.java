package com.wwsl.mdsj.bean.net;


import com.alibaba.fastjson.annotation.JSONField;

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
public class NetFansBean {
    /**
     * isattention : 0
     * name : 用户不存在
     * uid : 102958
     * avatar : http://ek023.wanwusl.com//default.jpg
     * age : 0
     * city : 0
     * sex : 0
     * addtime : 0
     */
    @JSONField(name = "isattention")
    private int attention;
    private String name;
    private String uid;
    private String avatar;
    private String age;
    private String city;
    private String sex;
    private String addtime;
    private String signature;
}
