package com.wwsl.mdsj.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.live.LiveAnchorActivity;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;

/**
 * Created by cxf on 2018/10/9.
 * 主播直播间逻辑
 */

public class LiveAnchorViewHolder extends AbsLiveViewHolder {
    private boolean mMicEnable = true;
    private LinearLayout layoutMicEnable;
    private ImageView ivMicEnable;
    private TextView tvMicEnable;
    private ImageView mBtnFunction;
    private View mBtnGameClose;//关闭游戏的按钮
    private View mBtnPk;//主播连麦pk按钮

    public LiveAnchorViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_anchor;
    }

    @Override
    public void init() {
        super.init();
        layoutMicEnable = (LinearLayout) findViewById(R.id.layoutMicEnable);
        ivMicEnable = (ImageView) findViewById(R.id.ivMicEnable);
        tvMicEnable = (TextView) findViewById(R.id.tvMicEnable);
        mBtnFunction = (ImageView) findViewById(R.id.btn_function);
        mBtnFunction.setImageResource(R.mipmap.icon_live_func_0);
        mBtnFunction.setOnClickListener(this);
        mBtnGameClose = findViewById(R.id.btn_close_game);
        mBtnGameClose.setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        layoutMicEnable.setOnClickListener(this);
        mBtnPk = findViewById(R.id.btn_pk);
        mBtnPk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_close:
                close();
                break;
            case R.id.btn_function:
                showFunctionDialog();
                break;
            case R.id.btn_close_game:
                closeGame();
                break;
            case R.id.btn_pk:
                applyLinkMicPk();
                break;
            case R.id.layoutMicEnable:
                HttpUtil.setMic(!mMicEnable, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        ToastUtil.show(msg);
                        if (mMicEnable) {
                            mMicEnable = false;
                            ivMicEnable.setImageResource(R.mipmap.icon_mic_disable);
                            tvMicEnable.setText(WordUtil.getString(R.string.mic_disable));
                        } else {
                            mMicEnable = true;
                            ivMicEnable.setImageResource(R.mipmap.icon_mic_enable);
                            tvMicEnable.setText(WordUtil.getString(R.string.mic_enable));
                        }
                    }
                });
                break;
        }
    }

    /**
     * 设置游戏按钮是否可见
     */
    public void setGameBtnVisible(boolean show) {
        if (mBtnGameClose != null) {
            if (show) {
                if (mBtnGameClose.getVisibility() != View.VISIBLE) {
                    mBtnGameClose.setVisibility(View.VISIBLE);
                }
            } else {
                if (mBtnGameClose.getVisibility() == View.VISIBLE) {
                    mBtnGameClose.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 关闭游戏
     */
    private void closeGame() {
        ((LiveAnchorActivity) mContext).closeGame();
    }

    /**
     * 关闭直播
     */
    private void close() {
        ((LiveAnchorActivity) mContext).closeLive();
    }

    /**
     * 显示功能弹窗
     */
    private void showFunctionDialog() {
        if (mBtnFunction != null) {
            mBtnFunction.setImageResource(R.mipmap.icon_live_func_1);
            mBtnFunction.setBackgroundResource(0);
        }
        ((LiveAnchorActivity) mContext).showFunctionDialog();
    }

    /**
     * 设置功能按钮变暗
     */
    public void setBtnFunctionDark() {
        if (mBtnFunction != null) {
            mBtnFunction.setImageResource(R.mipmap.icon_live_func_0);
            mBtnFunction.setBackgroundResource(R.drawable.bg_icon_live);
        }
    }

    /**
     * 设置连麦pk按钮是否可见
     */
    public void setPkBtnVisible(boolean visible) {
        if (mBtnPk != null) {
            if (visible) {
                if (mBtnPk.getVisibility() != View.VISIBLE) {
                    mBtnPk.setVisibility(View.VISIBLE);
                    TextView btnChat = (TextView) findViewById(R.id.btn_chat);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnChat.getLayoutParams();
                    params.width = DpUtil.dp2px(120);
                    btnChat.setLayoutParams(params);
                }
            } else {
                if (mBtnPk.getVisibility() == View.VISIBLE) {
                    mBtnPk.setVisibility(View.INVISIBLE);
                    TextView btnChat = (TextView) findViewById(R.id.btn_chat);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnChat.getLayoutParams();
                    params.width = DpUtil.dp2px(180);
                    btnChat.setLayoutParams(params);
                }
            }
        }
    }

    /**
     * 发起主播连麦pk
     */
    private void applyLinkMicPk() {
        ((LiveAnchorActivity) mContext).applyLinkMicPk();
    }

}
