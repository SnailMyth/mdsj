package com.wwsl.mdsj.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.dueeeke.videoplayer.player.VideoView;
import com.frame.fire.util.LogUtils;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.VideoBean;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.ScaleUtils;
import com.wwsl.mdsj.utils.cache.PreloadManager;
import com.wwsl.mdsj.utils.tiktok.OnVideoLayoutClickListener;
import com.wwsl.mdsj.utils.tiktok.TikTokController;
import com.wwsl.mdsj.utils.tiktok.TikTokRenderViewFactory;
import com.wwsl.mdsj.views.TikTokView;
import com.zj.zjsdk.ad.ZjAdError;
import com.zj.zjsdk.ad.ZjSize;
import com.zj.zjsdk.ad.express.ZjExpressFullVideoFeed;
import com.zj.zjsdk.ad.express.ZjExpressFullVideoFeedAd;
import com.zj.zjsdk.ad.express.ZjExpressFullVideoFeedListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VideoAdapter extends BaseMultiItemQuickAdapter<VideoBean, BaseViewHolder> {

    private final static String TAG = "VideoAdapter";
    private OnVideoLayoutClickListener listener;
    private TikTokRenderViewFactory tikTokRenderViewFactory;
    private int videoType;
    private Context mContext;
    private Activity mActivity;

    public VideoAdapter(@Nullable List<VideoBean> data, Context context, int videoType) {
        super(data);
        addItemType(3, R.layout.item_tik_tok);//正常视频
        addItemType(1, R.layout.item_tik_tok);//助农
        addItemType(2, R.layout.item_tik_tok);//广告
        addItemType(99, R.layout.item_tik_tok_zj);//众简广告
        this.mContext = context;
        this.videoType = videoType;
        this.tikTokRenderViewFactory = TikTokRenderViewFactory.create();
    }

    @Override
    public int getItemViewType(int position) {
        return getData().get(position).getItemType();
    }

    public void setmActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void setLayoutClickListener(OnVideoLayoutClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, VideoBean item) {
        switch (item.getItemType()) {
            case 99:
                FrameLayout container = helper.getView(R.id.container_ad);
                DisplayMetrics dm = new DisplayMetrics();
                mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
                int i = ScaleUtils.px2dip(mContext, dm.heightPixels) - 10;

                ZjExpressFullVideoFeed expressFullVideoFeed = new ZjExpressFullVideoFeed(
                        mActivity, "zjad_945429072",
                        new ZjSize(dm.widthPixels, i),
                        new ZjExpressFullVideoFeedListener() {
                            @Override
                            public void onZjExpressFullVideoFeedAdDataLoaded(List<ZjExpressFullVideoFeedAd> list) {
                                if (list == null || list.isEmpty()) return;
                                for (ZjExpressFullVideoFeedAd ad : list) {
                                    ad.setCanInterruptVideoPlay(true);
                                    ad.setExpressInteractionListener(new ZjExpressFullVideoFeedAd.ExpressAdInteractionListener() {
                                        @Override
                                        public void onAdClicked(View view, int i) {

                                        }

                                        @Override
                                        public void onAdShow(View view, int i) {

                                        }

                                        @Override
                                        public void onRenderSuccess(View view, float v, float v1) {
                                            container.removeAllViews();
                                            container.addView(view);
                                        }

                                        @Override
                                        public void onRenderFail(View view, ZjAdError zjAdError) {

                                        }
                                    });
                                    ad.render();
                                }
                            }

                            @Override
                            public void onZjAdError(ZjAdError zjAdError) {

                            }
                        });
                expressFullVideoFeed.loadAd(1);
                break;
            default:
                TikTokView view = helper.getView(R.id.tiktok_View);
                VideoView mVideoView = helper.getView(R.id.videoView);
                view.setVideoData(item, videoType);
                view.setListener(listener);
                mVideoView.setRenderViewFactory(tikTokRenderViewFactory);
                mVideoView.setLooping(true);
                TikTokController mController = new TikTokController(mContext);
                mVideoView.setVideoController(mController);
                String playUrl = PreloadManager.getInstance(mContext).getPlayUrl(item.getVideoUrl());
                mVideoView.setUrl(playUrl);
                mController.addControlComponent(view, true);
                break;
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull BaseViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        if (getItemViewType(holder.getLayoutPosition()) == 99) return;

        TikTokView view = holder.getView(R.id.tiktok_View);
        view.clear();
        VideoView mVideoView = holder.getView(R.id.videoView);
        if (mVideoView.isPlaying()) {
            LogUtils.e(TAG, "onViewDetachedFromWindow: pause");
            mVideoView.pause();
        }
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, VideoBean item, @NotNull List<?> payloads) {
        for (Object p : payloads) {
            int payload = (int) p;
            TikTokView view = holder.getView(R.id.tiktok_View);
            if (payload == PAYLOAD_FOLLOW) {
                view.updateFollow(item.getAttent());
            } else if (payload == PAYLOAD_LIKE) {
                view.updateLike(item.getLikeNum(), item.getLike());
            } else if (payload == PAYLOAD_COMMENT) {
                view.updateComment(item.getCommentNum());
            } else if (payload == PAYLOAD_SHARE) {
                view.updateShares(item.getShareNum());
            }
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewRecycled(@NonNull BaseViewHolder holder) {

        if (getItemViewType(holder.getLayoutPosition()) == 99) return;

        int position = holder.getLayoutPosition();
        VideoView view = holder.getView(R.id.videoView);
        view.release();
        if (position < getData().size()) {
            //取消预加载
            PreloadManager.getInstance(mContext).removePreloadTask(getData().get(position).getVideoUrl());
            HttpUtil.cancel(String.format("%d%s", Constants.FOLLOW_FROM_VIDEO_LIKE, getData().get(position).getId()));
        }
        HttpUtil.cancel(HttpConst.SET_ATTENTION);
    }

    public static final int PAYLOAD_FOLLOW = 901;
    public static final int PAYLOAD_LIKE = 902;
    public static final int PAYLOAD_COMMENT = 903;
    public static final int PAYLOAD_SHARE = 904;

    /**
     * 关注状态更新
     */
    public void onFollowChanged(String toUid, int isAttention) {
        for (VideoBean bean : getData()) {
            if (toUid.equals(bean.getUid())) {
                int index = getData().indexOf(bean);

                bean.setAttent(isAttention);
                notifyItemChanged(index, PAYLOAD_FOLLOW);
                break;
            }
        }
    }

    /**
     * 喜欢状态更新
     */
    public void onlike(String id, String likeNum, int like) {
        for (VideoBean bean : getData()) {
            if (id.equals(bean.getId())) {
                int index = getData().indexOf(bean);

                bean.setLike(like);
                bean.setLikeNum(likeNum);
                notifyItemChanged(index, PAYLOAD_LIKE);
                break;
            }
        }
    }

    /**
     * 评论数量更新
     */
    public void onComment(String id, String num) {
        for (VideoBean bean : getData()) {
            if (id.equals(bean.getId())) {
                int index = getData().indexOf(bean);

                bean.setCommentNum(num);
                notifyItemChanged(index, PAYLOAD_COMMENT);
                break;
            }
        }
    }

    public void onShare(String id, String num) {
        List<VideoBean> data = getData();
        for (int i = 0; i < data.size(); i++) {
            if (id.equals(data.get(i).getId())) {
                getData().get(i).setShareNum(num);
                notifyItemChanged(i, PAYLOAD_SHARE);
                break;
            }
        }
    }
}