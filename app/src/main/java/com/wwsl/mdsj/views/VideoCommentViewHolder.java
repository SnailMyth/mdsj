package com.wwsl.mdsj.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.frame.fire.util.LogUtils;
import com.lxj.xpopup.util.KeyboardUtils;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.RefreshAdapter;
import com.wwsl.mdsj.adapter.VideoCommentAdapter;
import com.wwsl.mdsj.bean.VideoBean;
import com.wwsl.mdsj.bean.VideoCommentBean;
import com.wwsl.mdsj.custom.MyLinearLayout3;
import com.wwsl.mdsj.custom.RefreshView;
import com.wwsl.mdsj.event.CommentDialogEvent;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.OnItemClickListener;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.L;
import com.wwsl.mdsj.utils.WordUtil;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;

import java.util.ArrayList;
import java.util.List;

public class VideoCommentViewHolder extends AbsViewHolder implements View.OnClickListener, OnItemClickListener<VideoCommentBean> {

    private View mRoot;
    private MyLinearLayout3 mBottom;
    private RefreshView mRefreshView;
    private TextView mCommentNum;
    private VideoCommentAdapter mVideoCommentAdapter;
    private VideoBean mVideoBean;
    private String mCommentString;
    private ObjectAnimator mShowAnimator;
    private ObjectAnimator mHideAnimator;
    private boolean mAnimating;
    private OnDialogCallBackListener callBackListener;

    public VideoCommentViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_video_comment;
    }

    @Override
    public void init() {
        mRoot = findViewById(R.id.root);
        mBottom = (MyLinearLayout3) findViewById(R.id.bottom);
        int height = mBottom.getHeight2();
        mBottom.setTranslationY(height);
        mShowAnimator = ObjectAnimator.ofFloat(mBottom, "translationY", 0);
        mHideAnimator = ObjectAnimator.ofFloat(mBottom, "translationY", height);
        mShowAnimator.setDuration(200);
        mHideAnimator.setDuration(200);
        TimeInterpolator interpolator = new AccelerateDecelerateInterpolator();
        mShowAnimator.setInterpolator(interpolator);
        mHideAnimator.setInterpolator(interpolator);
        AnimatorListenerAdapter animatorListener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimating = false;
                if (animation == mHideAnimator) {
                    if (mRoot != null && mRoot.getVisibility() == View.VISIBLE) {
                        mRoot.setVisibility(View.INVISIBLE);
                        isShowing = false;
                    }
                } else if (animation == mShowAnimator) {
                    if (mRefreshView != null) {
                        isShowing = true;
                        mRefreshView.initData();
                    }
                }
            }
        };
        mShowAnimator.addListener(animatorListener);
        mHideAnimator.addListener(animatorListener);

        findViewById(R.id.root).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.input).setOnClickListener(this);
        findViewById(R.id.btn_face).setOnClickListener(this);
        mCommentString = WordUtil.getString(R.string.video_comment);
        mCommentNum = (TextView) findViewById(R.id.comment_num);
        mRefreshView = (RefreshView) findViewById(R.id.refreshView);
        mRefreshView.setNoDataLayoutId(R.layout.view_no_data_comment);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (Exception e) {
                    L.e("onLayoutChildren------>" + e.getMessage());
                }
            }
        });

        mRefreshView.setDataHelper(new RefreshView.DataHelper<VideoCommentBean>() {
            @Override
            public RefreshAdapter<VideoCommentBean> getAdapter() {
                if (mVideoCommentAdapter == null) {
                    mVideoCommentAdapter = new VideoCommentAdapter(mContext);
                    mVideoCommentAdapter.setLongClickListener((bean, position) -> {
                        KeyboardUtils.hideSoftInput(mCommentNum);
                        DialogUtil.delComment(mContext, (VideoCommentBean) bean, mVideoBean, (view, object) -> {
                            //刷新列表
                            mVideoCommentAdapter.getList().remove(bean);
                            mVideoCommentAdapter.notifyDataSetChanged();
                            //刷新title数据显示
                            if (mCommentNum != null) {
                                int size = mVideoCommentAdapter.getList().size();
                                mCommentNum.setText(String.format("%s %s", size, mCommentString));
                            }
                        });
                    });
                    mVideoCommentAdapter.setOnItemClickListener(VideoCommentViewHolder.this);
                }
                return mVideoCommentAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                if (mVideoBean != null) {
                    HttpUtil.getVideoCommentList(mVideoBean.getId(), p, callback);
                }
            }

            @Override
            public List<VideoCommentBean> processData(String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                String commentNum = obj.getString("comments");
                if (mVideoBean != null && callBackListener != null) {
                    List<String> value = new ArrayList<>();
                    value.add(mVideoBean.getId());
                    value.add(commentNum);
//                    callBackListener.onDialogViewClick(null, KeyValueBean.builder().key(Constants.KEY_VIDEO_COMMENT_NUM).value(value).build());
                }

                if (mCommentNum != null) {
                    mCommentNum.setText(String.format("%s %s", commentNum, mCommentString));
                }
                String objString = obj.getString("commentlist");
//                LogUtils.e(objString.substring(2355, 2370));
                List<VideoCommentBean> list;
                try {
                    list = JSON.parseArray(objString, VideoCommentBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    mCommentNum.setText(String.format("%s %s", 0, mCommentString));
                    return new ArrayList<>();
                }
                List<VideoCommentBean> newList = new ArrayList<>();
                for (VideoCommentBean bean : list) {
                    newList.add(bean);
                    VideoCommentBean firstChild = bean.getFirstChild();
                    if (firstChild != null) {
                        if (bean.getReplyNum() > 1) {
                            firstChild.setChildType(VideoCommentBean.CHILD_FIRST);
                            firstChild.setExpand(false);
                        } else {
                            firstChild.setChildType(VideoCommentBean.CHILD_NORMAL);
                        }
                        firstChild.setParentComment(bean);
                        newList.add(firstChild);
                    }
                }
                return newList;
            }

            @Override
            public void onRefresh(List<VideoCommentBean> list) {

            }

            @Override
            public void onNoData(boolean noData) {

            }

            @Override
            public void onLoadDataCompleted(int dataCount) {

            }
        });
    }


    public void setCallBackListener(OnDialogCallBackListener callBackListener) {
        this.callBackListener = callBackListener;
    }

    public void setVideoBean(VideoBean videoBean) {
        if (mVideoBean != null) {
            String curVideoId = mVideoBean.getId();
            if (!TextUtils.isEmpty(curVideoId) && !curVideoId.equals(videoBean.getId())) {
                if (mVideoCommentAdapter != null) {
                    mVideoCommentAdapter.clearData();
                }
                if (mRefreshView != null) {
                    mRefreshView.showLoading();
                }
            }
        }
        mVideoBean = videoBean;
    }

    public void showBottom() {
        if (mAnimating) {
            return;
        }
        if (mRoot != null && mRoot.getVisibility() != View.VISIBLE) {
            mRoot.setVisibility(View.VISIBLE);
        }
        if (mShowAnimator != null) {
            mShowAnimator.start();
        }
    }

    public void hideBottom() {
        if (mAnimating) {
            return;
        }
        if (mHideAnimator != null) {
            mHideAnimator.start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.root:
            case R.id.btn_close:
                hideBottom();
                break;
            case R.id.input:
                callBackListener.onDialogViewClick(null, CommentDialogEvent.builder().openFace(false).videoBean(mVideoBean).commentBean(null).build());
                break;
            case R.id.btn_face:
                callBackListener.onDialogViewClick(null, CommentDialogEvent.builder().openFace(true).videoBean(mVideoBean).commentBean(null).build());
                break;
        }
    }


    public void release() {
        if (mShowAnimator != null) {
            mShowAnimator.cancel();
        }
        mShowAnimator = null;
        if (mHideAnimator != null) {
            mHideAnimator.cancel();
        }
        mHideAnimator = null;
        HttpUtil.cancel(HttpConst.GET_VIDEO_COMMENT_LIST);
        HttpUtil.cancel(HttpConst.SET_COMMENT_LIKE);
        HttpUtil.cancel(HttpConst.GET_COMMENT_REPLY);
        HttpUtil.cancel(HttpConst.VIDEO_DELCOMMENTS);
    }

    @Override
    public void onItemClick(VideoCommentBean bean, int position) {
        callBackListener.onDialogViewClick(null, CommentDialogEvent.builder().openFace(false).videoBean(mVideoBean).commentBean(bean).build());
    }

    private boolean isShowing;

    public boolean isShowing() {
        return isShowing;
    }

    public void updateData() {
        mRefreshView.initData();
    }
}