package com.wwsl.mdsj.activity.maodou;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.flyco.tablayout.SlidingTabLayout;
import com.wwsl.mdsj.HtmlConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.WebViewActivity;
import com.wwsl.mdsj.adapter.CommPagerAdapter;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.custom.SimpleViewPager;

import java.util.ArrayList;
import java.util.Arrays;

public class MaoDouFoundationActivity extends BaseActivity {
    private ArrayList<Fragment> fragments;
    private ProcessingFragment processingFragment;
    private TodayFragment todayFragment;
    private MingWenTaskFragment mingWenTaskFragment;
    private HistoryTaskFragment historyTaskFragment;
    private SlidingTabLayout tabLayout;
    private SimpleViewPager viewPager;
    private CommPagerAdapter pagerAdapter;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_mao_dou_fundation;
    }

    @Override
    protected void init() {
        initView();
    }

    public void backClick(View view) {
        finish();
    }

    public void showRule(View view) {
        WebViewActivity.forward(this, HtmlConfig.WEB_LINK_MD_RULE);
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, MaoDouFoundationActivity.class);
        context.startActivity(intent);
    }


    private void initView() {
        tabLayout = (SlidingTabLayout) findViewById(R.id.tabLayout);
        viewPager = (SimpleViewPager) findViewById(R.id.viewpager);


        fragments = new ArrayList<>();
        String[] titles = new String[4];

        titles[0] = "进行中";
        processingFragment = ProcessingFragment.newInstance();
        fragments.add(processingFragment);

        titles[1] = "今日进度";
        todayFragment = TodayFragment.newInstance();
        fragments.add(todayFragment);

        titles[2] = "毛豆农场";
        mingWenTaskFragment = MingWenTaskFragment.newInstance();
        fragments.add(mingWenTaskFragment);

        titles[3] = "历史任务";
        historyTaskFragment = HistoryTaskFragment.newInstance();
        fragments.add(historyTaskFragment);

        pagerAdapter = new CommPagerAdapter(getSupportFragmentManager(), fragments, Arrays.asList(titles));
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setViewPager(viewPager, titles);
        tabLayout.setCurrentTab(0);
    }
}
