package com.wwsl.mdsj.bean.maodou;


import com.alibaba.fastjson.annotation.JSONField;

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
public class NetMwTaskBean {


    /**
     * id : 1
     * sort : 0
     * status : 1
     * catalog : 1
     * divide : 1
     * title : 体验矿机
     * image : http://ek029.wanwusl.com/data/upload/20200604/5ed870d747006.png
     * cycle : 720
     * income : 12.0000
     * power : 0.0000
     * price : 0.000
     * price_score : 0.0000
     * limit : 1
     * day : 10000
     * sales : 15
     * stock : 5999983
     * usdtprice : null
     * givehuo : 0
     * mylimitbuy : 0
     * productcount : 1
     */

    private String id;
    private String sort;
    private String status;
    private String catalog;
    private String divide;
    private String title;
    private String image;
    @JSONField(name = "cycle")
    private String activeExpired;//活跃度有效期
    private String income;
    private String power;
    private String price;
    private String price_score;
    private String limit;
    private String day;
    private String sales;
    private String stock;
    private Object usdtprice;
    private String givehuo;
    private String mylimitbuy;
    @JSONField(name = "productcount")
    private String productCount;//已经购买的数量
    @JSONField(name = "enddaycount")
    private String taskExpired;//秘籍有效期

    public static List<MwTaskBean> parse(List<NetMwTaskBean> datas) {
        if (null == datas) return null;
        List<MwTaskBean> beans = new ArrayList<>(datas.size());
        for (int i = 0; i < datas.size(); i++) {
            NetMwTaskBean data = datas.get(i);
            beans.add(MwTaskBean.builder()
                    .name(data.getTitle())
                    .haveBuy(data.getProductCount())
                    .totalBuy(data.getLimit())
                    .iconUrl(data.getImage())
                    .needNum(data.getPrice())
                    .haveGetNum("0")
                    .build());
        }

        return beans;
    }
}
