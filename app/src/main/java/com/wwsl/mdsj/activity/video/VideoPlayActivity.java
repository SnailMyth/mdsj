package com.wwsl.mdsj.activity.video;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dueeeke.videoplayer.player.VideoView;
import com.permissionx.guolindev.PermissionX;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.UserHomePageActivity;
import com.wwsl.mdsj.activity.common.WebViewActivity;
import com.wwsl.mdsj.activity.message.ChatRoomActivity;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.bean.KeyValueBean;
import com.wwsl.mdsj.bean.ShareBean;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.bean.VideoBean;
import com.wwsl.mdsj.bean.VideoCommentBean;
import com.wwsl.mdsj.bean.net.NetFriendBean;
import com.wwsl.mdsj.dialog.VideoGiftDialogFragment;
import com.wwsl.mdsj.dialog.VideoInputDialogFragment;
import com.wwsl.mdsj.event.CommentDialogEvent;
import com.wwsl.mdsj.event.DialogShowEvent;
import com.wwsl.mdsj.event.VideoFollowEvent;
import com.wwsl.mdsj.fragment.ShareDialog;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.im.ImChatFacePagerAdapter;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.interfaces.OnFaceClickListener;
import com.wwsl.mdsj.share.ShareHelper;
import com.wwsl.mdsj.utils.DownloadUtil;
import com.wwsl.mdsj.utils.FileUtil;
import com.wwsl.mdsj.utils.SnackBarUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.tiktok.OnVideoLayoutClickListener;
import com.wwsl.mdsj.utils.tiktok.TikTokController;
import com.wwsl.mdsj.utils.tiktok.TikTokRenderViewFactory;
import com.wwsl.mdsj.views.TikTokView;
import com.wwsl.mdsj.views.VideoCommentViewHolder;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import cn.hutool.core.util.StrUtil;

/**
 * @author :
 * @date : 2020/6/17 16:06
 * @description : 单个视频全屏播放页
 */
public class VideoPlayActivity extends BaseActivity implements View.OnClickListener, OnFaceClickListener,
        OnDialogCallBackListener {

    private VideoCommentViewHolder mVideoCommentViewHolder;
    private VideoInputDialogFragment mVideoInputDialogFragment;
    private View mFaceView;//表情面板
    private int mFaceHeight;//表情面板高度
    private DownloadUtil mDownloadUtil;
    private ShareDialog shareDialog;
    private FrameLayout rootView;

    private VideoView mVideoView;
    private TikTokController mController;

    private TikTokView tikTokView;
    private VideoBean videoBean;//视频信息

    @Override
    protected int setLayoutId() {
        return R.layout.activity_video_play;
    }

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        videoBean = getIntent().getParcelableExtra("video");
        mDownloadUtil = new DownloadUtil();
        findView();
        initView();
        initListener();
    }


    private void initListener() {
        shareDialog.setListener((view, object) -> {
            if (object instanceof NetFriendBean) {
                NetFriendBean friend = (NetFriendBean) object;
                ChatRoomActivity.forward(this, UserBean.builder().id(friend.getTouid()).avatar(friend.getAvatar()).username(friend.getUsername()).build(), videoBean, true);
            } else if (object instanceof ShareBean) {
                ShareBean shareBean = (ShareBean) object;
                if (shareBean.getType() == Constants.SAVE_LOCAL) {
                    saveVideo();
                } else if (shareBean.getType() == Constants.DELETE_VIDEO) {
                    //删除视频
                    deleteVideo();
                } else if (shareBean.getType() == Constants.VIDEO_REPORT) {
                    VideoReportActivity.forward(mActivity, videoBean.getId());
                } else {
                    ShareHelper.shareVideo(this, shareBean, videoBean);
                }
            }
        });
    }

    private void deleteVideo() {
        showLoadCancelable(true, "删除中...");
        HttpUtil.videoDelete(videoBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                dismissLoad();
                if (code == 0) {
                    ToastUtil.show("删除成功");
                    finish();
                }
            }

            @Override
            public void onError() {
                dismissLoad();
            }
        });
    }

    private void saveVideo() {
        PermissionX.init(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE)
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        showLoadCancelable(false, "保存视频中...");
                        mDownloadUtil.download(videoBean.getId(), AppConfig.VIDEO_PATH, Calendar.getInstance().getTimeInMillis() + ".mp4", videoBean.getVideoUrl(), new DownloadUtil.Callback() {
                            @Override
                            public void onSuccess(File file) {
                                dismissLoad();
                                String path = "保存成功";
                                if (file != null) {
                                    String temp = file.getPath();
                                    path = String.format("保存成功,位置:%s", temp);
                                }

                                FileUtil.saveVideo(mActivity, file);

                                SnackBarUtil.ShortSnackbar(rootView, path, SnackBarUtil.Info).show();
                            }

                            @Override
                            public void onProgress(int progress) {
                            }

                            @Override
                            public void onError(Throwable e) {
                                dismissLoad();
                                SnackBarUtil.ShortSnackbar(rootView, "下载失败", SnackBarUtil.Alert).show();
                            }
                        });
                    }
                });
    }

    @SuppressLint("DefaultLocale")
    private void initView() {
        OnVideoLayoutClickListener onVideoLayoutClickListener = new OnVideoLayoutClickListener() {
            @Override
            public void onClickEvent(int type, VideoBean bean) {
                switch (type) {
                    case Constants.VIDEO_CLICK_HEAD:
                        UserHomePageActivity.forward(mActivity, bean.getUid());
                        release();
                        break;
                    case Constants.VIDEO_CLICK_LIKE:
                        HttpUtil.setVideoLike(String.format("%d%s", Constants.FOLLOW_FROM_VIDEO_LIKE, bean.getId()), bean.getId(), new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                if (code == 0 && info.length > 0) {
                                    JSONObject obj = JSON.parseObject(info[0]);
                                    String likeNum = obj.getString("likes");
                                    int like = obj.getIntValue("islike");
                                    tikTokView.updateLike(likeNum, like);
                                }
                            }
                        });
                        break;
                    case Constants.VIDEO_CLICK_SHARE:
                        shareDialog.show(getSupportFragmentManager(), "VideoPlayActivity");
                        break;
                    case Constants.VIDEO_CLICK_COMMENT:
                        openCommentWindow(bean);
                        break;
                    case Constants.VIDEO_CLICK_FOLLOW:
                        HttpUtil.setAttention(Constants.FOLLOW_FROM_VIDEO_PLAY, bean.getUid(), new CommonCallback<Integer>() {
                            @Override
                            public void callback(Integer bean) {
//                        ToastUtil.show(bean == 1 ? "关注成功" : "取消关注成功");
                            }
                        });
                        break;
                    case Constants.VIDEO_CLICK_TITLE:
                        break;
                    case Constants.VIDEO_CLICK_MUSIC:
                        if (null != bean.getMusicInfo()) {
                            TakeVideoWithSameMusicActivity.forward(mActivity, bean.getMusicInfo());
                        } else {
                            ToastUtil.show("当前音乐不可编辑");
                        }
                        break;
                    case Constants.VIDEO_CLICK_AD:
                        if (!StrUtil.isEmpty(bean.getAdUrl())) {
                            WebViewActivity.forward2(mActivity, bean.getAdUrl());
                        }
                        break;
                    case Constants.VIDEO_CLICK_GOODS:
                        if (!StrUtil.isEmpty(bean.getGoodsId()) && bean.getGoods() != null && !StrUtil.isEmpty(bean.getGoods().getUrl())) {
                            WebViewActivity.forward(mActivity, bean.getGoods().getUrl());
                        }
                        break;
                    case Constants.VIDEO_CLICK_DS:
                        openGiftDialog(bean.getId(), bean.getUid());
                        break;
                }
            }
        };
        tikTokView.setVideoData(videoBean);
        mController = new TikTokController(this);
        tikTokView.setListener(onVideoLayoutClickListener);
        mVideoView.setRenderViewFactory(TikTokRenderViewFactory.create());
        mVideoView.setLooping(true);
        mVideoView.setVideoController(mController);
        mVideoView.setUrl(videoBean.getVideoUrl());
        mController.addControlComponent(tikTokView, true);
        shareDialog = new ShareDialog(videoBean.getUid());
    }

    private void release() {
        HttpUtil.cancel(HttpConst.GET_HOME_VIDEO);
        finish();
    }

    private void findView() {
        tikTokView = findViewById(R.id.tiktok_View);
        mVideoView = findViewById(R.id.videoView);
        rootView = findViewById(R.id.rootView);
    }


    @Override
    public void onBackPressed() {
        if (mVideoCommentViewHolder != null) {
            if (mVideoCommentViewHolder.isShowing()) {
                mVideoCommentViewHolder.hideBottom();
                return;
            }
        }
        super.onBackPressed();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mVideoView != null) {
            if (mVideoView.isPlaying()) {
                mVideoView.resume();
            } else {
                mVideoView.start();
            }
        }
    }

    @Override
    public void onPause() {
        mVideoView.pause();
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mVideoView.release();
    }

    /**
     * 显示评论
     */
    public void openCommentWindow(VideoBean videoBean) {
        if (mVideoCommentViewHolder == null) {
            mVideoCommentViewHolder = new VideoCommentViewHolder(this, this.findViewById(R.id.rootView));
            mVideoCommentViewHolder.addToParent();
        }

        mVideoCommentViewHolder.setCallBackListener(this);
        mVideoCommentViewHolder.setVideoBean(videoBean);
        mVideoCommentViewHolder.showBottom();
    }

    /**
     * 隐藏评论
     */
    public void hideCommentWindow() {
        if (mVideoCommentViewHolder != null) {
            mVideoCommentViewHolder.hideBottom();
        }
        mVideoInputDialogFragment = null;
    }

    public View getFaceView() {
        if (mFaceView == null) {
            mFaceView = initFaceView();
        }
        return mFaceView;
    }


    /**
     * 打开评论输入框
     */
    public void openCommentInputWindow(boolean openFace, VideoBean videoBean, VideoCommentBean bean) {

        if (AppConfig.getInstance().getUserBean().getCanComment() <= 0) {
            ToastUtil.show("没有评论权限");
            return;
        }

        if (mFaceView == null) {
            mFaceView = initFaceView();
        }

        VideoInputDialogFragment fragment = new VideoInputDialogFragment();
        fragment.setVideoBean(videoBean);
        fragment.setFaceView(getFaceView(), this);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.VIDEO_FACE_OPEN, openFace);
        bundle.putInt(Constants.VIDEO_FACE_HEIGHT, mFaceHeight);
        bundle.putParcelable(Constants.VIDEO_COMMENT_BEAN, bean);
        fragment.setArguments(bundle);
        mVideoInputDialogFragment = fragment;
        fragment.show(this.getSupportFragmentManager(), "VideoInputDialogFragment");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send) {
            if (mVideoInputDialogFragment != null) {
                mVideoInputDialogFragment.sendComment();
            }
        }
    }

    @Override
    public void onFaceClick(String str, int faceImageRes) {
        if (mVideoInputDialogFragment != null) {
            mVideoInputDialogFragment.onFaceClick(str, faceImageRes);
        }
    }

    @Override
    public void onFaceDeleteClick() {
        if (mVideoInputDialogFragment != null) {
            mVideoInputDialogFragment.onFaceDeleteClick();
        }
    }

    /**
     * 初始化表情控件
     */
    private View initFaceView() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.view_chat_face, null);
        v.measure(0, 0);
        mFaceHeight = v.getMeasuredHeight();
        v.findViewById(R.id.btn_send).setOnClickListener(this);
        final RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radio_group);
        ViewPager viewPager = (ViewPager) v.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(10);

        ImChatFacePagerAdapter adapter = new ImChatFacePagerAdapter(this, this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((RadioButton) radioGroup.getChildAt(position)).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (int i = 0, pageCount = adapter.getCount(); i < pageCount; i++) {
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.view_chat_indicator, radioGroup, false);
            radioButton.setId(i + 10000);
            if (i == 0) {
                radioButton.setChecked(true);
            }
            radioGroup.addView(radioButton);
        }
        return v;
    }


    @Override
    public void onDialogViewClick(View view, Object object) {
        if (object instanceof CommentDialogEvent) {
            CommentDialogEvent event = (CommentDialogEvent) object;
            openCommentInputWindow(event.isOpenFace(), event.getVideoBean(), event.getCommentBean());
        } else if (object instanceof DialogShowEvent) {
            DialogShowEvent event = (DialogShowEvent) object;
            if (event.getTag().equals(Constants.VIDEO_COMMENT_DIALOG)) {
                hideCommentWindow();
            }
        } else if (object instanceof KeyValueBean) {
            KeyValueBean event = (KeyValueBean) object;
            switch (event.getKey()) {
                case Constants.KEY_VIDEO_COMMENT_NUM:
                    //刷新短视频评论数字
                    List<String> value = (List<String>) event.getValue();
                    tikTokView.updateComment(value.get(1));
                    //刷新评论列表
                    if (mVideoCommentViewHolder != null) {
                        mVideoCommentViewHolder.updateData();
                    }
                    break;
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(VideoFollowEvent e) {
        if (tikTokView != null) {
            tikTokView.updateFollow(e.getMIsAttention());
        }
    }


    public static void forward(Context conext, VideoBean videoBean) {
        Intent intent = new Intent(conext, VideoPlayActivity.class);
        intent.putExtra("video", videoBean);
        conext.startActivity(intent);
    }

    public void backClick(View view) {
        finish();
    }

    private void openGiftDialog(String id, String uid) {

        VideoGiftDialogFragment fragment = new VideoGiftDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.VIDEO_DS_ID, id);
        bundle.putString(Constants.VIDEO_DS_UID, uid);
        fragment.setArguments(bundle);
        fragment.show((this).getSupportFragmentManager(), "VideoGiftDialogFragment");
    }
}
