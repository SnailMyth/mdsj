package com.wwsl.mdsj.bean.net;

import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.activity.maodou.MdOrderFragment;

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
public class MdOrderShowBean {

    private int type;//订单,卖单
    private int subType;//进行中,已付款,已完成
    private String id;
    private String title;
    private String status;
    private boolean success;
    private String reason;
    private String orderNum;
    private String time;
    private String totalPrice;
    private String payImgUrl;//打款凭证url
    private String qrUrl;//收款码url
    private String statusStr;

    public static List<MdOrderShowBean> parse(List<NetMdOrderBean> data, int type, int subType) {
        List<MdOrderShowBean> beans = new ArrayList<>();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                NetMdOrderBean temp = data.get(i);
                beans.add(MdOrderShowBean.builder()
                        .id(temp.getId())
                        .type(type)
                        .subType(subType)
                        .orderNum(temp.getTid())
                        .status(temp.getStatus())
                        .success(true)
                        .title(String.format("%s毛豆 X%s", type == MdOrderFragment.TYPE_SALE ? "出售" : "购买", temp.getNumber()))
                        .time(temp.getTime())
                        .totalPrice(temp.getAllprice())
                        .payImgUrl(temp.getPayImgUrl())
                        .qrUrl(temp.getQrUrl())
                        .success(temp.getOrderRes() == Constants.ORDER_DEFAULT_SUCCESS)
                        .reason(temp.getFailReason())
                        .statusStr(temp.getStatusStr())
                        .build());
            }
        }
        return beans;
    }
}
