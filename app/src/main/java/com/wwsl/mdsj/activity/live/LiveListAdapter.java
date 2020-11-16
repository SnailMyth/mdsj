package com.wwsl.mdsj.activity.live;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.LiveBean;
import com.wwsl.mdsj.bean.LiveListBean;
import com.wwsl.mdsj.glide.ImgLoader;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;


public class LiveListAdapter extends BaseQuickAdapter<LiveListBean, BaseViewHolder> {

    public LiveListAdapter(@Nullable List<LiveListBean> data) {
        super(R.layout.item_live_list, data);
        players = new HashMap<>();
        videoViews = new HashMap<>();
    }

    private Map<String, TXLivePlayer> players;
    private Map<String, TXCloudVideoView> videoViews;

    @Override
    protected void convert(@NonNull BaseViewHolder helper, LiveListBean item) {
        LiveBean liveBean = item.getLiveBean();
        //渲染数据
        helper.setText(R.id.tvTitle, liveBean.getUserNiceName() + "的直播");
        helper.setText(R.id.tvAudienceNum, liveBean.getNums() + "观看");
        helper.setText(R.id.tvSubType, "频道 |" + liveBean.getTypeVal());
        helper.setText(R.id.txMainCity, liveBean.getCity());
        helper.setText(R.id.tvName, liveBean.getUserNiceName());
        helper.setText(R.id.tvFansNum, "粉丝" + liveBean.getNums());

        ImgLoader.displayAvatar(liveBean.getAvatar(), helper.getView(R.id.ivAvatar));

        TXLivePlayer mPlayer = new TXLivePlayer(getContext());
        TXCloudVideoView view = helper.getView(R.id.videoView);

        mPlayer.setConfig(new TXLivePlayConfig());
        mPlayer.setPlayerView(view);

        //尽量保证唯一  不然下拉后退出无法关闭直播声音
        TXLivePlayer player = players.get(liveBean.getUid());
        if (player == null) players.put(liveBean.getUid(), mPlayer);

        TXCloudVideoView videoView = videoViews.get(liveBean.getUid());
        if (videoView == null) videoViews.put(liveBean.getUid(), view);

        ImgLoader.display(liveBean.getThumb(), helper.getView(R.id.ivCover));

        //播放加载完后  需要播放的item
        ConstraintLayout dataLayout = helper.getView(R.id.dataLayout);
        ConstraintLayout liveLayout = helper.getView(R.id.liveLayout);
        LiveListPlayListener liveListPlayListener = new LiveListPlayListener(dataLayout, liveLayout);

        mPlayer.setPlayListener(liveListPlayListener);
        String pull = liveBean.getPull();
        if (item.isNeedPlay() && !StrUtil.isEmpty(pull)) {
            mPlayer.startPlay(pull, TXLivePlayer.PLAY_TYPE_LIVE_RTMP);
        }
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, LiveListBean item, @NotNull List<?> payloads) {
        super.convert(holder, item, payloads);
        if (payloads.size() > 0) {
            for (int i = 0; i < payloads.size(); i++) {
                int tag = (Integer) payloads.get(i);
                if (tag == PAYLOAD_PLAY) {
                    LiveBean liveBean = item.getLiveBean();
                    String pull = liveBean.getPull();
                    TXCloudVideoView videoView = holder.getView(R.id.videoView);
                    TXLivePlayer txPlayer = players.get(liveBean.getUid());
                    ConstraintLayout dataLayout = holder.getView(R.id.dataLayout);
                    ConstraintLayout liveLayout = holder.getView(R.id.liveLayout);
                    if (txPlayer == null) {
                        txPlayer = new TXLivePlayer(getContext());
                        txPlayer.setConfig(new TXLivePlayConfig());
                        txPlayer.setPlayerView((videoView));
                        players.put(liveBean.getUid(), txPlayer);
                    }

                    if (item.isNeedPlay() && !StrUtil.isEmpty(pull)) {
                        txPlayer.startPlay(pull, TXLivePlayer.PLAY_TYPE_LIVE_RTMP);
                    } else {
                        if (item.isPlay()) {
                            dataLayout.setVisibility(View.VISIBLE);
                            liveLayout.setVisibility(View.GONE);
                            txPlayer.stopPlay(true);

                            int itemPosition = getItemPosition(item);
                            getData().get(itemPosition).setPlay(false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onViewRecycled(@NonNull BaseViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public static final int PAYLOAD_PLAY = 101;

    public void release() {
        if (players == null) return;

        for (Map.Entry<String, TXLivePlayer> entry : players.entrySet()) {
            TXLivePlayer player = players.get(entry.getKey());
            if (player != null) {
                player.pause();
                player.stopPlay(true);
            }
        }

        for (Map.Entry<String, TXCloudVideoView> entry : videoViews.entrySet()) {
            TXCloudVideoView videoView = videoViews.get(entry.getKey());
            if (videoView != null) {
                videoView.onPause();
                videoView.onDestroy();
            }
        }
    }

}
