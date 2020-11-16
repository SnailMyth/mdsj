package com.wwsl.mdsj.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.live.LiveActivity;
import com.wwsl.mdsj.activity.me.ChargeActivity;
import com.wwsl.mdsj.adapter.GuardRightAdapter;
import com.wwsl.mdsj.bean.GuardBuyBean;
import com.wwsl.mdsj.bean.GuardRightBean;
import com.wwsl.mdsj.bean.LiveGuardInfo;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by cxf on 2018/11/6.
 * 直播间购买守护弹窗
 */

public class LiveGuardBuyDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private RadioButton[] mRadioBtns;
    private TextView[] mPrices;
    private TextView mCoinNameTextView;
    private TextView mCoin;
    private String mCoinName;
    private View mBtnBuy;
    private List<GuardRightBean> mRightList;//权限列表
    private List<GuardBuyBean> mBuyList;//商品列表
    private GuardRightAdapter mGuardRightAdapter;
    private long mCoinVal;//余额
    private String mLiveUid;
    private String mStream;
    private LiveGuardInfo mLiveGuardInfo;
    private GuardBuyBean mTargetBuyBean;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_guard_buy;
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

    public void setLiveGuardInfo(LiveGuardInfo info) {
        mLiveGuardInfo = info;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRadioBtns = new RadioButton[3];
        mRadioBtns[0] = (RadioButton) mRootView.findViewById(R.id.btn_1);
        mRadioBtns[1] = (RadioButton) mRootView.findViewById(R.id.btn_2);
        mRadioBtns[2] = (RadioButton) mRootView.findViewById(R.id.btn_3);
        mPrices = new TextView[3];
        mPrices[0] = (TextView) mRootView.findViewById(R.id.price_1);
        mPrices[1] = (TextView) mRootView.findViewById(R.id.price_2);
        mPrices[2] = (TextView) mRootView.findViewById(R.id.price_3);
        mCoinNameTextView = (TextView) mRootView.findViewById(R.id.coin_name);
        mCoin = (TextView) mRootView.findViewById(R.id.coin);
        mBtnBuy = mRootView.findViewById(R.id.btn_buy);
        mRadioBtns[0].setOnClickListener(this);
        mRadioBtns[1].setOnClickListener(this);
        mRadioBtns[2].setOnClickListener(this);
        mBtnBuy.setOnClickListener(this);
        mCoin.setOnClickListener(this);
        ((RadioGroup) mRootView.findViewById(R.id.rgType)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.btn_1:
                        mPrices[0].setTextColor(Color.WHITE);
                        mPrices[1].setTextColor(getResources().getColor(R.color.color_shouhu_money));
                        mPrices[2].setTextColor(getResources().getColor(R.color.color_shouhu_money));
                        break;
                    case R.id.btn_2:
                        mPrices[0].setTextColor(getResources().getColor(R.color.color_shouhu_money));
                        mPrices[1].setTextColor(Color.WHITE);
                        mPrices[2].setTextColor(getResources().getColor(R.color.color_shouhu_money));
                        break;
                    case R.id.btn_3:
                        mPrices[0].setTextColor(getResources().getColor(R.color.color_shouhu_money));
                        mPrices[1].setTextColor(getResources().getColor(R.color.color_shouhu_money));
                        mPrices[2].setTextColor(Color.WHITE);
                        break;
                    default:
                        break;
                }
            }
        });
        Bundle bundle = getArguments();
        if (bundle != null) {
            mLiveUid = bundle.getString(Constants.LIVE_UID);
            mStream = bundle.getString(Constants.STREAM);
            String coinName = bundle.getString(Constants.COIN_NAME);
            mCoinName = coinName;
            mCoinNameTextView.setText(WordUtil.getString(R.string.guard_my) + coinName + ":");
        }
        HttpUtil.getGuardBuyList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    mRightList = JSON.parseArray(obj.getString("privilege"), GuardRightBean.class);
                    mBuyList = JSON.parseArray(obj.getString("list"), GuardBuyBean.class);
                    long coin = new BigDecimal(obj.getString("coin")).longValue();
                    mCoinVal = coin;
                    mCoin.setText(String.valueOf(mCoinVal));
                    int buyListSize = mBuyList.size();
                    for (int i = 0, length = mPrices.length; i < length; i++) {
                        if (i < buyListSize) {
                            GuardBuyBean buyBean = mBuyList.get(i);
                            mRadioBtns[i].setText(buyBean.getName());
                            mPrices[i].setText(String.valueOf(buyBean.getCoin()));
                        }
                    }
                    refreshList(0);
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }


    private void refreshList(int index) {
        if (mRightList != null && mBuyList != null) {
            for (GuardRightBean rightBean : mRightList) {
                rightBean.setChecked(false);
            }
            if (mBuyList.size() > index) {
                GuardBuyBean buyBean = mBuyList.get(index);
                mTargetBuyBean = buyBean;
                int[] privilege = buyBean.getPrivilege();
                for (int i : privilege) {
                    if (mRightList.size() > i) {
                        GuardRightBean rightBean = mRightList.get(i);
                        rightBean.setChecked(true);
                        if (i == 0) {
                            if (buyBean.getType() == Constants.GUARD_TYPE_YEAR) {
                                rightBean.setIconIndex(1);
                            } else {
                                rightBean.setIconIndex(0);
                            }
                        } else {
                            rightBean.setIconIndex(1);
                        }
                    }
                }
                if (AppConfig.grayGuardIcon()) {
                    if (index == 0) {
                        for (GuardRightBean rightBean : mRightList) {
                            rightBean.setChecked(false);
                            rightBean.setIconIndex(0);
                        }
                    } else if (index == 1) {
                        if (mRightList.size() > 0) {
                            mRightList.get(0).setChecked(true);
                            mRightList.get(0).setIconIndex(1);
                        }
                    }
                }
                if (mGuardRightAdapter == null) {
                    mGuardRightAdapter = new GuardRightAdapter(mContext, mRightList);
                    mRecyclerView.setAdapter(mGuardRightAdapter);
                } else {
                    mGuardRightAdapter.notifyDataSetChanged();
                }

                if (mCoinVal < buyBean.getCoin()) {
                    ToastUtil.show("当前余额不足,请充值");
                }

                mBtnBuy.setEnabled(mCoinVal >= buyBean.getCoin());
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_1:
                refreshList(0);
                break;
            case R.id.btn_2:
                refreshList(1);
                break;
            case R.id.btn_3:
                refreshList(2);
                break;
            case R.id.btn_buy:
                clickBuyGuard();
                break;
            case R.id.coin:
                forwardMyCoin();
                break;
        }
    }

    /**
     * 跳转到我的钻石
     */
    private void forwardMyCoin() {
        dismiss();
        ChargeActivity.forward(mContext);
    }

    /**
     * 点击购买守护按钮
     */
    private void clickBuyGuard() {
        if (TextUtils.isEmpty(mLiveUid) || TextUtils.isEmpty(mStream) || mLiveGuardInfo == null || mTargetBuyBean == null) {
            return;
        }
        if (mLiveGuardInfo.getMyGuardType() > mTargetBuyBean.getType()) {
            if (AppConfig.guardWordModify()) {
                DialogUtil.showSimpleTipDialog(mContext, WordUtil.getString(R.string.guard_buy_tip_new));
            } else {
                DialogUtil.showSimpleTipDialog(mContext, WordUtil.getString(R.string.guard_buy_tip));
            }
            return;
        } else {
            if (mLiveGuardInfo.getMyGuardType() == Constants.GUARD_TYPE_MONTH
                    && mTargetBuyBean.getType() == Constants.GUARD_TYPE_YEAR) {
                DialogUtil.showSimpleDialog(mContext, WordUtil.getString(R.string.guard_buy_tip_2), new DialogUtil.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        doBuyGuard();
                    }
                });
                return;
            }
        }
        buyGuard();
    }

    /**
     * 购买守护
     */
    private void buyGuard() {
        if (mTargetBuyBean == null) {
            return;
        }
        DialogUtil.showSimpleDialog(mContext,
                String.format(WordUtil.getString(R.string.guard_buy_tip_3), mTargetBuyBean.getCoin(), mCoinName, mTargetBuyBean.getShopName()),
                new DialogUtil.SimpleCallback() {

                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        doBuyGuard();
                    }
                });
    }

    /**
     * 购买守护
     */
    private void doBuyGuard() {
        if (mTargetBuyBean == null) {
            return;
        }
        HttpUtil.buyGuard(mLiveUid, mStream, mTargetBuyBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String votes = obj.getString("votestotal");//主播当前的映票数
                    int guardNum = obj.getIntValue("guard_nums");//主播当前的守护人数
                    int guardType = obj.getIntValue("type");
                    if (mLiveGuardInfo != null) {
                        mLiveGuardInfo.setMyGuardType(guardType);
                        mLiveGuardInfo.setMyGuardEndTime(obj.getString("endtime"));
                        mLiveGuardInfo.setGuardNum(guardNum);
                    }
                    mCoinVal = new BigDecimal(obj.getString("coin")).longValue();
                    String coinString = String.valueOf(mCoinVal);
                    mCoin.setText(coinString);
                    UserBean u = AppConfig.getInstance().getUserBean();
                    if (u != null) {
                        u.setCoin(coinString);
                        u.setLevel(obj.getIntValue("level"));
                    }
                    ((LiveActivity) mContext).sendBuyGuardMessage(votes, guardNum, guardType);
                    dismiss();
                }
                ToastUtil.show(msg);
            }
        });
    }


    @Override
    public void onDestroy() {
        mLiveGuardInfo = null;
        HttpUtil.cancel(HttpConst.GET_GUARD_BUY_LIST);
        super.onDestroy();
    }
}
