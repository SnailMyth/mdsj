package com.wwsl.mdsj.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;

public class ActionCenterViewHolder extends AbsMainViewHolder implements View.OnClickListener {
    private ActionCenterTicketViewHolder mViewHolders;
    private FrameLayout mViewPager;

    public ActionCenterViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_action_center;
    }

    @Override
    public void init() {
        mViewPager = (FrameLayout) findViewById(R.id.viewPager);
        mViewHolders = new ActionCenterTicketViewHolder(mContext, mViewPager);
        mViewHolders.addToParent();
        findViewById(R.id.btn_ticket_sell).setOnClickListener(this);
        findViewById(R.id.btn_ticket_history).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ticket_sell:
                mViewHolders.refreshData(Constants.TICKET_CENTER_ING);
                break;
            case R.id.btn_ticket_history:
                mViewHolders.refreshData(Constants.TICKET_CENTER_ED);
                break;
        }
    }

    @Override
    public void loadData() {
        mViewHolders.loadData();
    }
}
