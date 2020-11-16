package com.wwsl.mdsj.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.live.LiveActivity;
import com.wwsl.mdsj.activity.live.LiveAudienceActivity;

/**
 * Created by cxf on 2018/10/9.
 * 观众直播间逻辑
 */

public class LiveAudienceViewHolder extends AbsLiveViewHolder {

    private String mLiveUid;
    private String mStream;

    public LiveAudienceViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_audience;
    }

    @Override
    public void init() {
        super.init();
        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.btnRedPack).setOnClickListener(this);
        findViewById(R.id.btn_gift).setOnClickListener(this);
    }

    public void setLiveInfo(String liveUid, String stream) {
        mLiveUid = liveUid;
        mStream = stream;
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
            case R.id.btnRedPack:
                ((LiveActivity) mContext).openRedPackSendWindow();
                break;
            case R.id.btn_gift:
                openGiftWindow();
                break;
        }
    }

    /**
     * 退出直播间
     */
    private void close() {
        ((LiveAudienceActivity) mContext).onBackPressed();
    }


    /**
     * 打开礼物窗口
     */
    private void openGiftWindow() {
        ((LiveAudienceActivity) mContext).openGiftWindow();
    }


}
