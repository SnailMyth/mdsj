package com.wwsl.mdsj.bean.net;


import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

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
public class NetBankCardBean implements Serializable {


    /**
     * id : 1
     * uid : 10000013
     * true_name : 张明海
     * bank_card_number : 1234567899687
     * reserved_phone : 15611780929
     * addtime : 1594462305
     * bank_name :
     */
    private String id;
    private String uid;
    @JSONField(name = "true_name")
    private String realName;
    @JSONField(name = "bank_card_number")
    private String cardNumber;
    @JSONField(name = "reserved_phone")
    private String phone;
    @JSONField(name = "addtime")
    private String time;
    @JSONField(name = "bank_name")
    private String bankName;

    private boolean isShowManager;//是否显示删除图标

}
