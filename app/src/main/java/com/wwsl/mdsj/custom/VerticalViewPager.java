package com.wwsl.mdsj.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.frame.fire.util.LogUtils;
import com.frame.fire.view.viewpager.ViewPager;


/**
 * @author :
 * @date : 2020/6/20 11:22
 * @description : VerticalViewPager
 */
public class VerticalViewPager extends ViewPager {

    private final static String TAG = "VerticalViewPager";

    public VerticalViewPager(Context context) {
        super(context);
        init();
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // The majority of the magic happens here
        setPageTransformer(true, new VerticalPageTransformer());
    }


    /**
     * Swaps the X and Y coordinates of your touch event.
     */
    private MotionEvent swapXY(MotionEvent ev) {
        float width = getWidth();
        float height = getHeight();
        float newX = (ev.getY() / height) * width;
        float newY = (ev.getX() / width) * height;
        ev.setLocation(newX, newY);
        return ev;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = super.onInterceptTouchEvent(swapXY(ev));
        swapXY(ev); // return touch coordinates to original reference frame for any child views

        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                x1 = ev.getX();
                y1 = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //当前页下标
                float x2 = ev.getX();
                float y2 = ev.getY();
                float absY = Math.abs(y2 - y1);
                float absX = Math.abs(x2 - x1);
                if (absY > absX) {
                    if (absY > 100) {
                        LogUtils.e(TAG, "大于100 响应滑动");
                        intercepted = true;
                    }
                }
                break;
        }

        LogUtils.e(TAG, "onInterceptTouchEvent: " + ev.getAction() + "-->intercepted:" + intercepted);

        return intercepted;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getChildCount() == 0) {
            return false;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean b = super.onTouchEvent(swapXY(ev));
        LogUtils.e(TAG, "onTouchEvent: " + b);
        return b;
    }

    private float x1;
    private float y1;


    @Override
    public boolean canScrollHorizontally(int direction) {
        return false;
    }


    /**
     * 竖直滑动效果
     */
    private static class VerticalPageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View view, float position) {
            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);
            } else if (position <= 1) { // [-1,1]
                view.setAlpha(1);
                // Counteract the default slide transition
                view.setTranslationX(view.getWidth() * -position);
                //set Y position to swipe in from top
                float yPosition = position * view.getHeight();
                view.setTranslationY(yPosition);
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

}
