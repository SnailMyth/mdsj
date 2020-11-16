package com.wwsl.mdsj.dialog;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.live.LiveActivity;
import com.wwsl.mdsj.activity.live.LiveAudienceActivity;
import com.wwsl.mdsj.adapter.LiveGiftCountAdapter;
import com.wwsl.mdsj.adapter.LiveGiftPagerAdapter;
import com.wwsl.mdsj.bean.LiveGiftBean;
import com.wwsl.mdsj.bean.LiveGuardInfo;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.OnItemClickListener;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.utils.ToastUtil;

import java.util.List;

/**
 * Created by cxf on 2018/10/12.
 * 送礼物的弹窗
 */

public class LiveGiftDialogFragment extends AbsDialogFragment implements View.OnClickListener, OnItemClickListener<String>, LiveGiftPagerAdapter.ActionListener {

    private TextView mCoin;
    private TextView tvCharge;
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private View mLoading;
    private View mBtnSend;
    private View mBtnSendGroup;
    private View mBtnSendLian;
    private TextView mBtnChooseCount;
    private PopupWindow mGiftCountPopupWindow;//选择分组数量的popupWindow
    private LiveGiftPagerAdapter mLiveGiftPagerAdapter;
    private LiveGiftBean mLiveGiftBean;
    private static final String DEFAULT_COUNT = "1";
    private String mCount = DEFAULT_COUNT;
    private String mLiveUid;
    private String mStream;
    private Handler mHandler;
    private int mLianCountDownCount;//连送倒计时的数字
    private TextView mLianText;
    private static final int WHAT_LIAN = 100;
    private boolean mShowLianBtn;//是否显示了连送按钮
    private LiveGuardInfo mLiveGuardInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_gift;
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

    public void setLiveGuardInfo(LiveGuardInfo liveGuardInfo) {
        mLiveGuardInfo = liveGuardInfo;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCoin = (TextView) mRootView.findViewById(R.id.coin);
        tvCharge = mRootView.findViewById(R.id.tvCharge);
        mLoading = mRootView.findViewById(R.id.loading);
        mBtnSend = mRootView.findViewById(R.id.btn_send);
        mBtnSendGroup = mRootView.findViewById(R.id.btn_send_group);
        mBtnSendLian = mRootView.findViewById(R.id.btn_send_lian);
        mBtnChooseCount = (TextView) mRootView.findViewById(R.id.btn_choose);
        mLianText = (TextView) mRootView.findViewById(R.id.lian_text);
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

        mRadioGroup = (RadioGroup) mRootView.findViewById(R.id.radio_group);
        mBtnSend.setOnClickListener(this);
        mBtnSendLian.setOnClickListener(this);
        mBtnChooseCount.setOnClickListener(this);
        tvCharge.setOnClickListener(this);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mLianCountDownCount--;
                if (mLianCountDownCount == 0) {
                    hideLianBtn();
                } else {
                    if (mLianText != null) {
                        mLianText.setText(mLianCountDownCount + "s");
                        if (mHandler != null) {
                            mHandler.sendEmptyMessageDelayed(WHAT_LIAN, 1000);
                        }
                    }
                }
            }
        };
        Bundle bundle = getArguments();
        if (bundle != null) {
            mLiveUid = bundle.getString(Constants.LIVE_UID);
            mStream = bundle.getString(Constants.LIVE_STREAM);
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
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
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
        openGiftWindow();
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
        hideLianBtn();
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
        if (TextUtils.isEmpty(mLiveUid) || TextUtils.isEmpty(mStream) || mLiveGiftBean == null) {
            return;
        }
        if (mLiveGuardInfo != null) {
            if (mLiveGiftBean.getMark() == LiveGiftBean.MARK_GUARD && mLiveGuardInfo.getMyGuardType() != Constants.GUARD_TYPE_YEAR) {
                ToastUtil.show(R.string.guard_gift_tip);
                return;
            }
        }
        SendGiftCallback callback = new SendGiftCallback(mLiveGiftBean);
        HttpUtil.sendGift(mLiveUid, mStream, mLiveGiftBean.getId(), mCount, callback);
    }

    /**
     * 隐藏连送按钮
     */
    private void hideLianBtn() {
        mShowLianBtn = false;
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_LIAN);
        }
        if (mBtnSendLian != null && mBtnSendLian.getVisibility() == View.VISIBLE) {
            mBtnSendLian.setVisibility(View.INVISIBLE);
        }
        if (mBtnSendGroup != null && mBtnSendGroup.getVisibility() != View.VISIBLE) {
            mBtnSendGroup.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示连送按钮
     */
    private void showLianBtn() {
        if (mLianText != null) {
            mLianText.setText("5s");
        }
        mLianCountDownCount = 5;
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_LIAN);
            mHandler.sendEmptyMessageDelayed(WHAT_LIAN, 1000);
        }
        if (mShowLianBtn) {
            return;
        }
        mShowLianBtn = true;
        if (mBtnSendGroup != null && mBtnSendGroup.getVisibility() == View.VISIBLE) {
            mBtnSendGroup.setVisibility(View.INVISIBLE);
        }
        if (mBtnSendLian != null && mBtnSendLian.getVisibility() != View.VISIBLE) {
            mBtnSendLian.setVisibility(View.VISIBLE);
        }
    }


    private class SendGiftCallback extends HttpCallback {

        private LiveGiftBean mGiftBean;

        public SendGiftCallback(LiveGiftBean giftBean) {
            mGiftBean = giftBean;
        }

        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                if (info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String coin = obj.getString("coin");
                    UserBean u = AppConfig.getInstance().getUserBean();
                    if (u != null) {
                        u.setLevel(obj.getIntValue("level"));
                        u.setCoin(coin);
                    }
                    if (mCoin != null) {
                        mCoin.setText(coin);
                    }
                    if (mContext != null && mGiftBean != null) {
                        ((LiveActivity) mContext).onCoinChanged(coin);
                        ((LiveActivity) mContext).sendGiftMessage(mGiftBean, obj.getString("gifttoken"));
                    }
                    if (mLiveGiftBean.getType() == LiveGiftBean.TYPE_NORMAL) {
                        showLianBtn();
                    }
                }
            } else {
                hideLianBtn();
                ToastUtil.show(msg);
            }
        }
    }

    public void openGiftWindow() {
        ChargeDialogFragment fragment = new ChargeDialogFragment();
        fragment.show(((LiveAudienceActivity) mContext).getSupportFragmentManager(), "ChargeDialogFragment");
    }

}
