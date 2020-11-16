package com.wwsl.mdsj.custom;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;


/**
 * @author :
 * @date : 2020/6/24 9:41
 * @description :
 */
public class SimpleViewPager extends ViewPager {
    private final static String TAG = "SimpleViewPager";

    public SimpleViewPager(@NonNull Context context) {
        this(context, null);
    }

    public SimpleViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }


}
