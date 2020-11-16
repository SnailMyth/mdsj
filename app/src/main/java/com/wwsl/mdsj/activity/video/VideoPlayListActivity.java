package com.wwsl.mdsj.activity.video;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.recyclerview.widget.OrientationHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dueeeke.videoplayer.player.VideoView;
import com.frame.fire.util.LogUtils;
import com.lzy.okgo.model.Response;
import com.permissionx.guolindev.PermissionX;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.UserHomePageActivity;
import com.wwsl.mdsj.activity.common.ActivityManager;
import com.wwsl.mdsj.activity.common.WebViewActivity;
import com.wwsl.mdsj.activity.message.ChatRoomActivity;
import com.wwsl.mdsj.adapter.VideoAdapter;
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
import com.wwsl.mdsj.event.PersonHomePageChangeEvent;
import com.wwsl.mdsj.event.VideoCommentEvent;
import com.wwsl.mdsj.event.VideoFollowEvent;
import com.wwsl.mdsj.event.VideoLikeEvent;
import com.wwsl.mdsj.fragment.ShareDialog;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.http.JsonBean;
import com.wwsl.mdsj.im.ImChatFacePagerAdapter;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.interfaces.OnFaceClickListener;
import com.wwsl.mdsj.share.ShareHelper;
import com.wwsl.mdsj.utils.DownloadUtil;
import com.wwsl.mdsj.utils.FileUtil;
import com.wwsl.mdsj.utils.SnackBarUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.VideoStorage;
import com.wwsl.mdsj.utils.cache.PreloadManager;
import com.wwsl.mdsj.utils.tiktok.OnVideoLayoutClickListener;
import com.wwsl.mdsj.views.VideoCommentViewHolder;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;
import com.wwsl.mdsj.views.viewpagerlayoutmanager.OnViewPagerListener;
import com.wwsl.mdsj.views.viewpagerlayoutmanager.ViewPagerLayoutManager;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import cn.hutool.core.util.StrUtil;

/**
 * @author :
 * @date : 2020/6/17 16:06
 * @description : 视频全屏播放页
 */
public class VideoPlayListActivity extends BaseActivity implements View.OnClickListener, OnFaceClickListener,
        OnDialogCallBackListener, SwipeRefreshLayout.OnRefreshListener, SwipeRecyclerView.LoadMoreListener {

    public int initPos;

    /**
     * 当前播放位置
     */
    private SwipeRefreshLayout swipeRefreshLayout;

    private SwipeRecyclerView videoRecycler;
    private VideoAdapter videoAdapter;
    private ViewPagerLayoutManager layoutManager;
    private int mCurPos = 0;
    private List<VideoBean> mVideoList = new ArrayList<>();
    private int mPage = 1;
    private String mType = HttpConst.USER_VIDEO_TYPE_PRODUCT;
    private String uid;
    private int videoIndex = HttpConst.VIDEO_TYPE_PRODUCT << 10;

    private PreloadManager mPreloadManager;

    private VideoCommentViewHolder mVideoCommentViewHolder;
    private VideoInputDialogFragment mVideoInputDialogFragment;
    private View mFaceView;//表情面板
    private int mFaceHeight;//表情面板高度

    private VideoView curVideoView;
    private ShareDialog shareDialog;
    private DownloadUtil mDownloadUtil;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_video_play_list;
    }

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        mType = getIntent().getStringExtra("type");
        uid = getIntent().getStringExtra("uid");
        initPos = getIntent().getIntExtra("position", 0);

        if (HttpConst.USER_VIDEO_TYPE_PRODUCT.equals(mType)
                || HttpConst.USER_VIDEO_TYPE_TREND.equals(mType)
                || HttpConst.USER_VIDEO_TYPE_LIKE.equals(mType)) {
            videoIndex = HttpConst.VIDEO_TYPE_PRODUCT << 10;
        }

        findView();
        mPreloadManager = PreloadManager.getInstance(this);
        mDownloadUtil = new DownloadUtil();
        initView();
        initListener();
    }

    private void initListener() {

        layoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {
                if (videoRecycler == null || videoRecycler.getChildAt(0) == null) {
                    return;
                }
                curVideoView = videoRecycler.getChildAt(0).findViewById(R.id.videoView);
                if (curVideoView != null) {
                    curVideoView.start();
                }
            }

            @Override
            public void onPageRelease(boolean isNext, int position) {

            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                if (mCurPos != position) {
                    //如果当前position 和 上一次固定后的position 相同, 说明是同一个, 只不过滑动了一点点, 然后又释放了
                    curVideoView = (VideoView) videoAdapter.getViewByPosition(position, R.id.videoView);
                    if (curVideoView != null) {
                        LogUtils.e("startPlay: " + "position: " + position + "  url: " + mVideoList.get(position).getVideoUrl());
                        curVideoView.start();
                    }
                }

                mCurPos = position;

                if (mVideoList.size() > 5 && position == mVideoList.size() - 2) {
                    loadVideo(false);
                }

            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);

        videoRecycler.useDefaultLoadMore();

        videoRecycler.setLoadMoreListener(this);


        shareDialog.setListener((view, object) -> {
            if (object instanceof NetFriendBean) {
                NetFriendBean friend = (NetFriendBean) object;
                ChatRoomActivity.forward(VideoPlayListActivity.this, UserBean.builder().id(friend.getTouid()).avatar(friend.getAvatar()).username(friend.getUsername()).build(), mVideoList.get(mCurPos), true);
            } else if (object instanceof ShareBean) {
                ShareBean shareBean = (ShareBean) object;
                if (shareBean.getType() == Constants.SAVE_LOCAL) {
                    saveVideo();
                } else if (shareBean.getType() == Constants.DELETE_VIDEO) {
                    //删除视频
                    showLoadCancelable(true, "删除中...");
                    HttpUtil.videoDelete(mVideoList.get(mCurPos).getId(), new HttpCallback() {
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
                } else if (shareBean.getType() == Constants.VIDEO_REPORT) {
                    VideoReportActivity.forward(VideoPlayListActivity.this, mVideoList.get(mCurPos).getId());
                } else {
                    ShareHelper.shareVideo(VideoPlayListActivity.this, shareBean, mVideoList.get(mCurPos));
                }
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
                        VideoBean videoBean = videoAdapter.getData().get(mCurPos);
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

                                FileUtil.saveVideo(VideoPlayListActivity.this, file);

                                SnackBarUtil.ShortSnackbar(swipeRefreshLayout, path, SnackBarUtil.Info).show();
                            }

                            @Override
                            public void onProgress(int progress) {
                            }

                            @Override
                            public void onError(Throwable e) {
                                dismissLoad();
                                SnackBarUtil.ShortSnackbar(swipeRefreshLayout, "下载失败", SnackBarUtil.Alert).show();
                            }
                        });
                    }
                });
    }

    @SuppressLint("DefaultLocale")
    private void initView() {
        OnVideoLayoutClickListener onVideoLayoutClickListener = (type, bean) -> {
            switch (type) {
                case Constants.VIDEO_CLICK_HEAD:
                    if (HttpConst.USER_VIDEO_TYPE_HOMETOWN.equals(mType)) {
                        UserHomePageActivity.forward(VideoPlayListActivity.this, bean.getUid());
                    } else if (HttpConst.USER_VIDEO_TYPE_LIKE.equals(mType)) {
                        //从UserHomePageActivity 跳转
                        UserHomePageActivity activity = (UserHomePageActivity) ActivityManager.getInstance().getActivity(UserHomePageActivity.class.getSimpleName());
                        if (null != activity) {
                            activity.updateUserInfo(bean.getUid());
                            release();
                        } else {
                            EventBus.getDefault().post(PersonHomePageChangeEvent.builder().uid(bean.getUid()).build());
                            release();
                        }
                    } else {
                        release();
                    }
                    break;
                case Constants.VIDEO_CLICK_LIKE:
                    HttpUtil.setVideoLike(String.format("%d%s", Constants.FOLLOW_FROM_VIDEO_LIKE, bean.getId()), bean.getId(), new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0) {
                                JSONObject obj = JSON.parseObject(info[0]);
                                String likeNum = obj.getString("likes");
                                int like = obj.getIntValue("islike");
                                videoAdapter.onlike(bean.getId(), likeNum, like);
                                //更新首页点赞
                                EventBus.getDefault().post(new VideoLikeEvent(bean.getId(), like, likeNum));
                            }
                        }
                    });
                    break;
                case Constants.VIDEO_CLICK_SHARE:
                    shareDialog.updateAction(mVideoList.get(mCurPos).getUid());
                    shareDialog.show(getSupportFragmentManager(), "VideoPlayListActivity");
                    break;
                case Constants.VIDEO_CLICK_COMMENT:
                    openCommentWindow(bean);
                    break;
                case Constants.VIDEO_CLICK_FOLLOW:
                    HttpUtil.setAttention(Constants.FOLLOW_FROM_VIDEO_PLAY, bean.getUid(), new CommonCallback<Integer>() {
                        @Override
                        public void callback(Integer bean) {
                            //HttpUtil 里面已经发送事件
                        }
                    });
                    break;
                case Constants.VIDEO_CLICK_TITLE:
                    break;
                case Constants.VIDEO_CLICK_MUSIC:
                    if (null != bean.getMusicInfo()) {
                        TakeVideoWithSameMusicActivity.forward(VideoPlayListActivity.this, bean.getMusicInfo());
                    } else {
                        ToastUtil.show("当前音乐不可编辑");
                    }
                    break;
                case Constants.VIDEO_CLICK_AD:
                    if (!StrUtil.isEmpty(bean.getAdUrl())) {
                        WebViewActivity.forward2(VideoPlayListActivity.this, bean.getAdUrl());
                    }
                    break;
                case Constants.VIDEO_CLICK_GOODS:
                    if (!StrUtil.isEmpty(bean.getGoodsId()) && bean.getGoods() != null && !StrUtil.isEmpty(bean.getGoods().getUrl())) {
                        WebViewActivity.forward(VideoPlayListActivity.this, bean.getGoods().getUrl());
                    }
                    break;
                case Constants.VIDEO_CLICK_DS:
                    openGiftDialog(bean.getId(), bean.getUid());
                    break;
            }
        };
        layoutManager = new ViewPagerLayoutManager(this, OrientationHelper.VERTICAL);
        switch (mType) {
            case HttpConst.USER_VIDEO_TYPE_HOMETOWN:
                videoAdapter = new VideoAdapter(mVideoList, this, HttpConst.VIDEO_TYPE_HOMETOWN);
                break;
            case HttpConst.USER_VIDEO_TYPE_PRODUCT:
            case HttpConst.USER_VIDEO_TYPE_TREND:
            case HttpConst.USER_VIDEO_TYPE_LIKE:
            case HttpConst.USER_VIDEO_TYPE_SAME_MUSIC:
                videoAdapter = new VideoAdapter(mVideoList, this, -1);
                break;
        }

        videoAdapter.setLayoutClickListener(onVideoLayoutClickListener);
        videoRecycler.setLayoutManager(layoutManager);
        videoRecycler.setAdapter(videoAdapter);
        shareDialog = new ShareDialog();
    }

    private void release() {
        HttpUtil.cancel(HttpConst.GET_VIDEO_LIST);
        HttpUtil.cancel(HttpConst.GET_HOME_VIDEO);
        finish();
    }

    private void findView() {
        swipeRefreshLayout = findViewById(R.id.videoFreshLayout);
        videoRecycler = findViewById(R.id.videoRecycler);
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

        if (mVideoList.size() == 0) {
            loadData();
        }

        //返回时，推荐页面可见，则继续播放视频
        if (curVideoView != null) {
            if (curVideoView.isPlaying()) {
                curVideoView.resume();
            } else {
                curVideoView.start();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.e(TAG, "onPause: " + mType);
        if (curVideoView != null) {
            curVideoView.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (curVideoView != null) {
            curVideoView.release();
        }
//        mPreloadManager.removeAllPreloadTask();
        //清除缓存，实际使用可以不需要清除，这里为了方便测试
//        ProxyVideoCacheManager.clearAllCache(this);
        EventBus.getDefault().unregister(this);
    }

    private final static String TAG = "VideoPlayFragment";

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
                    videoAdapter.onComment(value.get(0), value.get(1));
                    //刷新评论列表
                    if (mVideoCommentViewHolder != null) {
                        mVideoCommentViewHolder.updateData();
                    }
                    //刷新上一界面数据

                    //刷新首页评论数量
                    EventBus.getDefault().post(new VideoCommentEvent(value.get(0), value.get(1)));
                    break;
            }
        }
    }

    @Override
    public void onRefresh() {
        mPage = 1;
        initPos = 0;
        loadData();
    }

    @Override
    public void onLoadMore() {
        loadVideo(true);
    }

    public void loadData() {
        if (StrUtil.isEmpty(uid)) return;
        List<VideoBean> videoList = VideoStorage.getInstance().getVideoList(uid, mType);
        if (videoList != null && videoList.size() > 0) {
            mPage = videoList.size() / HttpConst.ITEM_COUNT;
            mPage += videoList.size() % HttpConst.ITEM_COUNT == 0 ? 0 : 1;
            videoAdapter.getData().clear();
            videoAdapter.addData(videoList);
            videoRecycler.scrollToPosition(initPos);
            swipeRefreshLayout.setRefreshing(false);
            mCurPos = initPos;
        } else {
            loadVideo(true);
        }
    }

    private void loadVideo(boolean showLoadTxt) {
        if (HttpConst.USER_VIDEO_TYPE_HOMETOWN.equals(mType)) {
            HttpUtil.getHometownVideoList(HttpConst.VIDEO_TYPE_HOMETOWN, mPage, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info != null && info.length > 0) {
                        List<VideoBean> videoBeans = JSON.parseArray(Arrays.toString(info), VideoBean.class);
                        int startIndex = mVideoList.size();
                        if (startIndex > 0) {
                            mVideoList.clear();
                        }

                        int videoStartIndex = HttpConst.VIDEO_TYPE_HOMETOWN << 10;
                        for (int i = 0, j = startIndex; i < videoBeans.size(); i++) {
                            mPreloadManager.addPreloadTask(videoBeans.get(i).getVideoUrl(), (videoStartIndex + j++));
                        }

                        if (videoBeans.size() == HttpConst.ITEM_COUNT) {
                            mPage++;
                        }

                        videoAdapter.addData(videoBeans);
                        videoRecycler.loadMoreFinish(false, true);
                    } else {
                        videoRecycler.loadMoreFinish(true, false);
                        ToastUtil.show(msg);
                    }

                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        } else if (HttpConst.USER_VIDEO_TYPE_SAME_MUSIC.equals(mType)) {

        } else {
            HttpUtil.getHomeVideo(mType, uid, mPage, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info != null && info.length > 0) {
                        List<VideoBean> newBeans = JSON.parseArray(Arrays.toString(info), VideoBean.class);

                        if (newBeans.size() == HttpConst.ITEM_COUNT) {
                            mPage++;
                        }

                        List<VideoBean> addBeans = new ArrayList<>();
                        int oldLastIndex = mVideoList.size();
                        if (oldLastIndex != 0) {
                            String lastId = mVideoList.get(oldLastIndex - 1).getId();
                            int startIndex = -1;
                            //匹配最后一个id相同的视频
                            for (int i = 0; i < newBeans.size(); i++) {
                                if (lastId.equals(newBeans.get(i).getId())) {
                                    startIndex = i;
                                    break;
                                }
                            }
                            //当前页至少有2个数据
                            if (startIndex >= 0 && newBeans.size() > 1) {
                                for (int i = startIndex + 1; i < newBeans.size(); i++) {
                                    addBeans.add(newBeans.get(i));
                                }
                            } else {
                                addBeans.addAll(newBeans);
                            }

                        } else {
                            addBeans.addAll(newBeans);
                        }

                        for (int i = 0, j = mVideoList.size(); i < addBeans.size(); i++) {
                            mPreloadManager.addPreloadTask(addBeans.get(i).getVideoUrl(), (videoIndex + j++));
                        }

                        videoAdapter.addData(addBeans);

                        videoRecycler.loadMoreFinish(false, true);
                    } else {
                        videoRecycler.loadMoreFinish(true, false);
                        if (showLoadTxt) {
                            ToastUtil.show(msg);
                        }
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onError(Response<JsonBean> response) {
                    swipeRefreshLayout.setRefreshing(false);
                    videoRecycler.loadMoreFinish(false, true);
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(VideoFollowEvent e) {
        if (videoAdapter != null) {
            videoAdapter.onFollowChanged(e.getMToUid(), e.getMIsAttention());
        }
    }

    public static void forward(Context conext, String uid, String type, int position) {
        Intent intent = new Intent(conext, VideoPlayListActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("uid", uid);
        intent.putExtra("position", position);
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
