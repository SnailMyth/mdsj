package com.wwsl.mdsj.share;


import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wwsl.mdsj.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/19.
 */

public class ShareItemBean {

    private String mType;
    private int mIcon1;
    private int mIcon2;
    private int mName;
    private boolean mChecked;

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public int getName() {
        return mName;
    }

    public void setName(int name) {
        mName = name;
    }

    public int getIcon1() {
        return mIcon1;
    }

    public void setIcon1(int icon1) {
        mIcon1 = icon1;
    }

    public int getIcon2() {
        return mIcon2;
    }

    public void setIcon2(int icon2) {
        mIcon2 = icon2;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }


    /**
     * 登录类型
     */
    public static List<ShareItemBean> getLoginTypeList(String[] types) {
        List<ShareItemBean> list = new ArrayList<>();
        if (types != null) {
            for (String type : types) {
                ShareItemBean bean = new ShareItemBean();
                bean.setType(type);
                switch (type) {
                    case "qq":
                        bean.setIcon1(R.mipmap.icon_login_qq);
                        break;
                    case "wx":
                        bean.setIcon1(R.mipmap.icon_login_wx);
                        break;
                    case "facebook":
                        bean.setIcon1(R.mipmap.icon_login_fb);
                        break;
                }
                list.add(bean);
            }
        }
        return list;
    }

    /**
     * 开播前分享类型
     */
    public static List<ShareItemBean> getLiveReadyShareTypeList(String[] types) {
        List<ShareItemBean> list = new ArrayList<>();
        if (types != null) {
            for (String type : types) {
                ShareItemBean bean = new ShareItemBean();
                bean.setType(type);
                switch (type) {
                    case "qq":
                        bean.setIcon1(R.mipmap.icon_share_qq_1);
                        bean.setIcon2(R.mipmap.icon_share_qq_2);
                        break;
                    case "qzone":
                        bean.setIcon1(R.mipmap.icon_share_qzone_1);
                        bean.setIcon2(R.mipmap.icon_share_qzone_2);
                        break;
                    case "wx":
                        bean.setIcon1(R.mipmap.icon_share_wx_1);
                        bean.setIcon2(R.mipmap.icon_share_wx_2);
                        break;
                    case "wchat":
                        bean.setIcon1(R.mipmap.icon_share_pyq_1);
                        bean.setIcon2(R.mipmap.icon_share_pyq_2);
                        break;

                }
                list.add(bean);
            }
        }
        return list;
    }


    /**
     * 直播间分享类型
     */
    public static List<ShareItemBean> getLiveShareTypeList(String[] types) {
        List<ShareItemBean> list = new ArrayList<>();
        if (types != null) {
            for (String type : types) {
                ShareItemBean bean = new ShareItemBean();
                bean.setType(type);
                switch (type) {
                    case "qq":
                        bean.setIcon1(R.mipmap.icon_share_qq_1);
                        bean.setIcon2(R.mipmap.icon_share_qq_2);
                        bean.setName(R.string.share_qq);
                        break;
                    case "qzone":
                        bean.setIcon1(R.mipmap.icon_share_qzone_1);
                        bean.setIcon2(R.mipmap.icon_share_qzone_2);
                        bean.setName(R.string.share_qq);
                        break;
                    case "wx":
                        bean.setIcon1(R.mipmap.icon_share_wx_1);
                        bean.setIcon2(R.mipmap.icon_share_wx_2);
                        bean.setName(R.string.share_wx);
                        break;
                    case "wchat":
                        bean.setIcon1(R.mipmap.icon_share_pyq_1);
                        bean.setIcon2(R.mipmap.icon_share_pyq_2);
                        bean.setName(R.string.share_wx_circle);
                        break;
                }
                list.add(bean);
            }
        }
        return list;
    }


    /**
     * 视频分享类型
     */
    public static List<ShareItemBean> getVideoShareTypeList(String[] types) {
        List<ShareItemBean> list = new ArrayList<>();
        if (types != null) {
            for (String type : types) {
                ShareItemBean bean = new ShareItemBean();
                bean.setType(type);
                switch (type) {
                    case "qq":
                        bean.setIcon1(R.mipmap.icon_share_qq_1);
                        bean.setIcon2(R.mipmap.icon_share_qq_2);
                        break;
                    case "qzone":
                        bean.setIcon1(R.mipmap.icon_share_qzone_1);
                        bean.setIcon2(R.mipmap.icon_share_qzone_2);
                        break;
                    case "wx":
                        bean.setIcon1(R.mipmap.icon_share_wx_1);
                        bean.setIcon2(R.mipmap.icon_share_wx_2);
                        break;
                    case "wchat":
                        bean.setIcon1(R.mipmap.icon_share_pyq_1);
                        bean.setIcon2(R.mipmap.icon_share_pyq_2);
                        break;
                }
                list.add(bean);
            }
        }
        return list;
    }

    public static SHARE_MEDIA getShareMedia(String mType) {
        switch (mType) {
            case "wx":
                return SHARE_MEDIA.WEIXIN;
            case "wchat":
                return SHARE_MEDIA.WEIXIN_CIRCLE;
            case "qq":
                return SHARE_MEDIA.QQ;
            case "qzone":
                return SHARE_MEDIA.QZONE;
        }

        return null;
    }
}
