package com.wwsl.mdsj.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserInfo {
    private String id;
    private String user_nicename;
    private String avatar;
    private String avatar_thumb;
    private String sex;
    private String signature;
    private String coin;
    private String consumption;
    private String votestotal;
    private String province;
    private String city;
    private String birthday;
    private String user_status;
    private String issuper;
    private String level;
    private String level_anchor;
}
