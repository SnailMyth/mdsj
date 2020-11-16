package com.wwsl.mdsj.upload;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.wwsl.mdsj.bean.ConfigBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.L;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片上传 七牛云实现类
 */
public class PictureUploadQnImpl implements PictureUploadStrategy {
    private static final String TAG = "PictureUploadQnImpl";
    private List<File> mFiles;
    private List<String> mUrls = new ArrayList<>();
    private PictureUploadCallback mPictureUploadCallback;
    private String mToken;
    private UploadManager mUploadManager;
    private String mQiNiuHost;
    private UpCompletionHandler mPictureUpCompletionHandler;//图片上传回调

    public PictureUploadQnImpl(ConfigBean configBean) {
        mQiNiuHost = configBean.getVideoQiNiuHost();
        mPictureUpCompletionHandler = (key, info, response) -> {
            if (info.isOK()) {
                L.e("上传成功" + info.toString());
                if (mUrls.size() < mFiles.size()) {
                    String imageResultUrl = mQiNiuHost + mFiles.get(mUrls.size()).getName();
                    L.e(TAG, "图片上传结果-------->" + imageResultUrl);
                    mUrls.add(imageResultUrl);
                    if (mUrls.size() == mFiles.size()) {
                        if (mPictureUploadCallback != null) {
                            StringBuilder url = new StringBuilder();
                            for (int i = 0; i < mUrls.size(); i++) {
                                url.append(mUrls.get(i));
                                if (i < mUrls.size() - 1) {
                                    url.append(",");
                                }
                            }
                            mPictureUploadCallback.onSuccess(url.toString());
                        }
                    } else {
                        uploadFile(mFiles.get(mUrls.size()), mPictureUpCompletionHandler);
                    }
                } else {
                    if (mPictureUploadCallback != null) {
                        StringBuilder url = new StringBuilder();
                        for (int i = 0; i < mUrls.size(); i++) {
                            url.append(mUrls.get(i));
                            if (i < mUrls.size() - 1) {
                                url.append(",");
                            }
                        }
                        mPictureUploadCallback.onSuccess(url.toString());
                    }
                }
            } else {
                if (mPictureUploadCallback != null) {
                    mPictureUploadCallback.onFailure();
                }
            }

        };
    }

    @Override
    public void upload(List<File> files, PictureUploadCallback callback) {
        if (files == null || files.size() == 0 || callback == null) {
            return;
        }
        mUrls.clear();
        mFiles = files;

        mPictureUploadCallback = callback;

        HttpUtil.getQiNiuToken(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (info.length > 0) {
                        mToken = JSON.parseObject(info[0]).getString("token");
                        L.e(TAG, "-------上传的token------>" + mToken);
                        uploadFile(mFiles.get(0), mPictureUpCompletionHandler);
                    }
                }
            }
        });
    }

    /**
     * 上传文件
     */
    private void uploadFile(File file, UpCompletionHandler handler) {
        if (TextUtils.isEmpty(mToken)) {
            return;
        }
        if (mUploadManager == null) {
            mUploadManager = new UploadManager();
        }
        mUploadManager.put(file, file.getName(), mToken, handler, null);
    }

    @Override
    public void cancel() {

    }
}
