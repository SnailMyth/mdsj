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
@AllArgsConstructor()
@NoArgsConstructor
@ToString
public class FarmerWelfareBean {


    /**
     * id : 3385
     * uid : 10000020
     * total : 990
     * addtime : 2020-07-28 14:30
     * from : 视频打赏
     * user_nicename : 司空朋姬
     * avatar :
     * note : 一级分佣
     * money : 99.00
     */

    private String id;
    private String uid;
    private String total;
    private String addtime;
    private String from;
    private String user_nicename;
    private String avatar;
    private String note;
    private String money;
}
