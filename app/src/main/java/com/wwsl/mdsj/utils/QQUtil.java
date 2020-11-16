package com.wwsl.mdsj.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import com.wwsl.mdsj.R;

import java.util.List;

public class QQUtil {
    /**
     * 跳转到QQ客服
     */
    public static void jumpToQQ(Activity mContext, String mQQNumber) {
        // 跳转之前，可以先判断手机是否安装QQ
        if (isQQClientAvailable(mContext)) {
            // 跳转到客服的QQ
            if (TextUtils.isEmpty(mQQNumber)) {
                ToastUtil.show(R.string.qq_not_exist);
                return;
            }
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + mQQNumber;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            // 跳转前先判断Uri是否存在，如果打开一个不存在的Uri，App可能会崩溃
            if (isValidIntent(mContext, intent)) {
                mContext.startActivity(intent);
            } else {
                ToastUtil.show(R.string.system_error);
            }
        } else {
            ToastUtil.show(R.string.not_install_qq);
        }
    }


    /**
     * 判断 用户是否安装QQ客户端
     */
    private static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equalsIgnoreCase("com.tencent.qqlite")
                        || pn.equalsIgnoreCase("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断 Uri是否有效
     */
    private static boolean isValidIntent(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        return !activities.isEmpty();
    }
}
