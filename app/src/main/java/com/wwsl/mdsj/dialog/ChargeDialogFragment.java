package com.wwsl.mdsj.dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wwsl.mdsj.HtmlConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.WebViewActivity;
import com.wwsl.mdsj.activity.me.ChargeActivity;
import com.wwsl.mdsj.adapter.ChargePageAdapter;
import com.wwsl.mdsj.bean.CoinBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.StrUtil;

/**
 * Created by cxf on 2018/10/19.
 * 直播充值弹窗
 */
public class ChargeDialogFragment extends AbsDialogFragment implements OnDialogCallBackListener {
    private ConstraintLayout protocolLayout;
    private RadioGroup mRadioGroup;
    private ViewPager mViewPager;
    private List<CoinBean> itemBeans;
    private CoinBean selectedBean;
    private ChargePageAdapter chargePageAdapter;
    private TextView tvCharge;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_charge;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        itemBeans = new ArrayList<>();
        findView();
        loadData();
    }

    private void findView() {
        mViewPager = mRootView.findViewById(R.id.viewPager);
        mRadioGroup = (RadioGroup) mRootView.findViewById(R.id.radio_group);
        protocolLayout = (ConstraintLayout) mRootView.findViewById(R.id.protocolLayout);
        tvCharge = (TextView) mRootView.findViewById(R.id.tvCharge);
        mViewPager.setOffscreenPageLimit(3);

        tvCharge.setOnClickListener(v -> {
            if (null != selectedBean)
                ChargeActivity.forward(getContext(), selectedBean);
        });

        protocolLayout.setOnClickListener(v -> WebViewActivity.forward(getContext(), HtmlConfig.WEB_LINK_CHARGE_RULE));

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mRadioGroup != null) {
                    ((RadioButton) mRadioGroup.getChildAt(position)).setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void loadData() {
        if (itemBeans.isEmpty()) {
            HttpUtil.getBalance(
                    new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0) {
                                JSONObject obj = JSON.parseObject(info[0]);
                                List<CoinBean> list = JSON.parseArray(obj.getString("rules"), CoinBean.class);
                                if (null != list) {
                                    itemBeans.clear();
                                    itemBeans.addAll(list);
                                    selectedBean = itemBeans.get(0);
                                    itemBeans.get(0).setSelect(true);
                                    showCoinList();
                                }
                            }
                        }
                    });
        }
    }

    private void showCoinList() {
        chargePageAdapter = new ChargePageAdapter(mContext, itemBeans);
        chargePageAdapter.setListener(ChargeDialogFragment.this);
        mViewPager.setAdapter(chargePageAdapter);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        for (int i = 0, size = chargePageAdapter.getCount(); i < size; i++) {
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.view_gift_indicator, mRadioGroup, false);
            radioButton.setId(i + 10000);
            if (i == 0) {
                radioButton.setChecked(true);
            }
            mRadioGroup.addView(radioButton);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void charge(String type) {
        if (StrUtil.isEmpty(type)) return;

        HttpUtil.getChargeUrl(type, "", "", selectedBean.getId(), selectedBean.getMoney(), selectedBean.getCoin(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String url = obj.getString("url");
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onDialogViewClick(View view, Object object) {
        selectedBean = (CoinBean) object;
    }
}
