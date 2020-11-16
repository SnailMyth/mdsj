package com.wwsl.mdsj.utils;

import android.util.SparseIntArray;

import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/10/11.
 */

public class IconUtil {
    private static SparseIntArray sLiveTypeMap;//直播间类型图标
    private static SparseIntArray sLiveLightMap;//飘心动画图片
    private static SparseIntArray sLiveGiftCountMap;//送礼物数字
    private static SparseIntArray sCashTypeMap;//提现图片
    private static List<Integer> sLinkMicPkAnim;//连麦pk帧动画

    private static List<Integer> sysMsgType;//系统消息图标
    private static List<String> sysMsgTypeName;//系统消息名称

    static {
        sLiveTypeMap = new SparseIntArray();
        sLiveTypeMap.put(Constants.LIVE_TYPE_NORMAL, R.mipmap.icon_main_live_type_0);
        sLiveTypeMap.put(Constants.LIVE_TYPE_PWD, R.mipmap.icon_main_live_type_1);
        sLiveTypeMap.put(Constants.LIVE_TYPE_PAY, R.mipmap.icon_main_live_type_2);
        sLiveTypeMap.put(Constants.LIVE_TYPE_TIME, R.mipmap.icon_main_live_type_3);

        sLiveLightMap = new SparseIntArray();
        sLiveLightMap.put(1, R.mipmap.icon_live_light_1);
        sLiveLightMap.put(2, R.mipmap.icon_live_light_2);
        sLiveLightMap.put(3, R.mipmap.icon_live_light_3);
        sLiveLightMap.put(4, R.mipmap.icon_live_light_4);
        sLiveLightMap.put(5, R.mipmap.icon_live_light_5);
        sLiveLightMap.put(6, R.mipmap.icon_live_light_6);

        sLiveGiftCountMap = new SparseIntArray();
        sLiveGiftCountMap.put(0, R.mipmap.icon_live_gift_count_0);
        sLiveGiftCountMap.put(1, R.mipmap.icon_live_gift_count_1);
        sLiveGiftCountMap.put(2, R.mipmap.icon_live_gift_count_2);
        sLiveGiftCountMap.put(3, R.mipmap.icon_live_gift_count_3);
        sLiveGiftCountMap.put(4, R.mipmap.icon_live_gift_count_4);
        sLiveGiftCountMap.put(5, R.mipmap.icon_live_gift_count_5);
        sLiveGiftCountMap.put(6, R.mipmap.icon_live_gift_count_6);
        sLiveGiftCountMap.put(7, R.mipmap.icon_live_gift_count_7);
        sLiveGiftCountMap.put(8, R.mipmap.icon_live_gift_count_8);
        sLiveGiftCountMap.put(9, R.mipmap.icon_live_gift_count_9);

        sCashTypeMap = new SparseIntArray();
        sCashTypeMap.put(Constants.CASH_ACCOUNT_ALI, R.mipmap.icon_cash_ali);
        sCashTypeMap.put(Constants.CASH_ACCOUNT_WX, R.mipmap.icon_cash_wx);
        sCashTypeMap.put(Constants.CASH_ACCOUNT_BANK, R.mipmap.icon_cash_bank);

        sLinkMicPkAnim = Arrays.asList(
                R.mipmap.pk01,
                R.mipmap.pk02,
                R.mipmap.pk03,
                R.mipmap.pk04,
                R.mipmap.pk05,
                R.mipmap.pk06,
                R.mipmap.pk07,
                R.mipmap.pk08,
                R.mipmap.pk09,
                R.mipmap.pk10,
                R.mipmap.pk11,
                R.mipmap.pk12,
                R.mipmap.pk13,
                R.mipmap.pk14,
                R.mipmap.pk15,
                R.mipmap.pk16,
                R.mipmap.pk17,
                R.mipmap.pk18,
                R.mipmap.pk19
        );

        sysMsgType = Arrays.asList(R.mipmap.icon_msg_sys,
                R.mipmap.icon_home_gonggao,
                R.mipmap.icon_home_msg_md,
                R.mipmap.icon_msg_assistant_md,
                R.mipmap.icon_msg_friend);

        sysMsgTypeName = Arrays.asList("系统消息",
                "其他消息",
                "毛豆小助手",
                "直播助手",
                "购物助手");
    }


    public static int getLiveTypeIcon(int key) {
        return sLiveTypeMap.get(key);
    }

    public static int getLiveLightIcon(int key) {
        if (key > 6 || key < 1) {
            key = 1;
        }
        return sLiveLightMap.get(key);
    }

    public static int getGiftCountIcon(int key) {
        return sLiveGiftCountMap.get(key);
    }

    public static int getSexIcon(int key) {
        return key == 1 ? R.mipmap.icon_sex_male : R.mipmap.icon_sex_female;
    }

    public static int getCashTypeIcon(int key) {
        return sCashTypeMap.get(key);
    }

    public static List<Integer> getLinkMicPkAnim() {
        return sLinkMicPkAnim;
    }

    public static int getSysMsgIconType(int type) {
        return sysMsgType.get(type - 1);
    }

    public static String getSysMsgName(int type) {
        return sysMsgTypeName.get(type - 1);
    }

}
