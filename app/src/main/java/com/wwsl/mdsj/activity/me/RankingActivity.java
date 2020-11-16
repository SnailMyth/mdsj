package com.wwsl.mdsj.activity.me;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import androidx.viewpager.widget.ViewPager;
import android.view.View;

import com.flyco.tablayout.SlidingTabLayout;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.activity.main.RankFragment;
import com.wwsl.mdsj.adapter.BasePagerAdapter;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.WordUtil;

import java.util.ArrayList;

public class RankingActivity extends AbsActivity implements View.OnClickListener {
    private SlidingTabLayout mIndicator;
    private ViewPager mViewPager;
    private ArrayList<Fragment> mFragments;

    public static void forward(Context context) {
        context.startActivity(new Intent(context, RankingActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ranking;
    }

    @Override
    protected void main() {
        initView();
    }

    private void initView() {
        mIndicator = findViewById(R.id.indicator);
        mViewPager = findViewById(R.id.viewPager);


        String[] titles = {
                WordUtil.getString(R.string.main_list_star),
                WordUtil.getString(R.string.main_list_contribute),
                WordUtil.getString(R.string.main_list_magnate),
                WordUtil.getString(R.string.main_list_gambler)
        };
        mFragments = new ArrayList<>();
        mFragments.add(RankFragment.getInstance(RankFragment.RANK_STAR));
        mFragments.add(RankFragment.getInstance(RankFragment.RANK_CONTRIBUTE));
        mFragments.add(RankFragment.getInstance(RankFragment.RANK_MAGNATE));
        mFragments.add(RankFragment.getInstance(RankFragment.RANK_GAMBLER));

        BasePagerAdapter pagerAdapter = new BasePagerAdapter(((AbsActivity) mContext).getSupportFragmentManager(), titles, mFragments);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                ((RankFragment) mFragments.get(mViewPager.getCurrentItem())).onResume();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mViewPager.setAdapter(pagerAdapter);
        mIndicator.setViewPager(mViewPager, titles, this, mFragments);

    }

    public void openQuestion(View v) {
        DialogUtil.showContentTipsDialog(mContext, "说明", "内容说明内容说明内容说明内容说明内容说明内容说明内容说明内容说明内容说明内容说明内容说明内容说明内容说明内容说明内容说明内容说明内容说明内容说明", new DialogUtil.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                dialog.dismiss();
            }
        });
    }


    @Override
    public void onClick(View v) {

    }

    public void loadData(int type) {

    }
}
