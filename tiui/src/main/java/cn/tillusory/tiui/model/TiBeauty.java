package cn.tillusory.tiui.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import cn.tillusory.tiui.R;

/**
 * Created by Anko on 2018/11/22.
 * Copyright (c) 2018 拓幻科技 - tillusory.cn. All rights reserved.
 */
public enum TiBeauty {
    WHITENING(R.string.skin_whitening, R.drawable.ic_ti_whitening),
    BLEMISH_REMOVAL(R.string.skin_blemish_removal, R.drawable.ic_ti_blemish_removal),
    SATURATION(R.string.skin_saturation, R.drawable.ic_ti_saturation),
    TENDERNESS(R.string.skin_tenderness, R.drawable.ic_ti_tenderness);

    private int stringId;
    private int imageId;

    TiBeauty(@StringRes int stringId, @DrawableRes int imageId) {
        this.stringId = stringId;
        this.imageId = imageId;
    }

    public String getString(@NonNull Context context) {
        return context.getResources().getString(stringId);
    }

    public Drawable getImageDrawable(@NonNull Context context) {
        return context.getResources().getDrawable(imageId);
    }
}
