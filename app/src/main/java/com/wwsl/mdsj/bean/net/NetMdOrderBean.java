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
public class NetMdOrderBean {


    /**
     * id : 133
     * tid : 2020071625186
     * type : 1
     * status : 0
     * number : 20
     * price : 1.200
     * charge : 0.000
     * owner :
     * target : 10000005
     * kuanimg : null
     * secret : null
     * create_at : 2020/07/16 16:09
     * paytime : 0
     * update_at : 0
     * finishtime : 0
     * shouxu : 0.00
     * jytype : 0
     * buyconfirm : 0
     * sellconfirm : 0
     * buyconfirmtime : 0
     * sellconfirmtime : 0
     * isdel : 0
     * imgurl :
     * allprice : 24.00
     * orderstatus2 : 交易中,挂单中
     */

    private String id;
    private String tid;
    private String type;
    private String status;
    private String number;
    private String price;
    private String charge;
    private String owner;
    private String target;
    @JSONField(name = "kuanimg")
    private String payImgUrl;//打款凭证url
    private String secret;
    @JSONField(name = "create_at")
    private String time;
    private String paytime;
    private String update_at;
    private String finishtime;
    private String shouxu;
    private String jytype;
    private String buyconfirm;
    private String sellconfirm;
    private String buyconfirmtime;
    private String sellconfirmtime;
    private String isdel;
    @JSONField(name = "imgurl")
    private String qrUrl;//收款码url
    private String allprice;
    @JSONField(name = "orderstatus2")
    private String statusStr;
    @JSONField(name = "jubao_status")
    private int orderRes;//订单结束状态 默认为8  表示成功完成
    @JSONField(name = "jubao_msg")
    private String failReason;//异常订单原因
}
