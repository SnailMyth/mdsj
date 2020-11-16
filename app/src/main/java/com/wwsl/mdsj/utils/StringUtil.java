package com.wwsl.mdsj.utils;

import android.text.TextUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import cn.hutool.core.util.StrUtil;

/**
 * Created by cxf on 2018/9/28.
 */

public class StringUtil {
    private static DecimalFormat sDecimalFormat;
    private static DecimalFormat sDecimalFormat2;
    // private static Pattern sPattern;
    private static Pattern sIntPattern;


    static {
        sDecimalFormat = new DecimalFormat("#.#");
        sDecimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        sDecimalFormat2 = new DecimalFormat("#.##");
        sDecimalFormat2.setRoundingMode(RoundingMode.DOWN);
        //sPattern = Pattern.compile("[\u4e00-\u9fa5]");
        sIntPattern = Pattern.compile("^[-\\+]?[\\d]*$");
    }

    public static String format(double value) {
        return sDecimalFormat.format(value);
    }

    /**
     * 把数字转化成多少万
     */
    public static String toWan(long num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        return sDecimalFormat.format(num / 10000d) + "W";
    }


    /**
     * 把数字转化成多少万
     */
    public static String toWan2(long num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        return sDecimalFormat.format(num / 10000d);
    }

    /**
     * 把数字转化成多少万
     */
    public static String toWan3(long num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        return sDecimalFormat2.format(num / 10000d) + "w";
    }

//    /**
//     * 判断字符串中是否包含中文
//     */
//    public static boolean isContainChinese(String str) {
//        Matcher m = sPattern.matcher(str);
//        if (m.find()) {
//            return true;
//        }
//        return false;
//    }

    /**
     * 判断一个字符串是否是数字
     */
    public static boolean isInt(String str) {
        return sIntPattern.matcher(str).matches();
    }


    /**
     * 把一个long类型的总毫秒数转成时长
     */
    public static String getDurationText(long mms) {
        int hours = (int) (mms / (1000 * 60 * 60));
        int minutes = (int) ((mms % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) ((mms % (1000 * 60)) / 1000);
        String s = "";
        if (hours > 0) {
            if (hours < 10) {
                s += "0" + hours + ":";
            } else {
                s += hours + ":";
            }
        }
        if (minutes > 0) {
            if (minutes < 10) {
                s += "0" + minutes + ":";
            } else {
                s += minutes + ":";
            }
        } else {
            s += "00" + ":";
        }
        if (seconds > 0) {
            if (seconds < 10) {
                s += "0" + seconds;
            } else {
                s += seconds;
            }
        } else {
            s += "00";
        }
        return s;
    }

    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString 原始字符串
     * @param length      指定长度
     */
    public static List<String> getStrList(String inputString, int length) {
        int size = inputString.length() / length;
        if (inputString.length() % length != 0) {
            size += 1;
        }
        return getStrList(inputString, length, size);
    }


    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString 原始字符串
     * @param length      指定长度
     * @param size        指定列表大小
     * @return
     */
    public static List<String> getStrList(String inputString, int length,
                                          int size) {
        List<String> list = new ArrayList<String>();
        for (int index = 0; index < size; index++) {
            String childStr = substring(inputString, index * length,
                    (index + 1) * length);
            list.add(childStr);
        }
        return list;
    }


    /**
     * 分割字符串，如果开始位置大于字符串长度，返回空
     *
     * @param str 原始字符串
     * @param f   开始位置
     * @param t   结束位置
     * @return
     */
    public static String substring(String str, int f, int t) {
        if (f > str.length())
            return null;
        if (t > str.length()) {
            return str.substring(f, str.length());
        } else {
            return str.substring(f, t);
        }
    }

    /**
     * 是否是英文
     *
     * @param charaString
     * @return
     */

    public static boolean isEnglish(String charaString) {
        return charaString.matches("^[a-zA-Z]*");

    }


    public static String replaceBank(String str) {
        if (StrUtil.isEmpty(str)) return null;
        else return str.trim().replaceAll("\\s*|\t|\r|\n", "");
    }

    //是否是数字
    public static boolean isInteger(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    //是否是钱
    public static boolean isMoney(String str) {
        if (null == str || "".equals(str) || "-".startsWith(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static String getDistance(String distance) {
        if (StrUtil.isEmpty(distance)) return "";

        if (!isInteger(distance)) return distance;

        String res = "";
        res = distance + "m";
        int i = Integer.parseInt(distance);
        if (100 <= i && i < 1000) {
            res = String.format("0.%skm", i / 10);
        } else if (i >= 1000) {
            res = String.format("%s.%skm", i / 1000, i % 1000);
        }

        return res;
    }

    final static Map<Integer, String> zoneNum = new HashMap<Integer, String>();

    static {
        zoneNum.put(11, "北京");
        zoneNum.put(12, "天津");
        zoneNum.put(13, "河北");
        zoneNum.put(14, "山西");
        zoneNum.put(15, "内蒙古");
        zoneNum.put(21, "辽宁");
        zoneNum.put(22, "吉林");
        zoneNum.put(23, "黑龙江");
        zoneNum.put(31, "上海");
        zoneNum.put(32, "江苏");
        zoneNum.put(33, "浙江");
        zoneNum.put(34, "安徽");
        zoneNum.put(35, "福建");
        zoneNum.put(36, "江西");
        zoneNum.put(37, "山东");
        zoneNum.put(41, "河南");
        zoneNum.put(42, "湖北");
        zoneNum.put(43, "湖南");
        zoneNum.put(44, "广东");
        zoneNum.put(45, "广西");
        zoneNum.put(46, "海南");
        zoneNum.put(50, "重庆");
        zoneNum.put(51, "四川");
        zoneNum.put(52, "贵州");
        zoneNum.put(53, "云南");
        zoneNum.put(54, "西藏");
        zoneNum.put(61, "陕西");
        zoneNum.put(62, "甘肃");
        zoneNum.put(63, "青海");
        zoneNum.put(64, "新疆");
        zoneNum.put(71, "台湾");
        zoneNum.put(81, "香港");
        zoneNum.put(82, "澳门");
        zoneNum.put(91, "外国");
    }

    final static int[] PARITYBIT = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
    final static int[] POWER_LIST = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10,
            5, 8, 4, 2};

    public static boolean isIDCard(String certNo) {
        if (certNo == null || (certNo.length() != 15 && certNo.length() != 18))
            return false;
        final char[] cs = certNo.toUpperCase().toCharArray();
        //校验位数
        int power = 0;
        for (int i = 0; i < cs.length; i++) {
            if (i == cs.length - 1 && cs[i] == 'X')
                break;//最后一位可以 是X或x
            if (cs[i] < '0' || cs[i] > '9')
                return false;
            if (i < cs.length - 1) {
                power += (cs[i] - '0') * POWER_LIST[i];
            }
        }

        //校验区位码
        if (!zoneNum.containsKey(Integer.valueOf(certNo.substring(0, 2)))) {
            return false;
        }

        //校验年份
        String year = null;
        year = certNo.length() == 15 ? getIdCardCalendar(certNo) : certNo.substring(6, 10);


        final int iyear = Integer.parseInt(year);
        if (iyear < 1900 || iyear > Calendar.getInstance().get(Calendar.YEAR))
            return false;//1900年的PASS，超过今年的PASS

        //校验月份
        String month = certNo.length() == 15 ? certNo.substring(8, 10) : certNo.substring(10, 12);
        final int imonth = Integer.parseInt(month);
        if (imonth < 1 || imonth > 12) {
            return false;
        }

        //校验天数
        String day = certNo.length() == 15 ? certNo.substring(10, 12) : certNo.substring(12, 14);
        final int iday = Integer.parseInt(day);
        if (iday < 1 || iday > 31)
            return false;

        //校验"校验码"
        if (certNo.length() == 15)
            return true;
        return cs[cs.length - 1] == PARITYBIT[power % 11];
    }

    private static String getIdCardCalendar(String certNo) {
        // 获取出生年月日
        String birthday = certNo.substring(6, 12);
        SimpleDateFormat ft = new SimpleDateFormat("yyMMdd", Locale.CHINA);
        Date birthdate = null;
        try {
            birthdate = ft.parse(birthday);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        Calendar cday = Calendar.getInstance();
        cday.setTime(birthdate);
        return String.valueOf(cday.get(Calendar.YEAR));
    }


    //数字
    public static final String REG_NUMBER = ".*\\d+.*";
    //小写字母
    public static final String REG_UPPERCASE = ".*[A-Z]+.*";
    //大写字母
    public static final String REG_LOWERCASE = ".*[a-z]+.*";

    public static boolean checkPasswordRule(String password) {

        String passRegex = "^(?![0-9]+$)(?![a-zA-Z]+$)(?!([^(0-9a-zA-Z)]|[\\(\\)])+$)([^(0-9a-zA-Z)]|[\\(\\)]|[a-zA-Z]|[0-9]){6,16}$";

//        //密码为空或者长度6-20位则返回false
//        if (password == null || password.length() < 6 || password.length() > 20) return false;
//        int i = 0;
//        if (password.matches(REG_NUMBER)) i++;
//        if (password.matches(REG_LOWERCASE)) i++;
//        if (password.matches(REG_UPPERCASE)) i++;
//
//        if (i < 2) return false;

        return !TextUtils.isEmpty(password) && password.matches(passRegex);
    }


    public static int checkNullNumberInt(String str) {
        int i = 0;
        if (isInteger(str)) {
            i = Integer.parseInt(str);
        }
        return i;
    }

    public static String checkNullNumberStr(String str) {
        return isInteger(str) ? str : "0";
    }

    public static String checkNullStr(String str) {
        return isEmpty(str) ? "" : str;
    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

}
