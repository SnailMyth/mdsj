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
public class FamilyActiveBean {
    private String id;
    private String name;
    private String avatar;
    private String singleActive;
    private String allActive;
    private String time;
    private String totalNum;//所有人数
    private String activeNum;//有效人数
    private String phone;
}
