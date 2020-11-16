package com.wwsl.mdsj.bean;


import com.umeng.socialize.bean.SHARE_MEDIA;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author :
 * @date : 2020/6/17 17:38
 * @description : ShareBean
 */

@Setter
@Getter
@Builder
@AllArgsConstructor()
@NoArgsConstructor
@ToString
public class ShareBean {
    private int iconRes;
    private String text;
    private int bgRes;
    private int type;

    public ShareBean(int iconRes, String text, int bgRes) {
        this.iconRes = iconRes;
        this.text = text;
        this.bgRes = bgRes;
    }


}
