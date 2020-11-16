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
public class NetFamilyActiveBean {


    /**
     * id : 102984
     * user_nicename : 吃饭睡觉打豆豆
     * avatar : http://zy888.wanwusl.com/api/upload/avatar/20200704/maodou.png
     * mobile : 13452570240
     * create_time : 2020-04-28 19:04:13
     * myactivecount : 1
     * allactivecount : 1.00
     * allusercount : 0
     * activeusercount : 0
     */

    private String id;
    @JSONField(name = "user_nicename")
    private String username;
    private String avatar;
    private String mobile;
    @JSONField(name = "create_time")
    private String createTime;//用户生成时间
    @JSONField(name = "myactivecount")
    private int myActive;//个人活跃度
    @JSONField(name = "allactivecount")
    private String familyActive;//家族活跃度
    @JSONField(name = "allusercount")
    private int userTotalCount;//总人数
    @JSONField(name = "activeusercount")
    private int totalIdentifyCount;//认证人数

    public static List<FamilyActiveBean> parse(List<NetFamilyActiveBean> data) {
        List<FamilyActiveBean> beans = new ArrayList<>();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                NetFamilyActiveBean bean = data.get(i);
                beans.add(FamilyActiveBean.builder()
                        .id(bean.getId())
                        .name(bean.getUsername())
                        .avatar(bean.getAvatar())
                        .singleActive(String.valueOf(bean.getMyActive()))
                        .allActive(bean.getFamilyActive())
                        .time(bean.getCreateTime())
                        .phone(bean.getMobile())
                        .totalNum(String.valueOf(bean.getUserTotalCount()))
                        .activeNum(String.valueOf(bean.getTotalIdentifyCount()))
                        .build());
            }
        }
        return beans;
    }
}
