package com.wwsl.mdsj.adapter;

import android.os.Parcelable;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * @author :
 * @date : 2020/6/17 15:36
 * @description : 公共view PagerAdapter
 */
public class CommPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<? extends Fragment> items;
    private List<String> mTitles;

    public CommPagerAdapter(FragmentManager fm, ArrayList<? extends Fragment> items, List<String> mTitles) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.items = items;
        this.mTitles = mTitles;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        return items.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void updateTitle(List<String> titles) {
        if (titles.size() != mTitles.size()) return;
        mTitles.clear();
        mTitles.addAll(titles);
        notifyDataSetChanged();
    }
}