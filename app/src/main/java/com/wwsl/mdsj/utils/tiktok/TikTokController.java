package com.wwsl.mdsj.utils.tiktok;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.controller.BaseVideoController;

/**
 * @author :
 * @date : 2020/6/18 15:37
 * @description : 抖音
 */
public class TikTokController extends BaseVideoController {

    public TikTokController(@NonNull Context context) {
        super(context);
    }

    public TikTokController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TikTokController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public boolean showNetWarning() {
        //不显示移动网络播放警告
        return false;
    }




    @Override
    protected void setProgress(int duration, int position) {
        super.setProgress(duration, position);
    }
}

