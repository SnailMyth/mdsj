package com.wwsl.mdsj.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.LiveGiftCountAdapter;
import com.wwsl.mdsj.adapter.LiveGiftPagerAdapter;
import com.wwsl.mdsj.bean.LiveGiftBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.OnItemClickListener;
import com.wwsl.mdsj.interfaces.OnWishBillGiftSelectListener;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;

import java.util.List;

public class WishBillGiftDialogFragment extends AbsDialogFragment implements View.OnClickListener, OnItemClickListener<String>, LiveGiftPagerAdapter.ActionListener {
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private View mLoading;
    private View mBtnSend;
    private TextView mBtnChooseCount;
    private EditText etGiftNum;
    private PopupWindow mGiftCountPopupWindow;//选择分组数量的popupWindow
    private LiveGiftPagerAdapter mLiveGiftPagerAdapter;
    private LiveGiftBean mLiveGiftBean;
    private static final String DEFAULT_COUNT = "1";
    private String mCount = DEFAULT_COUNT;
    private OnWishBillGiftSelectListener onWishBillGiftSelectListener;

    public void setOnWishBillGiftSelectListener(OnWishBillGiftSelectListener onWishBillGiftSelectListener) {
        this.onWishBillGiftSelectListener = onWishBillGiftSelectListener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_wish_bill_gift;
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
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoading = mRootView.findViewById(R.id.loading);
        mBtnSend = mRootView.findViewById(R.id.btn_send);
        etGiftNum = mRootView.findViewById(R.id.etGiftNum);
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
        mRadioGroup = (RadioGroup) mRootView.findViewById(R.id.radio_group);
        mBtnSend.setOnClickListener(this);
        mBtnChooseCount.setOnClickListener(this);
        mRootView.findViewById(R.id.btn_back).setOnClickListener(this);

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
        if (mLiveGiftPagerAdapter != null) {
            mLiveGiftPagerAdapter.release();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                dismiss();
                break;
            case R.id.btn_send:
                sendGift();
                break;
            case R.id.btn_choose:
                showGiftCount();
                break;
        }
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
        if (!DEFAULT_COUNT.equals(mCount)) {
            mCount = DEFAULT_COUNT;
            mBtnChooseCount.setText(DEFAULT_COUNT);
        }
//        mBtnChooseCount.setVisibility(View.VISIBLE);
    }

    public void sendGift() {
        if (mLiveGiftBean == null) {
            ToastUtil.show(WordUtil.getString(R.string.wish_none));
            return;
        }
        if (TextUtils.isEmpty(etGiftNum.getText().toString().trim()) || Integer.parseInt(etGiftNum.getText().toString().trim()) == 0) {
            ToastUtil.show(WordUtil.getString(R.string.wish_expect_num_none));
            return;
        }
        if (onWishBillGiftSelectListener != null) {
            onWishBillGiftSelectListener.onSelectConfirm(mLiveGiftBean, Integer.parseInt(etGiftNum.getText().toString().trim()) + "");
        }
        dismiss();
    }

}
