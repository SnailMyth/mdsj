package com.wwsl.mdsj.utils;

import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.interfaces.CommonCallback;

import java.io.File;

/**
 * Created by cxf on 2018/10/17.
 */

public class GifCacheUtil {

    public static void getFile(String fileName, String url, final CommonCallback<File> commonCallback) {
        if (commonCallback == null) {
            return;
        }
        File dir = new File(AppConfig.GIF_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fileName);

        if (file.exists()) {
            commonCallback.callback(file);
        } else {

            DownloadUtil downloadUtil = new DownloadUtil();
            downloadUtil.download(HttpConst.DOWNLOAD_GIF, dir, fileName, url, new DownloadUtil.Callback() {
                @Override
                public void onSuccess(File file) {
                    commonCallback.callback(file);
                }

                @Override
                public void onProgress(int progress) {

                }

                @Override
                public void onError(Throwable e) {
                    commonCallback.callback(null);
                }
            });
        }
    }

}
