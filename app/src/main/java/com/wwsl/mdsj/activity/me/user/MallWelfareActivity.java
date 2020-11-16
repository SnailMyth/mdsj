package com.wwsl.mdsj.activity.me.user;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.flyco.tablayout.SlidingTabLayout;
import com.wwsl.mdsj.HtmlConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.WebViewActivity;
import com.wwsl.mdsj.adapter.CommPagerAdapter;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.bean.MallWelfareBean;
import com.wwsl.mdsj.fragment.MallFragment;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * 商城福利
 */
public class MallWelfareActivity extends BaseActivity {

    @BindView(R.id.tablayout)
    SlidingTabLayout tablayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tv_all_price)
    TextView tv_all_price;
    @BindView(R.id.tv_price1)
    TextView tv_price1;
    @BindView(R.id.tv_price2)
    TextView tv_price2;

    public void backClick(View view) {
        finish();
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_mall_welfare;
    }

    @Override
    protected void init() {
        initTabLayout();
    }

    private void initTabLayout() {
        String[] titles = new String[2];
        titles[0] = "预计福利";
        titles[1] = "已获福利";
        ArrayList<Fragment> mFragments = new ArrayList<>();
        mFragments.add(MallFragment.getInstance(MallFragment.PREDICT_WELFARE));
        mFragments.add(MallFragment.getInstance(MallFragment.HAS_WELFARE));
        CommPagerAdapter pagerAdapter = new CommPagerAdapter(getSupportFragmentManager(), mFragments, Arrays.asList(titles));
        viewPager.setAdapter(pagerAdapter);
        tablayout.setViewPager(viewPager, titles);
        tablayout.setCurrentTab(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        HttpUtil.mallWelfare("1", 1, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<MallWelfareBean> welfareBeans = JSON.parseArray(Arrays.toString(info), MallWelfareBean.class);
                    MallWelfareBean mallWelfareBean = welfareBeans.get(0);
                    int sum_price = mallWelfareBean.getSum_price();
                    int estimate_price = mallWelfareBean.getEstimate_price();
                    int obtained_price = mallWelfareBean.getObtained_price();
                    tv_all_price.setText(String.valueOf(sum_price));
                    tv_price1.setText(String.valueOf(estimate_price));
                    tv_price2.setText(String.valueOf(obtained_price));
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    public static void invoke(Activity activity) {
        Intent intent = new Intent(activity, MallWelfareActivity.class);
        activity.startActivity(intent);

    }

    public void showDes(View view) {
        WebViewActivity.forward(this, HtmlConfig.WEB_LINK_MARKET_DES);
    }
}
