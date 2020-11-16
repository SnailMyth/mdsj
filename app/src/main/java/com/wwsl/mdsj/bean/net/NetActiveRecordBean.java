package com.wwsl.mdsj.bean.net;

import com.alibaba.fastjson.annotation.JSONField;
import com.wwsl.mdsj.bean.ActiveShowBean;

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
public class NetActiveRecordBean {


    /**
     * id : 59
     * type : 已折减
     * action : sell_maodou
     * uid : 10000005
     * touid : 10000005
     * totalmaodou : 160.0000
     * addtime : 2020/07/18 09:22
     * mark : 0
     * status : 1
     * remark : null
     * amount : -160.00
     */

    private String id;
    private String type;//发放状态
    private String action;
    private String uid;
    private String touid;
    @JSONField(name = "totalmaodou")
    private String totalMd;//毛豆总数
    @JSONField(name = "addtime")
    private String time;
    private String mark;
    private String status;
    private String remark;//变动原因
    private String amount;

    public static List<ActiveShowBean> parse(List<NetActiveRecordBean> data) {
        List<ActiveShowBean> beans = new ArrayList<>();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                NetActiveRecordBean bean = data.get(i);
                beans.add(ActiveShowBean.builder()
                        .id(bean.getId())
                        .des(bean.getRemark())
                        .changeNum(bean.getAmount())
                        .time(bean.getTime())
                        .action(bean.getAction())
                        .status(bean.getType())
                        .build());
            }
        }
        return beans;
    }
}
