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

public class LuckPanWrap2 extends FrameLayout {
    public LuckPanWrap2(@NonNull Context context) {
        super(context);
    }

    public LuckPanWrap2(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LuckPanWrap2(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
