package com.wwsl.mdsj.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class HomeListHallItemLayout extends RelativeLayout {

    public HomeListHallItemLayout(Context context) {
        super(context);
    }

    public HomeListHallItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeListHallItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        float widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (widthSize * 13 / 9), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
