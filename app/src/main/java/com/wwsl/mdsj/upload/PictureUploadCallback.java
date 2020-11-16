package com.wwsl.mdsj.upload;

public interface PictureUploadCallback {
    void onSuccess(String url);

    void onFailure();
}
