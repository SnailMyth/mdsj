package com.wwsl.mdsj.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.dueeeke.videoplayer.controller.ControlWrapper;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.player.VideoView;
import com.frame.fire.util.LogUtils;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.VideoBean;
import com.wwsl.mdsj.custom.DrawableTextView;
import com.wwsl.mdsj.custom.VideoLoadingBar;
import com.wwsl.mdsj.event.VideoPlayEvent;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.tiktok.AutoLinkHerfManager;
import com.wwsl.mdsj.utils.tiktok.OnVideoLayoutClickListener;
import com.wwsl.mdsj.views.autolinktextview.AutoLinkTextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hutool.core.util.StrUtil;

import static android.view.animation.Animation.INFINITE;

public class TikTokView extends RelativeLayout implements IControlComponent, View.OnClickListener {


    @BindView(R.id.tv_content)
    AutoLinkTextView autoLinkTextView;

    @BindView(R.id.txGood)
    DrawableTextView txGood;

    @BindView(R.id.videoLoadingBar)
    VideoLoadingBar videoLoadingBar;

    @BindView(R.id.txLocation)
    DrawableTextView txLocation;

    @BindView(R.id.iv_head)
    CircleImageView ivHead;
    @BindView(R.id.lottie_anim)
    LottieAnimationView animationView;
    @BindView(R.id.rl_like)
    ConstraintLayout rlLike;
    @BindView(R.id.iv_like)
    ImageView ivLike;
    @BindView(R.id.iv_comment)
    ImageView ivComment;
    @BindView(R.id.iv_share)
    ImageView ivShare;

    @BindView(R.id.ivZn)
    ImageView ivZn;

    @BindView(R.id.iv_record)
    ImageView ivRecord;
    @BindView(R.id.rl_record)
    ConstraintLayout rlRecord;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;

    @BindView(R.id.adBtn)
    TextView adBtn;
    @BindView(R.id.adTag)
    TextView adTag;

    @BindView(R.id.iv_head_anim)
    CircleImageView ivHeadAnim;

    @BindView(R.id.tv_likecount)
    TextView tvLikecount;
    @BindView(R.id.tv_commentcount)
    TextView tvCommentcount;
    @BindView(R.id.tv_sharecount)
    TextView tvSharecount;
    @BindView(R.id.likeview)
    LikeView likeView;
    @BindView(R.id.iv_focus)
    ImageView ivFocus;
    @BindView(R.id.ivDs)
    ImageView ivDs;
    @BindView(R.id.tvMusicTitle)
    MarqueeTextView musicName;

    @BindView(R.id.adBtnLayout)
    ConstraintLayout adBtnLayout;
    private OnVideoLayoutClickListener listener;

    private VideoBean videoData;

    private ImageView thumb;
    private ImageView mPlayBtn;

    private ControlWrapper mControlWrapper;
    private int mScaledTouchSlop;
    private int mStartX, mStartY;
    private boolean isSendEndTik = false;
    private boolean isSendStartTik = false;

    public TikTokView(@NonNull Context context) {
        super(context);
    }

    public TikTokView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.item_video, this, true);
        thumb = findViewById(R.id.iv_cover);
        mPlayBtn = findViewById(R.id.iv_play);
        mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        ButterKnife.bind(this, rootView);
        ivHead.setOnClickListener(this);
        ivComment.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        rlLike.setOnClickListener(this);
        ivFocus.setOnClickListener(this);
        likeView.setOnClickListener(this);
        rlRecord.setOnClickListener(this);
        txGood.setOnClickListener(this);
        adBtn.setOnClickListener(this);
        ivDs.setOnClickListener(this);

        initFocusAnim();

        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                LogUtils.e(TAG, "onAnimationStart: ");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animationView.setVisibility(INVISIBLE);
                LogUtils.e(TAG, "onAnimationEnd: ");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animationView.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        likeView.setOnLikeListener(() -> {

            if (AppConfig.getInstance().getUid().equals(videoData.getUid())) {
                ToastUtil.show("不能给自己的视频点赞");
                return;
            }

            if (videoData.getLike() == 0) {
                listener.onClickEvent(Constants.VIDEO_CLICK_LIKE, videoData);
            }
            like(false);
        });

        likeView.setOnPlayPauseListener(this::togglePlay);

    }

    public TikTokView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    /**
     * 解决点击和VerticalViewPager滑动冲突问题
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartX = (int) event.getX();
                mStartY = (int) event.getY();
                return true;
            case MotionEvent.ACTION_UP:
                int endX = (int) event.getX();
                int endY = (int) event.getY();
                if (Math.abs(endX - mStartX) < mScaledTouchSlop
                        && Math.abs(endY - mStartY) < mScaledTouchSlop) {
                    performClick();
                }
                break;
        }
        return false;
    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {

    }

    private final static String TAG = "tiktok";

    @Override
    public void onPlayStateChanged(int playState) {
        LogUtils.e(playState + "");
        switch (playState) {
            case VideoView.STATE_IDLE:
                thumb.setVisibility(VISIBLE);
                videoLoadingBar.setProgress(0);
                break;
            case VideoView.STATE_PLAYING:
                setRotateAnim();
                LogUtils.e(TAG, "STATE_PLAYING: " + videoData.getId() + videoData.getTitle());
                mControlWrapper.startProgress();
                videoLoadingBar.setLoading(false);
                EventBus.getDefault().post(VideoPlayEvent.builder().state(VideoView.STATE_PLAYING).videoId(videoData.getId()).build());
                thumb.setVisibility(GONE);
                mPlayBtn.setVisibility(GONE);
                break;
            case VideoView.STATE_PAUSED:
                thumb.setVisibility(GONE);
                mPlayBtn.setVisibility(VISIBLE);
                LogUtils.e(TAG, "STATE_PAUSED: " + videoData.getId() + videoData.getTitle());
                break;
            case VideoView.STATE_PREPARED:
                videoLoadingBar.setLoading(false);
                if (!isSendStartTik) {
                    isSendStartTik = true;
                    HttpUtil.videoWatchStart(videoData.getUid(), videoData.getId());
                }

                if (!isSendEndTik) {
                    if (videoData != null) {
                        isSendEndTik = true;
                        HttpUtil.videoWatchEnd(videoData.getUid(), videoData.getId());
                    }
                }

                break;
            case VideoView.STATE_BUFFERING:
            case VideoView.STATE_BUFFERED:
                videoLoadingBar.setLoading(false);
                break;
            case VideoView.STATE_ERROR:
                mControlWrapper.stopProgress();
                Toast.makeText(getContext(), R.string.dkplayer_error_message, Toast.LENGTH_SHORT).show();
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:

                break;
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {

    }

    @Override
    public void setProgress(int duration, int position) {

        if (videoLoadingBar != null) {
            if (duration > 0) {
                float pos = (float) (position * 1.0 / duration * 1f);
//                LogUtils.e(TAG, "setProgress: duration:" + duration + "-----position:" + position + "------rate:" + pos);
                videoLoadingBar.setProgress(pos);
            } else {
                videoLoadingBar.setLoading(true);
            }
        }
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {

    }

    public void togglePlay() {
        if (null != mControlWrapper) {
            mControlWrapper.togglePlay();
        }
    }

    public void setListener(OnVideoLayoutClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("SetTextI18n")
    public void setVideoData(VideoBean videoData) {
        this.videoData = videoData;

        if ("1".equals(videoData.getIsAd())) {
            //广告
            adTag.setVisibility(VISIBLE);
            adBtnLayout.setVisibility(VISIBLE);
            txGood.setVisibility(GONE);
            txLocation.setVisibility(GONE);
            if ("1".equals(videoData.getIsZn())) {
                ivZn.setVisibility(VISIBLE);
                adBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00AE57")));
            } else {
                adBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F95921")));
                ivZn.setVisibility(GONE);
            }

        } else {

            adTag.setVisibility(GONE);
            adBtnLayout.setVisibility(GONE);
            if (!StrUtil.isEmpty(videoData.getGoodsId()) && null != videoData.getGoods()) {
                //有带货商品
                txGood.setVisibility(VISIBLE);
                txGood.setText(videoData.getGoods().getName());
                txLocation.setVisibility(GONE);
            } else if ("1".equals(videoData.getIsPublic())) {
                //是否公开位置信息
                txLocation.setVisibility(VISIBLE);
                txLocation.setText(videoData.getCity());
                txGood.setVisibility(GONE);
            }

        }


        if (null != videoData.getMusicInfo()) {
            musicName.setText("@" + videoData.getMusicInfo().getMusicName());
        } else {
            musicName.setText("@" + videoData.getUserBean().getUsername() + "的原声");
        }
        tvNickname.setText("@" + videoData.getUserBean().getUsername());
        AutoLinkHerfManager.setContent(videoData.getTitle(), autoLinkTextView);
        ImgLoader.display(videoData.getUserBean().getAvatar(), ivHeadAnim);

        ImgLoader.display(videoData.getUserBean().getAvatar(), R.mipmap.icon_avatar_placeholder, ivHead);

        tvLikecount.setText(videoData.getLikeNum());
        tvCommentcount.setText(videoData.getCommentNum());
        tvSharecount.setText(videoData.getShareNum());
        animationView.setAnimation("like.json");
//        ImgLoader.display(R.mipmap.img_default_bg, thumb);
        ImgLoader.display(videoData.getCoverUrl(), 0, R.mipmap.img_default_bg, thumb);
        //点赞状态
        if (videoData.getLike() == 1) {
            ivLike.setBackgroundResource(R.mipmap.icon_video_like_yes);
            animationView.setVisibility(VISIBLE);
        } else {
            ivLike.setBackgroundResource(R.mipmap.icon_video_like_no);
        }

        if (AppConfig.getInstance().getUid().equals(videoData.getUid())) {
            ivFocus.setVisibility(GONE);
        } else {
            //关注状态
            if (videoData.getAttent() == 1) {
                ivFocus.setVisibility(GONE);
            } else {
                ivFocus.setVisibility(VISIBLE);
            }
        }


    }


    public void setVideoData(VideoBean videoData, int videoType) {
        this.videoData = videoData;
        boolean show = true;
        if (videoType == HttpConst.VIDEO_TYPE_PRODUCT ||
                videoType == HttpConst.VIDEO_TYPE_TREND ||
                videoType == HttpConst.VIDEO_TYPE_YPE_LIKE) {
            if (videoData.getUid().equals(AppConfig.getInstance().getUid())) {
                show = false;
            }
        }

        ivDs.setVisibility(show ? VISIBLE : INVISIBLE);

        if ("1".equals(videoData.getIsAd())) {
            //广告
            adTag.setVisibility(VISIBLE);
            adBtnLayout.setVisibility(VISIBLE);
            txGood.setVisibility(GONE);
            txLocation.setVisibility(GONE);
            if ("1".equals(videoData.getIsZn())) {
                ivZn.setVisibility(VISIBLE);
                adBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00AE57")));
            } else {
                adBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F95921")));
                ivZn.setVisibility(GONE);
            }
        } else {
            adTag.setVisibility(GONE);
            adBtnLayout.setVisibility(GONE);

            if (!StrUtil.isEmpty(videoData.getGoodsId()) && null != videoData.getGoods()) {
                //有带货商品
                txGood.setVisibility(VISIBLE);
                txGood.setText(videoData.getGoods().getName());
                txLocation.setVisibility(GONE);
            } else if ("1".equals(videoData.getIsPublic())) {
                //是否公开位置信息
                txLocation.setVisibility(VISIBLE);
                txLocation.setText(videoData.getCity());
                txGood.setVisibility(GONE);
            }
        }


        if (null != videoData.getMusicInfo()) {
            musicName.setText(String.format("@%s", videoData.getMusicInfo().getMusicName()));
        } else {
            musicName.setText(String.format("@%s的原声", videoData.getUserBean().getUsername()));
        }
        tvNickname.setText(String.format("@%s", videoData.getUserBean().getUsername()));
        AutoLinkHerfManager.setContent(videoData.getTitle(), autoLinkTextView);
        ImgLoader.display(videoData.getUserBean().getAvatar(), ivHeadAnim);
        ImgLoader.display(videoData.getUserBean().getAvatar(), R.mipmap.icon_avatar_placeholder, ivHead);
        tvLikecount.setText(videoData.getLikeNum());
        tvCommentcount.setText(videoData.getCommentNum());
        tvSharecount.setText(videoData.getShareNum());
        animationView.setAnimation("like.json");
//        ImgLoader.display(R.mipmap.img_default_bg, thumb);
        ImgLoader.display(videoData.getCoverUrl(), R.mipmap.img_default_bg, R.mipmap.img_default_bg, thumb);
        //点赞状态
        if (videoData.getLike() == 1) {
            ivLike.setBackgroundResource(R.mipmap.icon_video_like_yes);
            animationView.setVisibility(VISIBLE);
        } else {
            ivLike.setBackgroundResource(R.mipmap.icon_video_like_no);
        }

        try {
            if (AppConfig.getInstance().getUid().equals(videoData.getUid())) {
                ivFocus.setVisibility(GONE);
            } else {
                //关注状态
                if (videoData.getAttent() == 1) {
                    ivFocus.setVisibility(GONE);
                } else {
                    ivFocus.setVisibility(VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (listener == null) {
            return;
        }

        switch (v.getId()) {
            case R.id.txGood:
                listener.onClickEvent(Constants.VIDEO_CLICK_GOODS, videoData);
                break;
            case R.id.ivDs:
                listener.onClickEvent(Constants.VIDEO_CLICK_DS, videoData);
                break;
            case R.id.adBtn:
                listener.onClickEvent(Constants.VIDEO_CLICK_AD, videoData);
                break;
            case R.id.iv_head:
                listener.onClickEvent(Constants.VIDEO_CLICK_HEAD, videoData);
                break;
            case R.id.rl_like:
                if (AppConfig.getInstance().getUid().equals(videoData.getUid())) {
                    ToastUtil.show("不能给自己的视频点赞");
                    return;
                }
                listener.onClickEvent(Constants.VIDEO_CLICK_LIKE, videoData);
                like(true);
                break;
            case R.id.iv_comment:
                listener.onClickEvent(Constants.VIDEO_CLICK_COMMENT, videoData);
                break;
            case R.id.iv_share:
                listener.onClickEvent(Constants.VIDEO_CLICK_SHARE, videoData);
                break;
            case R.id.iv_focus:
                listener.onClickEvent(Constants.VIDEO_CLICK_FOLLOW, videoData);
                if (videoData.getAttent() == 0) {
                    videoData.setAttent(1);
                    ivFocus.setImageResource(R.mipmap.add_focus2);
                    if (addAnim != null) {
                        addAnim.start();
                    }
                }
                break;
            case R.id.likeview:
                if (AppConfig.getInstance().getUid().equals(videoData.getUid())) {
                    ToastUtil.show("不能给自己的视频点赞");
                    return;
                }
                togglePlay();
                break;
            case R.id.rl_record:
                listener.onClickEvent(Constants.VIDEO_CLICK_MUSIC, videoData);
                break;
        }
    }

    public void like(boolean canCancel) {
        if (videoData.getLike() == 0) {
            //点赞
            animationView.setVisibility(VISIBLE);
            animationView.playAnimation();
            ivLike.setBackgroundResource(R.mipmap.icon_video_like_yes);
            videoData.setLike(1);
        } else if (canCancel) {
            animationView.setVisibility(INVISIBLE);
            ivLike.setBackgroundResource(R.mipmap.icon_video_like_no);
            videoData.setLike(0);
        }
    }

    /**
     * 循环旋转动画
     */
    private void setRotateAnim() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 359,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setRepeatCount(INFINITE);
        rotateAnimation.setDuration(8000);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rlRecord.startAnimation(rotateAnimation);
    }

    public void updateFollow(int follow) {

        if (!addAnim.isRunning()) {
            if (follow == 1) {
                ivFocus.setVisibility(GONE);
            } else {
                ivFocus.setVisibility(VISIBLE);
            }
        }
    }

    public void clear() {
        likeView.clear();
        animationView.clearAnimation();
    }

    public void updateLike(String likeNum, int like) {
        if (videoData != null) {
            videoData.setLikeNum(likeNum);
            tvLikecount.setText(likeNum);
            if (videoData.getLike() != like) {
                ivLike.setBackgroundResource(like == 1 ? R.mipmap.icon_video_like_yes : R.mipmap.icon_video_like_no);
                videoData.setLike(like);
            }
        }
    }

    public void updateComment(String num) {
        if (videoData != null) {
            videoData.setCommentNum(num);
            tvCommentcount.setText(num);
        }
    }

    public void updateShares(String num) {
        if (videoData != null) {
            videoData.setShareNum(num);
            tvSharecount.setText(num);
        }
    }

    ObjectAnimator addAnim;

    public void initFocusAnim() {
        Interpolator interpolator = new DecelerateInterpolator();
        PropertyValuesHolder rotation = PropertyValuesHolder.ofFloat("rotation", 0f, 360f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0.5f, 1f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("ScaleX", 0.5f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("ScaleY", 0.5f, 1f);
        addAnim = ObjectAnimator.ofPropertyValuesHolder(ivFocus, rotation, alpha, scaleX, scaleY);
        addAnim.setDuration(900);
        addAnim.setInterpolator(interpolator);
        addAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (ivFocus != null) {
                    ivFocus.setImageResource(R.mipmap.add_focus);
                    ivFocus.setVisibility(GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (ivFocus != null) {
                    ivFocus.setImageResource(R.mipmap.add_focus);
                    ivFocus.setVisibility(GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
