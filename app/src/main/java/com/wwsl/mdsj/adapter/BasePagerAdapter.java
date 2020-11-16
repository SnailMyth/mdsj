package com.wwsl.mdsj.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class BasePagerAdapter extends FragmentStatePagerAdapter {

    private List<String> titles;
    private List<Fragment> mFragments;

    public BasePagerAdapter(FragmentManager fm, String[] titles, List<Fragment> mFragments) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.titles = Arrays.asList(titles);
        this.mFragments = mFragments;
    }

    @NotNull
    @Override
    public Fragment getItem(int i) {
        return mFragments.get(i);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

}
