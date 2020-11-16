package com.wwsl.mdsj.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.me.GainGiftListActivity;
import com.wwsl.mdsj.activity.live.LiveAnchorActivity;
import com.wwsl.mdsj.activity.live.LiveAudienceActivity;
import com.wwsl.mdsj.bean.LiveBean;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.LifeCycleAdapter;
import com.wwsl.mdsj.utils.StringUtil;

import java.math.BigDecimal;

/**
 * Created by cxf on 2018/10/9.
 */

public class LiveEndViewHolder extends AbsViewHolder implements View.OnClickListener {

    private ImageView mAvatar1;
    private ImageView mAvatar2;
    private TextView mName;
    private TextView mDuration;//直播时长
    private TextView mVotes;//收获映票
    private TextView mVotesName;
    private TextView mWatchNum;//观看人数
    private String videoStream;

    public LiveEndViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_end;
    }

    @Override
    public void init() {
        mAvatar1 = (ImageView) findViewById(R.id.avatar_1);
        mAvatar2 = (ImageView) findViewById(R.id.avatar_2);
        mName = (TextView) findViewById(R.id.name);
        mDuration = (TextView) findViewById(R.id.duration);
        mVotes = (TextView) findViewById(R.id.votes);
        mVotesName = (TextView) findViewById(R.id.votes_name);
        mWatchNum = (TextView) findViewById(R.id.watch_num);
        findViewById(R.id.giftDetail).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
//        mVotesName.setText(AppConfig.getInstance().getVotesName());
        mLifeCycleListener = new LifeCycleAdapter() {
            @Override
            public void onDestroy() {
                HttpUtil.cancel(HttpConst.GET_LIVE_END_INFO);
            }
        };
    }

    public void showData(LiveBean liveBean, String stream) {
        HttpUtil.getLiveEndInfo(stream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    ;
                    mVotes.setText(StringUtil.toWan(new BigDecimal(obj.get("votes").toString()).longValue()));
                    mDuration.setText(obj.getString("length"));
                    mWatchNum.setText(StringUtil.toWan(new BigDecimal(obj.get("nums").toString()).longValue()));
//                    mWatchNum.setText(StringUtil.toWan(obj.getLongValue("")));
                    videoStream = stream;

                }
            }
        });
        if (liveBean != null) {
            mName.setText(liveBean.getUserNiceName());
            ImgLoader.displayBlur(liveBean.getAvatar(), mAvatar1);
            ImgLoader.displayAvatar(liveBean.getAvatar(), mAvatar2);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.giftDetail:
                GainGiftListActivity.forward(mContext, videoStream);
                break;
            case R.id.btn_back:
                if (mContext instanceof LiveAnchorActivity) {
                    ((LiveAnchorActivity) mContext).superBackPressed();
                } else if (mContext instanceof LiveAudienceActivity) {
                    ((LiveAudienceActivity) mContext).exitLiveRoom();
                }
                break;
        }
    }
}
