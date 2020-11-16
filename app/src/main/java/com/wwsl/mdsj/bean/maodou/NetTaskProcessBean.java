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
public class NetTaskProcessBean {


    /**
     * id : 78
     * mid : 2020071115365210000005
     * status : 1
     * source : 1
     * username : 13595250427
     * product : 2
     * divide : 3
     * name : 微型矿机
     * cycle : 720
     * income : 15
     * power : 0.030000000000
     * price : 12.000000000000
     * profit : 0.000000000000
     * count : 0
     * profit_at : 0000-00-00 00:00:00
     * create_at : 2020-07-11 15:36:52
     * daycount : 30
     * img : http://ek029.wanwusl.com/data/upload/20200604/5ed870f81fc06.png
     * givehuo : 2.00
     * last_timeall : 178
     * dayshen : 1
     * width : 5.97
     */

    private String id;
    private String mid;
    private String status;
    private String source;
    private String username;
    private String product;
    private String divide;
    private String name;
    private String cycle;
    private int income;
    private String power;
    private String price;
    private String profit;
    private String count;
    private String profit_at;
    @JSONField(name = "create_at")
    private String time;
    private int daycount;
    @JSONField(name = "img")
    private String iconUrl;
    @JSONField(name = "givehuo")
    private String active;
    @JSONField(name = "last_timeall")
    private int expireTime;
    @JSONField(name = "dayshen")
    private int activeTime;//活跃有效期
    @JSONField(name = "width")
    private double percent;

    private boolean isHistory;
}
