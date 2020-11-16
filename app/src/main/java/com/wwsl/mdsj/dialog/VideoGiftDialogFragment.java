package com.wwsl.mdsj.dialog;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.LiveGiftCountAdapter;
import com.wwsl.mdsj.adapter.LiveGiftPagerAdapter;
import com.wwsl.mdsj.bean.LiveGiftBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.OnItemClickListener;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.utils.ToastUtil;

import java.util.List;

import cn.hutool.core.util.StrUtil;

/**
 * Created by cxf on 2018/10/12.
 * 送礼物的弹窗
 */

public class VideoGiftDialogFragment extends AbsDialogFragment implements View.OnClickListener, OnItemClickListener<String>, LiveGiftPagerAdapter.ActionListener {

    private TextView mCoin;
    private TextView tvCharge;
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private View mLoading;
    private View mBtnSend;
    private View mBtnSendGroup;
    private TextView mBtnChooseCount;
    private PopupWindow mGiftCountPopupWindow;//选择分组数量的popupWindow
    private LiveGiftPagerAdapter mLiveGiftPagerAdapter;
    private LiveGiftBean mLiveGiftBean;
    private static final String DEFAULT_COUNT = "1";
    private String mCount = DEFAULT_COUNT;
    private String videoId;
    private String videoUid;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_video_gift;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
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

    @SuppressLint("HandlerLeak")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCoin = (TextView) mRootView.findViewById(R.id.coin);
        tvCharge = mRootView.findViewById(R.id.tvCharge);
        mLoading = mRootView.findViewById(R.id.loading);
        mBtnSend = mRootView.findViewById(R.id.btn_send);
        mBtnSendGroup = mRootView.findViewById(R.id.btn_send_group);
        mBtnChooseCount = (TextView) mRootView.findViewById(R.id.btn_choose);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(5);
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
        mRadioGroup = mRootView.findViewById(R.id.radio_group);
        mBtnSend.setOnClickListener(this);
        mBtnChooseCount.setOnClickListener(this);
        tvCharge.setOnClickListener(this);

        Bundle bundle = getArguments();
        if (bundle != null) {
            videoId = bundle.getString(Constants.VIDEO_DS_ID);
            videoUid = bundle.getString(Constants.VIDEO_DS_UID);
        }
        loadData();
    }

    private void loadData() {
        List<LiveGiftBean> giftList = AppConfig.getInstance().getGiftList();
        if (giftList == null) {
            HttpUtil.getGiftList(new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        List<LiveGiftBean> list = JSON.parseArray(obj.getString("giftlist"), LiveGiftBean.class);
                        AppConfig.getInstance().setGiftList(list);
                        showGiftList(list);
                        mCoin.setText(obj.getString("coin"));
                    }
                }

                @Override
                public void onFinish() {
                    if (mLoading != null) {
                        mLoading.setVisibility(View.INVISIBLE);
                    }
                }
            });
        } else {
            for (LiveGiftBean bean : giftList) {
                bean.setChecked(false);
            }
            mLoading.setVisibility(View.INVISIBLE);
            showGiftList(giftList);
            HttpUtil.getCoin(new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        mCoin.setText(JSONObject.parseObject(info[0]).getString("coin"));
                    }
                }
            });
        }
    }

    private void showGiftList(List<LiveGiftBean> list) {
        mLiveGiftPagerAdapter = new LiveGiftPagerAdapter(mContext, list);
        mLiveGiftPagerAdapter.setActionListener(this);
        mViewPager.setAdapter(mLiveGiftPagerAdapter);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        for (int i = 0, size = mLiveGiftPagerAdapter.getCount(); i < size; i++) {
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
        if (mGiftCountPopupWindow != null) {
            mGiftCountPopupWindow.dismiss();
        }
        HttpUtil.cancel(HttpConst.GET_GIFT_LIST);
        HttpUtil.cancel(HttpConst.GET_COIN);
        HttpUtil.cancel(HttpConst.SEND_GIFT);
        if (mLiveGiftPagerAdapter != null) {
            mLiveGiftPagerAdapter.release();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
            case R.id.btn_send_lian:
                sendGift();
                break;
            case R.id.btn_choose:
                showGiftCount();
                break;
            case R.id.tvCharge:
                forwardMyCoin();
                break;
        }
    }

    /**
     * 跳转到我的钻石
     */
    private void forwardMyCoin() {
        dismiss();
        //ChargeActivity.forward(mContext);
        openChangeWindow();
    }

    /**
     * 显示分组数量
     */
    private void showGiftCount() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.view_gift_count, null);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, true));
        LiveGiftCountAdapter adapter = new LiveGiftCountAdapter(mContext);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        mGiftCountPopupWindow = new PopupWindow(v, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mGiftCountPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mGiftCountPopupWindow.setOutsideTouchable(true);
        mGiftCountPopupWindow.showAtLocation(mBtnChooseCount, Gravity.BOTTOM | Gravity.RIGHT, DpUtil.dp2px(70), DpUtil.dp2px(40));
    }

    /**
     * 隐藏分组数量
     */
    private void hideGiftCount() {
        if (mGiftCountPopupWindow != null) {
            mGiftCountPopupWindow.dismiss();
        }
    }


    @Override
    public void onItemClick(String bean, int position) {
        mCount = bean;
        mBtnChooseCount.setText(bean);
        hideGiftCount();
    }

    @Override
    public void onItemChecked(LiveGiftBean bean) {
        mLiveGiftBean = bean;
        mBtnSend.setEnabled(true);
        if (!DEFAULT_COUNT.equals(mCount)) {
            mCount = DEFAULT_COUNT;
            mBtnChooseCount.setText(DEFAULT_COUNT);
        }
        if (bean.getType() == LiveGiftBean.TYPE_DELUXE) {
            if (mBtnChooseCount != null && mBtnChooseCount.getVisibility() == View.VISIBLE) {
                mBtnChooseCount.setVisibility(View.INVISIBLE);
            }
        } else {
            if (mBtnChooseCount != null && mBtnChooseCount.getVisibility() != View.VISIBLE) {
                mBtnChooseCount.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 赠送礼物
     */
    public void sendGift() {
        if (StrUtil.isEmpty(videoId) || StrUtil.isEmpty(videoUid)) {
            ToastUtil.show("视频获取失败,请重试");
            dismiss();
            return;
        }

        HttpUtil.sendVideoGift(videoId, videoUid, mLiveGiftBean.getId(), mCount, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String coin = obj.getString("coin");
                    if (!StrUtil.isEmpty(coin)) {
                        mCoin.setText(coin);
                    }
                    ToastUtil.show("打赏成功");
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }


    public void openChangeWindow() {
        ChargeDialogFragment fragment = new ChargeDialogFragment();
        fragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "ChargeDialogFragment");
    }
}
