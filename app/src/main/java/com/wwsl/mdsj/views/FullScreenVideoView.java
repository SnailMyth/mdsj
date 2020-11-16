package com.wwsl.mdsj.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;


/**
 * @author :
 * @date : 2020/6/17 16:42
 * @description : 全屏播放Videoview
 */
public class FullScreenVideoView extends VideoView {

    public FullScreenVideoView(Context context) {
        super(context);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
    }
}
