package com.wwsl.mdsj.base;

import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

public interface OnOpenAlbumResultListener {
    void onResult(int requestCode, List<LocalMedia> result);
}
