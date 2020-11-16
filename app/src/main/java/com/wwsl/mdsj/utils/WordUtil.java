package com.wwsl.mdsj.utils;

import android.content.res.Resources;
import android.text.Html;
import android.text.Spanned;

import com.wwsl.mdsj.AppContext;

/**
 * Created by cxf on 2017/10/10.
 * 获取string.xml中的字
 */

public class WordUtil {

    private static Resources sResources;

    static {
        sResources = AppContext.sInstance.getResources();
    }

    public static String getString(int res) {
        return sResources.getString(res);
    }

    /**
     * 为字符串添加颜色
     *
     * @param color #FFFFFF
     */
    public static String strAddColor(String str, String color) {
        return "<font color=\"" + color + "\">" + str + "</font>";
    }

    /**
     * 将String转化为Spanned对象
     */
    public static Spanned strToSpanned(String str) {
        return Html.fromHtml(str);
    }
}
