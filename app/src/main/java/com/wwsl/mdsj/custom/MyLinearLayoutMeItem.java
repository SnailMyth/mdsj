package com.wwsl.mdsj.custom;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.utils.DpUtil;

public class MyLinearLayoutMeItem extends LinearLayout {

    private float mScreenWidth;
    private int mSpanCount;

    public MyLinearLayoutMeItem(Context context) {
        this(context, null);
    }

    public MyLinearLayoutMeItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyLinearLayoutMeItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels - DpUtil.dp2px(30);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyLinearLayout2);
        mSpanCount = ta.getInteger(R.styleable.MyLinearLayout2_mll_span_count, 6);
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (mScreenWidth / mSpanCount), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
