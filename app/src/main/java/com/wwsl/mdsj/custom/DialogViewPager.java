package com.wwsl.mdsj.custom;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;


/**
 * @author :
 * @date : 2020/6/24 9:41
 * @description : 弹窗 高度取一半
 */
public class DialogViewPager extends ViewPager {


    public DialogViewPager(@NonNull Context context) {
        this(context, null);
    }

    public DialogViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize / 2, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
