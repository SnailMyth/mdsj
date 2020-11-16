package com.wwsl.mdsj.activity.me;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.androidkun.xtablayout.XTabLayout;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.CommPagerAdapter;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.fragment.FansFragment;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;


/**
 * @author :
 * @date : 2020/6/17 16:06
 * @description : 粉丝关注人页面
 */
public class FollowActivity extends BaseActivity {
    @BindView(R.id.tablayout)
    XTabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private CommPagerAdapter pagerAdapter;
    private String[] titles;
    private String uid;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_follow;
    }

    @Override
    protected void init() {
        uid = getIntent().getStringExtra(Constants.TO_UID);
        int index = getIntent().getIntExtra("index", 0);
        titles = new String[]{"关注", "好友", "粉丝"};
        fragments.add(FansFragment.newInstance(uid, Constants.TYPE_FOLLOW));
        fragments.add(FansFragment.newInstance(uid, Constants.TYPE_FRIEND));
        fragments.add(FansFragment.newInstance(uid, Constants.TYPE_FANS));

        for (String title : titles) {
            tabLayout.addTab(tabLayout.newTab().setText(title));
        }

        pagerAdapter = new CommPagerAdapter(getSupportFragmentManager(), fragments, Arrays.asList(titles));
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(pagerAdapter);

        if (0 <= index && index < 3) {
            viewPager.setCurrentItem(index);
        }

        tabLayout.setupWithViewPager(viewPager);

    }

    public static void forward(Context context, String uid, int index) {
        Intent intent = new Intent(context, FollowActivity.class);
        intent.putExtra(Constants.TO_UID, uid);
        intent.putExtra("index", index);
        context.startActivity(intent);
    }

    public void backClick(View view) {
        finish();
    }
}
