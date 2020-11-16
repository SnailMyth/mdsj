package com.wwsl.mdsj.activity.video;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.adapter.VideoReportAdapter;
import com.wwsl.mdsj.bean.VideoReportBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.KeyBoardHeightChangeListener;
import com.wwsl.mdsj.utils.KeyBoardHeightUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/12/15.
 * 视频举报
 */

public class VideoReportActivity extends AbsActivity implements VideoReportAdapter.ActionListener, KeyBoardHeightChangeListener {

    public static void forward(Context context, String videoId) {
        Intent intent = new Intent(context, VideoReportActivity.class);
        intent.putExtra(Constants.VIDEO_ID, videoId);
        context.startActivity(intent);
    }

    private String mVideoId;
    private RecyclerView mRecyclerView;
    private VideoReportAdapter mAdapter;
    private KeyBoardHeightUtil mKeyBoardHeightUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_report;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.report));
        mVideoId = getIntent().getStringExtra(Constants.VIDEO_ID);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mKeyBoardHeightUtil = new KeyBoardHeightUtil(mContext, findViewById(android.R.id.content), this);
        HttpUtil.getVideoReportList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<VideoReportBean> list = JSON.parseArray(Arrays.toString(info), VideoReportBean.class);
                    mAdapter = new VideoReportAdapter(mContext, list);
                    mAdapter.setActionListener(VideoReportActivity.this);
                    if (mRecyclerView != null) {
                        mRecyclerView.setAdapter(mAdapter);
                    }
                    if (mKeyBoardHeightUtil != null) {
                        mKeyBoardHeightUtil.start();
                    }
                }
            }
        });
    }

    @Override
    public void onReportClick(VideoReportBean bean, String text) {
        if (TextUtils.isEmpty(mVideoId)) {
            return;
        }
        if (bean == null) {
            ToastUtil.show(R.string.video_report_tip_3);
            return;
        }
        String content = bean.getName();
        if (!TextUtils.isEmpty(text)) {
            content += " " + text;
        }
        HttpUtil.videoReport(mVideoId, bean.getId(), content, mReportCallback);
    }

    private HttpCallback mReportCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                ToastUtil.show(R.string.video_report_tip_4);
                onBackPressed();
            } else {
                ToastUtil.show(msg);
            }
        }
    };

    @Override
    public void onKeyBoardHeightChanged(int visibleHeight, int keyboardHeight) {
        if (mRecyclerView != null) {
            mRecyclerView.setTranslationY(-keyboardHeight);
        }
        if (keyboardHeight > 0 && mAdapter != null) {
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
        }
    }

    @Override
    public boolean isSoftInputShowed() {
        return false;
    }


    private void release() {
        HttpUtil.cancel(HttpConst.GET_VIDEO_REPORT_LIST);
        HttpUtil.cancel(HttpConst.VIDEO_REPORT);
        if (mKeyBoardHeightUtil != null) {
            mKeyBoardHeightUtil.release();
        }
        mKeyBoardHeightUtil = null;
        if (mAdapter != null) {
            mAdapter.setActionListener(null);
        }
        mAdapter = null;
    }

    @Override
    public void onBackPressed() {
        release();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
    }
}
