package com.wwsl.mdsj.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.utils.WordUtil;

public class BuyTicketConfirmViewHolder extends AbsViewHolder implements View.OnClickListener {
    private int type = 0;
    private boolean mShowed;
    private BuyTicketConfirmListener buyTicketConfirmListener;
    private LayoutInflater mInflater;
    private TextView tvBuyConfirmTitle;

    public BuyTicketConfirmViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_buy_ticket_confirm;
    }

    @Override
    public void init() {
        mInflater = LayoutInflater.from(mContext);
        findViewById(R.id.root).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        tvBuyConfirmTitle = (TextView) findViewById(R.id.tvBuyConfirmTitle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.root:
                removeFromParent();
                break;
            case R.id.btn_confirm:
                removeFromParent();
                if (buyTicketConfirmListener != null) {
                    if (type == 0) {
                        buyTicketConfirmListener.onConfirm();
                    } else {
                        buyTicketConfirmListener.onError();
                    }
                }
                break;
        }
    }

    @Override
    public void addToParent() {
        super.addToParent();
        mShowed = true;
    }

    @Override
    public void removeFromParent() {
        super.removeFromParent();
        mShowed = false;
    }

    public boolean isShowed() {
        return mShowed;
    }

    public void showConfirm() {
        tvBuyConfirmTitle.setText(WordUtil.getString(R.string.ticket_buy_confirm_title));
        type = 0;
    }

    public void showError(String msg) {
        tvBuyConfirmTitle.setText(msg);
        type = 1;
    }

    public void setBuyTicketConfirmListener(BuyTicketConfirmListener listener) {
        buyTicketConfirmListener = listener;
    }

    public interface BuyTicketConfirmListener {
        void onConfirm();

        void onError();
    }
}
