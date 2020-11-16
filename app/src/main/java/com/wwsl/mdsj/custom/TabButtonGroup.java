package com.wwsl.mdsj.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * @author :
 * @date : 2020/6/17 11:56
 * @description : TabButtonGroup
 */

public class TabButtonGroup extends LinearLayout implements View.OnClickListener {

    private List<TabButton> mList;
    private ViewPager mViewPager;
    private int mCurPosition;
    private ValueAnimator mAnimator;
    private View mAnimView;
    private Runnable mRunnable;
    private OnNoFragmentClick onNoFragmentClick;

    public TabButtonGroup(Context context) {
        this(context, null);
    }

    public TabButtonGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabButtonGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mList = new ArrayList<>();
        mCurPosition = 0;
        mAnimator = ValueAnimator.ofFloat(0.9f, 1.1f, 1f);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                if (mAnimView != null) {
                    mAnimView.setScaleX(v);
                    mAnimView.setScaleY(v);
                }
            }
        });
        mAnimator.setDuration(300);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimView = null;
            }
        });
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(mCurPosition, true);
                }
            }
        };
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int j = 0;
        for (int i = 0, count = getChildCount(); i < count; i++) {
            View v = getChildAt(i);
            v.setOnClickListener(this);

            boolean haveLayout = ((TabButton) v).isHaveLayout();

            if (haveLayout) {
                v.setTag(j++);

                mList.add((TabButton) v);
            } else {
                v.setTag(NO_FRAGMENT_TAB_CLICK);
            }
        }
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null) {
            int position = (int) tag;
            setSelected(position);
        }
    }

    public void setSelected(int position) {
        if (position == NO_FRAGMENT_TAB_CLICK) {
            if (onNoFragmentClick != null) {
                onNoFragmentClick.onClick();
            }
        } else {
            if (position == mCurPosition) {
                return;
            }
            mList.get(mCurPosition).setChecked(false);
            TabButton tbn = mList.get(position);
            tbn.setChecked(true);
            mCurPosition = position;
            mAnimView = tbn;
            mAnimator.start();
            postDelayed(mRunnable, 150);
        }
    }


    public void setOnNoFragmentClick(OnNoFragmentClick onNoFragmentClick) {
        this.onNoFragmentClick = onNoFragmentClick;
    }

    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
    }

    public void cancelAnim() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }

    private static int NO_FRAGMENT_TAB_CLICK = -1;

    public interface OnNoFragmentClick {
        void onClick();
    }

}
