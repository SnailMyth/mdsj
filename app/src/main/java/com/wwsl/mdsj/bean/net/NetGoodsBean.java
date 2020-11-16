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
public class NetGoodsBean {

    /**
     * id : 10
     * title : 婴儿玩具宝宝益智早教儿童1岁8半八男女孩九个月幼儿一9七6月到12
     * subtitle :
     * thumb : images/1/2020/07/V0pcYPHlzbl2CLG23joqKxqNCjx8rO.jpg
     * marketprice : 11.00
     * productprice : 11.00
     * minprice : 11.00
     * maxprice : 11.00
     * isdiscount : 0
     * isdiscount_time : 0
     * isdiscount_discounts : null
     * sales : 0
     * salesreal : 0
     * total : 100
     * web_view : http://mdsjsc.wenzuxz.com/app/index.php?i=1&c=entry&m=ewei_shopv2&do=mobile&r=goods.detail&token=ef2d3689afd77efb4cadbfe37d471b14&id=10
     * is_collection 0,1
     */

    private String id;
    private String title;
    private String subtitle;
    private String thumb;
    private String marketprice;
    private String productprice;
    private String minprice;
    private String maxprice;
    private String isdiscount;
    private String isdiscount_time;
    private Object isdiscount_discounts;
    private String sales;
    private String salesreal;
    private String total;
    @JSONField(name = "web_view")
    private String webView;
    @JSONField(name = "is_collection")
    private int collection;
    @JSONField(name = "is_shelves")
    private int isAdd;

}
