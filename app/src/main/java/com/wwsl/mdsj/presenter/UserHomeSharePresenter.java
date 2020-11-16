package com.wwsl.mdsj.presenter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;

import com.wwsl.mdsj.share.ShareUtil;
import com.wwsl.mdsj.utils.ToastUtil;

/**
 * Created by cxf on 2018/10/30.
 */

public class UserHomeSharePresenter {

    private Context mContext;
    private String mToUid;
    private String mToName;
    private String mToAvatarThumb;
    private String mFansNum;

    public UserHomeSharePresenter(Context context) {
        mContext = context;
    }

    public UserHomeSharePresenter setToUid(String toUid) {
        mToUid = toUid;
        return this;
    }

    public UserHomeSharePresenter setToName(String toName) {
        mToName = toName;
        return this;
    }

    public UserHomeSharePresenter setAvatarThumb(String avatarThumb) {
        mToAvatarThumb = avatarThumb;
        return this;
    }

    public UserHomeSharePresenter setFansNum(String fansNum) {
        mFansNum = fansNum;
        return this;
    }

    /**
     * 复制页面链接
     */
    public void copyLink() {
        if (TextUtils.isEmpty(mToUid)) {
            return;
        }
//        String link = HtmlConfig.SHARE_HOME_PAGE + mToUid;
        String link = AppConfig.getInstance().getShareText();
        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", link);
        cm.setPrimaryClip(clipData);
        ToastUtil.show(R.string.copy_success);
    }


    /**
     * 分享页面链接
     */
    public void shareHomePage(String type) {
        if (TextUtils.isEmpty(mToUid)) {
            return;
        }
        // TODO: 2020/7/11  ShareUtil

    }

    public void release() {
        mToUid = null;
        mToName = null;
        mToAvatarThumb = null;
        mFansNum = null;
    }
}
