package com.wwsl.mdsj.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.lxj.xpopup.XPopup;
import com.wwsl.mdsj.AppContext;
import com.wwsl.mdsj.dialog.VersionUpdateDialog;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;

import java.io.File;

/**
 * Created by cxf on 2017/10/9.
 */

public class VersionUtil {

    private static String sVersion;

    /**
     * 是否是最新版本
     */
    public static boolean isLatest(String version) {
        if (TextUtils.isEmpty(version)) {
            return true;
        }
        String curVersion = getVersion();
        if (TextUtils.isEmpty(curVersion)) {
            return true;
        }
        if (!curVersion.equals(version)) {
            return getUpdate(version, curVersion);
        } else {
            return true;
        }
    }

    // TODO: 2020/10/1/001 判断版本是否更新
    private static boolean getUpdate(String version, String curVersion) {
        if (version.contains(".") && curVersion.contains(".")) {
            String[] oleV = version.split("\\.");
            String[] newV = curVersion.split("\\.");
            if (oleV.length == 3 && newV.length == 3) {
                try {
                    int o1 = Integer.parseInt(oleV[0]);
                    int n1 = Integer.parseInt(newV[0]);
                    if (o1 > n1) return false;

                    int o2 = Integer.parseInt(oleV[1]);
                    int n2 = Integer.parseInt(newV[1]);
                    if (o1 == n1 && o2 > n2) return false;

                    int o3 = Integer.parseInt(oleV[2]);
                    int n3 = Integer.parseInt(newV[2]);
                    if (o1 == n1 && o2 == n2 && o3 > n3) return false;

                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return false;
    }


    public static void showDialog(final Context context, boolean cancelable, String version, String versionTip, OnDialogCallBackListener listener) {
        new XPopup.Builder(context)
                .hasShadowBg(false)
                .dismissOnTouchOutside(false)
                .customAnimator(new DialogUtil.DialogAnimator())
                .asCustom(new VersionUpdateDialog(context, cancelable, version, versionTip, listener))
                .show();
    }

    /**
     * 获取版本号
     */
    public static String getVersion() {
        if (TextUtils.isEmpty(sVersion)) {
            try {
                PackageManager manager = AppContext.sInstance.getPackageManager();
                PackageInfo info = manager.getPackageInfo(AppContext.sInstance.getPackageName(), 0);
                sVersion = info.versionName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sVersion;
    }

    public static void installNormal(Activity context, String path) {
        File file = new File(path);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //判断版本是否在7.0以上
            Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);//在AndroidManifest中的android:authorities值
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
            context.startActivity(install);
        } else {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(install);
        }
    }
}
