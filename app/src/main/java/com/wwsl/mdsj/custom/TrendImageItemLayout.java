package com.wwsl.mdsj.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class TrendImageItemLayout extends RelativeLayout {

    public TrendImageItemLayout(Context context) {
        super(context);
    }

    public TrendImageItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TrendImageItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = widthMeasureSpec;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
