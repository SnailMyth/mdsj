package com.wwsl.game.custom;

import android.content.Context;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by cxf on 2018/11/5.
 */

public class LuckPanWrap extends FrameLayout {

    private float mScale;

    public LuckPanWrap(@NonNull Context context) {
        super(context);
    }

    public LuckPanWrap(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LuckPanWrap(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScale=context.getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize / 2 + dp2px(60), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int dp2px(int dpVal) {
        return (int) (mScale + dpVal + 0.5f);
    }
}
