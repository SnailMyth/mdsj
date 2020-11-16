package com.wwsl.mdsj.utils.tiktok;

import com.wwsl.mdsj.utils.StringUtil;

import java.text.DecimalFormat;

import cn.hutool.core.util.StrUtil;

/**
 * @author :
 * @date : 2020/6/17 15:40
 * @description : NumUtils
 */
public class NumUtils {

    /**
     * 数字上万过滤
     *
     * @return
     */
    public static String numberFilter(int number) {
        if (number > 9999 && number <= 999999) {  //数字上万，小于百万，保留一位小数点
            DecimalFormat df2 = new DecimalFormat("##.#");
            String format = df2.format((float) number / 10000);
            return format + "万";
        } else if (number > 999999 && number < 99999999) {  //百万到千万不保留小数点
            return number / 10000 + "万";
        } else if (number > 99999999) { //上亿
            DecimalFormat df2 = new DecimalFormat("##.#");
            String format = df2.format((float) number / 100000000);
            return format + "亿";
        } else {
            return number + "";
        }
    }

    public static String numberFilter2(int number) {
        if (number > 9999 && number <= 999999) {  //数字上万，小于百万，保留一位小数点
            DecimalFormat df2 = new DecimalFormat("##.#");
            String format = df2.format((float) number / 10000);
            return format + "w";
        } else if (number > 999999 && number < 99999999) {  //百万到千万不保留小数点
            return number / 10000 + "w";
        } else if (number > 99999999) { //上亿
            DecimalFormat df2 = new DecimalFormat("##.#");
            String format = df2.format((float) number / 100000000);
            return format + "亿";
        } else {
            return number + "";
        }
    }

    public static String numberFilter(String number) {
        if (!StringUtil.isInteger(number)) {
            return "0";
        }
        return numberFilter(Integer.parseInt(number));
    }

    public static String numberFilter2(String number) {
        if (!StringUtil.isInteger(number)) {
            return "0";
        }
        return numberFilter2(Integer.parseInt(number));
    }
}
