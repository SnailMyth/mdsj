package com.wwsl.mdsj.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class HomeListLivingItemLayout extends RelativeLayout {

    public HomeListLivingItemLayout(Context context) {
        super(context);
    }

    public HomeListLivingItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeListLivingItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float widthSize = MeasureSpec.getSize(widthMeasureSpec);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (widthSize * 42 / 33), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}

