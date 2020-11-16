package com.wwsl.mdsj.activity.main;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import androidx.viewpager.widget.ViewPager;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.CommPagerAdapter;
import com.wwsl.mdsj.base.BaseFragment;
import com.wwsl.mdsj.event.MainPageChangeEvent;
import com.wwsl.mdsj.fragment.MainHomeFragment;
import com.wwsl.mdsj.fragment.PersonalHomeFragment;
import com.wwsl.mdsj.interfaces.LifeCycleAdapter;
import com.wwsl.mdsj.interfaces.LifeCycleListener;
import com.wwsl.mdsj.views.AbsMainViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * MainActivity 首页
 */

public class MainTestViewHolder extends AbsMainViewHolder {
    private final static String TAG = "MainTestViewHolder";


    public MainTestViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    public void init() {

    }

}
