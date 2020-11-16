package com.wwsl.mdsj.activity.maodou;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.SlidingTabLayout;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.BasePagerAdapter;
import com.wwsl.mdsj.base.BaseActivity;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class P2pOrderActivity extends BaseActivity {

    private SlidingTabLayout indicator;
    private ViewPager mViewPager;
    private ArrayList<Fragment> mFragments;
    private BasePagerAdapter pagerAdapter;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_p2p_order;
    }

    @Override
    protected void init() {
        initView();
    }

    public void backClick(View view) {
        finish();
    }

    private void initView() {
        indicator = findViewById(R.id.orderIndicator);
        mViewPager = findViewById(R.id.viewPager);
        String[] titles = new String[]{"我的订单", "我的卖单"};
        mFragments = new ArrayList<>();
        mFragments.add(MdOrderFragment.newInstance(MdOrderFragment.TYPE_SUBSCRIBE));
        mFragments.add(MdOrderFragment.newInstance(MdOrderFragment.TYPE_SALE));

        pagerAdapter = new BasePagerAdapter(getSupportFragmentManager(), titles, mFragments);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        indicator.setViewPager(mViewPager, titles);

        indicator.setCurrentTab(0);
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, P2pOrderActivity.class);
        context.startActivity(intent);
    }

}
