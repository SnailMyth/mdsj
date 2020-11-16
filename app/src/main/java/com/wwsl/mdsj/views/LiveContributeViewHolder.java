package com.wwsl.mdsj.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.live.LiveAudienceActivity;
import com.wwsl.mdsj.activity.live.LiveContributeActivity;

/**
 * Created by cxf on 2018/10/15.
 * 直播间礼物贡献榜
 */

public class LiveContributeViewHolder extends AbsLivePageViewHolder implements View.OnClickListener {
    private String mLiveUid;
    private MainListContributeViewHolder mainListContributeViewHolder;

    public LiveContributeViewHolder(Context context, ViewGroup parentView, String liveUid) {
        super(context, parentView);
        mLiveUid = liveUid;
        mainListContributeViewHolder.setGiftContribute(true, mLiveUid);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_contribute;
    }

    @Override
    public void init() {
        super.init();
        mainListContributeViewHolder = new MainListContributeViewHolder(mContext, (LinearLayout) findViewById(R.id.layoutContribute));
//        mainListContributeViewHolder.setGiftContribute(true, mLiveUid);
        mainListContributeViewHolder.addToParent();

        findViewById(R.id.btn_con_day).setOnClickListener(this);
        findViewById(R.id.btn_con_week).setOnClickListener(this);
        findViewById(R.id.btn_con_month).setOnClickListener(this);
        findViewById(R.id.btn_con_all).setOnClickListener(this);
    }

    @Override
    public void loadData() {
        mainListContributeViewHolder.loadData();
    }

    @Override
    public void release() {
        super.release();
        if (mainListContributeViewHolder != null) {
            mainListContributeViewHolder.removeFromParent();
        }
    }

    @Override
    public void hide() {
        if (mContext instanceof LiveContributeActivity) {
            ((LiveContributeActivity) mContext).onBackPressed();
        } else {
            super.hide();
        }
    }

    @Override
    public void onHide() {
        if (AppConfig.liveRoomScroll() && mContext != null && mContext instanceof LiveAudienceActivity) {
            ((LiveAudienceActivity) mContext).setScrollFrozen(false);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_con_day:
                mainListContributeViewHolder.refreshData(AbsMainListViewHolder.DAY);
                break;
            case R.id.btn_con_week:
                mainListContributeViewHolder.refreshData(AbsMainListViewHolder.WEEK);
                break;
            case R.id.btn_con_month:
                mainListContributeViewHolder.refreshData(AbsMainListViewHolder.MONTH);
                break;
            case R.id.btn_con_all:
                mainListContributeViewHolder.refreshData(AbsMainListViewHolder.TOTAL);
                break;
        }
    }
}
