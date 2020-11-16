package com.wwsl.mdsj.activity.maodou;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.luck.picture.lib.PictureSelector;
import com.umeng.commonsdk.debug.E;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.BasePagerAdapter;
import com.wwsl.mdsj.base.BaseFragment;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.views.ScrollViewPager;

import java.util.ArrayList;


public class MdOrderFragment extends BaseFragment {

    private int mType;
    private SlidingTabLayout subIndicator;
    private ArrayList<Fragment> mFragments;
    private ScrollViewPager subViewPager;
    private BasePagerAdapter pagerAdapter;
    private int mPage = 1;

    public static MdOrderFragment newInstance(int param1) {
        MdOrderFragment fragment = new MdOrderFragment();
        Bundle args = new Bundle();
        args.putInt("type", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_md_order;
    }

    @Override
    protected void init() {
        if (getArguments() != null) {
            mType = getArguments().getInt("type");
        }
        subIndicator = (SlidingTabLayout) findViewById(R.id.subIndicator);
        subViewPager = (ScrollViewPager) findViewById(R.id.subViewPager);

        mFragments = new ArrayList<>();

        String[] subTile = new String[]{"进行中", mType == TYPE_SUBSCRIBE ? "已付款" : "已打款", "已完成"};

        mFragments.add(MdOrderSubFragment.newInstance(mType, MdOrderSubFragment.ORDER_PROCESSING));
        mFragments.add(MdOrderSubFragment.newInstance(mType, MdOrderSubFragment.ORDER_CONFIRM));
        mFragments.add(MdOrderSubFragment.newInstance(mType, MdOrderSubFragment.ORDER_FINISH));

        pagerAdapter = new BasePagerAdapter(getChildFragmentManager(), subTile, mFragments);
        subViewPager.setAdapter(pagerAdapter);
        subViewPager.setOffscreenPageLimit(3);
        subIndicator.setViewPager(subViewPager, subTile);
        subIndicator.setCurrentTab(0);

    }

    @Override
    protected void initialData() {

    }

    public static final int TYPE_SALE = 0;
    public static final int TYPE_SUBSCRIBE = 1;


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (openAlbumResultListener != null) {
                openAlbumResultListener.onResult(requestCode, PictureSelector.obtainMultipleResult(data));
            }
        }
    }
}
