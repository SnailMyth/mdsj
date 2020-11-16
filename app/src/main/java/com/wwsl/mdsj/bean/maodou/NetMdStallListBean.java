package com.wwsl.mdsj.bean.maodou;

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
public class NetMdStallListBean {
    /**
     * id : 133
     * target : 10000005
     * number : 20
     * price : 1.20
     * imgurl :
     * nickname : 杨光
     * allamount : 24.00
     */

    private String id;
    private String target;
    private String number;
    private String price;
    @JSONField(name = "imgurl")
    private String imgUrl;
    @JSONField(name = "nickname")
    private String nickName;
    @JSONField(name = "allamount")
    private String allAmount;


}
