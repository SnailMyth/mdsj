package com.wwsl.mdsj.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 */

public class ViewPagerAdapter extends PagerAdapter {

    private List<View> mViewList;

    public ViewPagerAdapter(List<View> list) {
        mViewList = list;
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = null;
        if (mViewList.size() > position) {
            view = mViewList.get(position);
            container.addView(view);
        }
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(mViewList.get(position));
    }

    @Override
    public void restoreState(@Nullable Parcelable state, @Nullable ClassLoader loader) {
        super.restoreState(state, loader);
    }
}
