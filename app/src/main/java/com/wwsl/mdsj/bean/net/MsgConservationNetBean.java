package com.wwsl.mdsj.bean.net;

import android.os.Build;

import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.bean.MsgShortBean;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
public class MsgConservationNetBean {
    /**
     * type : 1
     * name : 系统消息
     * content : 系统消息
     * unreadNum : 1
     * time : 2020-06-30 15:15:00
     */
    private int type;
    private String name;
    private String content;
    private int unreadNum;
    private String time;

    public static List<MsgShortBean> parse(List<MsgConservationNetBean> data) {
        if (data == null) return null;
        List<MsgShortBean> list = new ArrayList<>();

        for (MsgConservationNetBean bean : data) {
            list.add(parse(bean));
        }
        return list;
    }

    public static MsgShortBean parse(MsgConservationNetBean data) {
        return MsgShortBean.builder()
                .type(Constants.MESSAGE_TYPE_MSG)
                .subType(data.getType())
                .content(data.getContent())
                .time(data.getTime())
                .unreadNum(String.valueOf(data.getUnreadNum()))
                .name(data.getName())
                .build();
    }
}
