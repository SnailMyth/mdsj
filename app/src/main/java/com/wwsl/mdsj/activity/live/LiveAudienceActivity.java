package com.wwsl.mdsj.activity.live;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.opensource.svgaplayer.SVGAImageView;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.me.ChargeActivity;
import com.wwsl.mdsj.adapter.LiveRoomScrollAdapter;
import com.wwsl.mdsj.bean.LiveBean;
import com.wwsl.mdsj.bean.LiveGiftBean;
import com.wwsl.mdsj.bean.LiveGuardInfo;
import com.wwsl.mdsj.bean.LiveUserGiftBean;
import com.wwsl.mdsj.custom.MyViewPager;
import com.wwsl.mdsj.dialog.LiveGiftDialogFragment;
import com.wwsl.mdsj.game.GamePresenter;
import com.wwsl.mdsj.game.bean.GameParam;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.presenter.LiveLinkMicAnchorPresenter;
import com.wwsl.mdsj.presenter.LiveLinkMicPkPresenter;
import com.wwsl.mdsj.presenter.LiveLinkMicPresenter;
import com.wwsl.mdsj.presenter.LiveRoomCheckLivePresenter;
import com.wwsl.mdsj.socket.SocketChatUtil;
import com.wwsl.mdsj.socket.SocketClient;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.GifCacheUtil;
import com.wwsl.mdsj.utils.L;
import com.wwsl.mdsj.utils.LiveStorge;
import com.wwsl.mdsj.utils.RandomUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;
import com.wwsl.mdsj.views.LiveAudienceViewHolder;
import com.wwsl.mdsj.views.LiveEndViewHolder;
import com.wwsl.mdsj.views.LiveRoomPlayViewHolder;
import com.wwsl.mdsj.views.LiveRoomViewHolder;
import com.wwsl.mdsj.views.LiveTxPlayViewHolder;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by cxf on 2018/10/10.
 */

public class LiveAudienceActivity extends LiveActivity {

    private static final String TAG = "LiveAudienceActivity";

    public static void forward(Context context, LiveBean liveBean, int liveType, int liveTypeVal, String key, int position) {
        Intent intent = new Intent(context, LiveAudienceActivity.class);
        intent.putExtra(Constants.LIVE_BEAN, liveBean);
        intent.putExtra(Constants.LIVE_TYPE, liveType);
        intent.putExtra(Constants.LIVE_TYPE_VAL, liveTypeVal);
        intent.putExtra(Constants.LIVE_KEY, key);
        intent.putExtra(Constants.LIVE_POSITION, position);
        context.startActivity(intent);
    }

    private String mKey;
    private int mPosition;
    private RecyclerView mRecyclerView;
    private LiveRoomScrollAdapter mRoomScrollAdapter;
    private View mMainContentView;
    private MyViewPager mViewPager;
    private ViewGroup mSecondPage;//默认显示第二页
    private FrameLayout mContainerWrap;
    //实现 腾讯播放器
    private LiveRoomPlayViewHolder mLivePlayViewHolder;
    //底部控件
    private LiveAudienceViewHolder mLiveAudienceViewHolder;
    private boolean mLighted;
    private long mLastLightClickTime;
    private boolean mEnd;
    private boolean mCoinNotEnough;//余额不足
    private LiveRoomCheckLivePresenter mCheckLivePresenter;

    //是否下载过礼物
    private boolean mLoadGift;
    private List<LiveGiftBean> giftList;
    private int giftLoadIndex = 0;
    private CommonCallback<File> mDownloadGifCallback;

    @Override
    public <T extends View> T findViewById(@IdRes int id) {
        if (AppConfig.liveRoomScroll()) {
            if (mMainContentView != null) {
                return mMainContentView.findViewById(id);
            }
        }
        return super.findViewById(id);
    }

    @Override
    protected int getLayoutId() {
        if (AppConfig.liveRoomScroll()) {
            return R.layout.activity_live_audience_2;
        }
        return R.layout.activity_live_audience;
    }

    public void setScrollFrozen(boolean frozen) {
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutFrozen(frozen);
        }
    }

    @Override
    protected void main() {
        if (AppConfig.liveRoomScroll()) {
            mRecyclerView = super.findViewById(R.id.recyclerView);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            mMainContentView = LayoutInflater.from(mContext).inflate(R.layout.activity_live_audience, null, false);
        }
        super.main();
        mLivePlayViewHolder = new LiveTxPlayViewHolder(mContext, (ViewGroup) findViewById(R.id.play_container));
        mLivePlayViewHolder.addToParent();
        addLifeCycleListener(mLivePlayViewHolder.getLifeCycleListener());
        mViewPager = findViewById(R.id.viewPager);
        mSecondPage = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.view_audience_page, mViewPager, false);
        mContainerWrap = mSecondPage.findViewById(R.id.container_wrap);
        mContainer = mSecondPage.findViewById(R.id.container);
        mLiveRoomViewHolder = new LiveRoomViewHolder(mContext, mContainer, (GifImageView) mSecondPage.findViewById(R.id.gift_gif), (SVGAImageView) mSecondPage.findViewById(R.id.gift_svga));
        mLiveRoomViewHolder.setOnLiveFinishByBackstage(() -> onLiveEnd());
        mLiveRoomViewHolder.addToParent();
        addLifeCycleListener(mLiveRoomViewHolder.getLifeCycleListener());
        mLiveAudienceViewHolder = new LiveAudienceViewHolder(mContext, mContainer);
        mLiveAudienceViewHolder.addToParent();
        mLiveAudienceViewHolder.setUnReadCount(getImUnReadCount());
        mLiveBottomViewHolder = mLiveAudienceViewHolder;
        mViewPager.setAdapter(new PagerAdapter() {

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                if (position == 0) {
                    View view = new View(mContext);
                    view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    container.addView(view);
                    return view;
                } else {
                    container.addView(mSecondPage);
                    return mSecondPage;
                }
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            }
        });
        mViewPager.setCurrentItem(1);
        mLiveLinkMicPresenter = new LiveLinkMicPresenter(mContext, mLivePlayViewHolder, false, mLiveAudienceViewHolder.getContentView());
        mLiveLinkMicAnchorPresenter = new LiveLinkMicAnchorPresenter(mContext, mLivePlayViewHolder, false, null);
        mLiveLinkMicPkPresenter = new LiveLinkMicPkPresenter(mContext, mLivePlayViewHolder, false, null);
        Intent intent = getIntent();
        if (AppConfig.liveRoomScroll()) {
            mKey = intent.getStringExtra(Constants.LIVE_KEY);
            mPosition = intent.getIntExtra(Constants.LIVE_POSITION, 0);
            List<LiveBean> list = LiveStorge.getInstance().get(mKey);
            mRoomScrollAdapter = new LiveRoomScrollAdapter(mContext, list, mPosition);
            mRoomScrollAdapter.setActionListener(new LiveRoomScrollAdapter.ActionListener() {
                @Override
                public void onPageSelected(LiveBean liveBean, ViewGroup container, boolean first) {
                    if (mMainContentView != null && container != null) {
                        ViewParent parent = mMainContentView.getParent();
                        if (parent != null) {
                            ViewGroup viewGroup = (ViewGroup) parent;
                            if (viewGroup != container) {
                                viewGroup.removeView(mMainContentView);
                                container.addView(mMainContentView);
                            }
                        } else {
                            container.addView(mMainContentView);
                        }
                    }
                    if (!first) {
                        checkLive(liveBean);
                    }
                }

                @Override
                public void onPageOutWindow(String liveUid) {
                    L.e(TAG, "onPageOutWindow----->" + liveUid);
                    if (TextUtils.isEmpty(mLiveUid) || mLiveUid.equals(liveUid)) {
                        HttpUtil.cancel(HttpConst.CHECK_LIVE);
                        HttpUtil.cancel(HttpConst.ENTER_ROOM);
                        HttpUtil.cancel(HttpConst.ROOM_CHARGE);
                        clearRoomData();
                    }
                }
            });
            mRecyclerView.setAdapter(mRoomScrollAdapter);
        }
        mLiveType = intent.getIntExtra(Constants.LIVE_TYPE, Constants.LIVE_TYPE_NORMAL);
        mLiveTypeVal = intent.getIntExtra(Constants.LIVE_TYPE_VAL, 0);
        LiveBean liveBean = intent.getParcelableExtra(Constants.LIVE_BEAN);
        setLiveRoomData(liveBean);
        enterRoom();
    }


    private void setLiveRoomData(LiveBean liveBean) {
        mLiveBean = liveBean;
        mLiveUid = liveBean.getUid();
        mStream = liveBean.getStream();
        mLivePlayViewHolder.setCover(liveBean.getThumb());
        mLivePlayViewHolder.play(liveBean.getPull());
        mLiveAudienceViewHolder.setLiveInfo(mLiveUid, mStream);
        mLiveRoomViewHolder.setAvatar(liveBean.getAvatar());
        mLiveRoomViewHolder.setAnchorLevel(liveBean.getLevelAnchor());
        mLiveRoomViewHolder.setName(liveBean.getUserNiceName());
        mLiveRoomViewHolder.setRoomNum(liveBean.getLiangNameTip());
        mLiveLinkMicPkPresenter.setLiveUid(mLiveUid);
    }


    private void clearRoomData() {
        if (mSocketClient != null) {
            mSocketClient.disConnect();
        }
        mSocketClient = null;
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.stopPlay();
        }
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.clearData();
        }
        if (mGamePresenter != null) {
            mGamePresenter.clearGame();
        }
        if (mLiveEndViewHolder != null) {
            mLiveEndViewHolder.removeFromParent();
        }
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.clearData();
        }
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.clearData();
        }
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.clearData();
        }
    }

    private void checkLive(LiveBean bean) {
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new LiveRoomCheckLivePresenter(mContext, new LiveRoomCheckLivePresenter.ActionListener() {
                @Override
                public void onLiveRoomChanged(LiveBean liveBean, int liveType, int liveTypeVal) {
                    if (liveBean == null) {
                        return;
                    }
                    setLiveRoomData(liveBean);
                    mLiveType = liveType;
                    mLiveTypeVal = liveTypeVal;
                    if (mRoomScrollAdapter != null) {
                        mRoomScrollAdapter.hideCover();
                    }
                    enterRoom();
                }
            });
        }
        mCheckLivePresenter.checkLive(bean);
    }


    private void enterRoom() {
        HttpUtil.enterRoom(mLiveUid, mStream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    mDanmuPrice = obj.getString("barrage_fee");
                    mShutTime = obj.getString("shut_time");
                    mSocketUserType = obj.getIntValue("usertype");
                    mLiveRoomViewHolder.setTgCode(obj.getString("tgcode"));
                    //连接socket
                    mSocketClient = new SocketClient(obj.getString("chatserver"), LiveAudienceActivity.this);
                    if (mLiveLinkMicPresenter != null) {
                        mLiveLinkMicPresenter.setSocketClient(mSocketClient);
                    }
                    mSocketClient.connect(mLiveUid, mStream);
                    if (mLiveRoomViewHolder != null) {
                        mLiveRoomViewHolder.refreshWatchNum(obj.getIntValue("nums"));
                        mLiveRoomViewHolder.setLiveInfo(mLiveUid, mStream, obj.getIntValue("userlist_time") * 1000, obj.getString("live_id"));
                        mLiveRoomViewHolder.startDetectGoods();
                        mLiveRoomViewHolder.showRedPackTime();
                        mLiveRoomViewHolder.setVotes(obj.getString("votestotal"));
                        mLiveRoomViewHolder.setAttention(obj.getIntValue("isattention"));
                        List<LiveUserGiftBean> list = JSON.parseArray(obj.getString("userlists"), LiveUserGiftBean.class);
                        mLiveRoomViewHolder.setUserList(list);
                        mLiveRoomViewHolder.startRefreshUserList();
                        mLiveRoomViewHolder.updateWishList();
                        if (mLiveType == Constants.LIVE_TYPE_TIME) {//计时收费
                            mLiveRoomViewHolder.startRequestTimeCharge();
                        }
                    }
                    //判断是否有连麦，要显示连麦窗口
                    String linkMicUid = obj.getString("linkmic_uid");
                    String linkMicPull = obj.getString("linkmic_pull");
                    if (!TextUtils.isEmpty(linkMicUid) && !"0".equals(linkMicUid) && !TextUtils.isEmpty(linkMicPull)) {
                        if (mLiveLinkMicPresenter != null) {
                            mLiveLinkMicPresenter.onLinkMicPlay(linkMicUid, linkMicPull);
                        }
                    }
                    //判断是否有主播连麦
                    JSONObject pkInfo = JSON.parseObject(obj.getString("pkinfo"));
                    if (pkInfo != null) {
                        String pkUid = pkInfo.getString("pkuid");
                        if (!TextUtils.isEmpty(pkUid) && !"0".equals(pkUid)) {
                            String pkPull = pkInfo.getString("pkpull");
                            if (!TextUtils.isEmpty(pkPull)) {
                                if (mLiveLinkMicAnchorPresenter != null) {
                                    mLiveLinkMicAnchorPresenter.onLinkMicAnchorPlayUrl(pkUid, pkPull);
                                }
                            }
                            if (pkInfo.getIntValue("ifpk") == 1) {//pk开始了

                                long pk_gift_liveuid = new BigDecimal(pkInfo.get("pk_gift_liveuid").toString()).longValue();
                                long pk_gift_pkuid = new BigDecimal(pkInfo.get("pk_gift_pkuid").toString()).longValue();
                                mLiveLinkMicPkPresenter.onEnterRoomPkStart(pkUid, pk_gift_liveuid, pk_gift_pkuid, pkInfo.getIntValue("pk_time") * 1000);
                            }
                        }
                    }

                    //守护相关
                    mLiveGuardInfo = new LiveGuardInfo();
                    int guardNum = obj.getIntValue("guard_nums");
                    mLiveGuardInfo.setGuardNum(guardNum);
                    JSONObject guardObj = obj.getJSONObject("guard");
                    if (guardObj != null) {
                        mLiveGuardInfo.setMyGuardType(guardObj.getIntValue("type"));
                        mLiveGuardInfo.setMyGuardEndTime(guardObj.getString("endtime"));
                    }
                    if (mLiveRoomViewHolder != null) {
                        mLiveRoomViewHolder.setGuardNum(guardNum);
                        //红包相关
//                        mLiveRoomViewHolder.setRedPackBtnVisible(obj.getIntValue("isred") == 1);
                    }

                    //游戏相关
                    if (AppConfig.GAME_ENABLE) {
                        GameParam param = new GameParam();
                        param.setContext(mContext);
                        param.setParentView(mContainerWrap);
                        param.setTopView(mContainer);
                        param.setInnerContainer(mLiveRoomViewHolder.getInnerContainer());
                        param.setSocketClient(mSocketClient);
                        param.setLiveUid(mLiveUid);
                        param.setStream(mStream);
                        param.setAnchor(false);
                        param.setCoinName(mCoinName);
                        param.setObj(obj);
                        if (mGamePresenter == null) {
                            mGamePresenter = new GamePresenter();
                        }
                        mGamePresenter.setGameParam(param);
                    }
                }
            }
        });
    }

    /**
     * 打开礼物窗口
     */
    public void openGiftWindow() {
        if (TextUtils.isEmpty(mLiveUid) || TextUtils.isEmpty(mStream)) {
            return;
        }
        LiveGiftDialogFragment fragment = new LiveGiftDialogFragment();
        fragment.setLiveGuardInfo(mLiveGuardInfo);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_UID, mLiveUid);
        bundle.putString(Constants.LIVE_STREAM, mStream);
        fragment.setArguments(bundle);
        fragment.show(((LiveAudienceActivity) mContext).getSupportFragmentManager(), "LiveGiftDialogFragment");

        if (!mLoadGift) {
            mLoadGift = true;
            if (mDownloadGifCallback == null) {
                mDownloadGifCallback = new CommonCallback<File>() {
                    @Override
                    public void callback(File file) {
                        giftLoadIndex++;
                        loadGiftFile();
                    }
                };
            }
            giftList = AppConfig.getInstance().getGiftList();
            if (giftList != null) {
                loadGiftFile();
            }
        }
    }


    /**
     * 结束观看
     */
    private void endPlay() {
        HttpUtil.cancel(HttpConst.ENTER_ROOM);
        if (mEnd) {
            return;
        }
        mEnd = true;
        HttpUtil.cancel(HttpConst.ENTER_ROOM);
        //断开socket
        if (mSocketClient != null) {
            mSocketClient.disConnect();
        }
        mSocketClient = null;
        //结束播放
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.release();
        }
        mLivePlayViewHolder = null;
        release();
    }

    @Override
    protected void release() {
        super.release();
        if (mRoomScrollAdapter != null) {
            mRoomScrollAdapter.setActionListener(null);
        }
        mRoomScrollAdapter = null;
    }

    /**
     * 观众收到直播结束消息
     */
    @Override
    public void onLiveEnd() {
        super.onLiveEnd();
        if (!AppConfig.liveRoomScroll()) {
            if (mViewPager != null) {
                if (mViewPager.getCurrentItem() != 1) {
                    mViewPager.setCurrentItem(1, false);
                }
                mViewPager.setCanScroll(false);
            }
            endPlay();
        }
        if (mLiveEndViewHolder == null) {
            mLiveEndViewHolder = new LiveEndViewHolder(mContext, mSecondPage);
            addLifeCycleListener(mLiveEndViewHolder.getLifeCycleListener());
            mLiveEndViewHolder.addToParent();
        }
        mLiveEndViewHolder.showData(mLiveBean, mStream);
    }


    /**
     * 观众收到踢人消息
     */
    @Override
    public void onKick(String touid) {
        if (!TextUtils.isEmpty(touid) && touid.equals(AppConfig.getInstance().getUid())) {//被踢的是自己
            exitLiveRoom();
            ToastUtil.show(WordUtil.getString(R.string.live_kicked_2));
        }
    }

    /**
     * 观众收到禁言消息
     */
    @Override
    public void onShutUp(String touid, String content) {
        if (!TextUtils.isEmpty(touid) && touid.equals(AppConfig.getInstance().getUid())) {
            DialogUtil.showSimpleDialog(mContext, content, null);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mEnd && !canBackPressed()) {
            return;
        }
        exitLiveRoom();
    }

    /**
     * 退出直播间
     */
    public void exitLiveRoom() {
        endPlay();
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        endPlay();
        L.e("LiveAudienceActivity-------onDestroy------->");
    }

    /**
     * 点亮
     */
    public void light() {
        if (!mLighted) {
            mLighted = true;
            int guardType = mLiveGuardInfo != null ? mLiveGuardInfo.getMyGuardType() : Constants.GUARD_TYPE_NONE;
            SocketChatUtil.sendLightMessage(mSocketClient, 1 + RandomUtil.nextInt(6), guardType);
        } else {
            long cutTime = System.currentTimeMillis();
            if (cutTime - mLastLightClickTime < 5000) {
                if (mLiveRoomViewHolder != null) {
                    mLiveRoomViewHolder.playLightAnim();
                }
            } else {
                mLastLightClickTime = cutTime;
                SocketChatUtil.sendFloatHeart(mSocketClient);
            }
        }
    }


    /**
     * 计时收费更新主播映票数
     */
    public void roomChargeUpdateVotes() {
        sendUpdateVotesMessage(mLiveTypeVal);
    }

    /**
     * 暂停播放
     */
    public void pausePlay() {
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.pausePlay();
        }
    }

    /**
     * 恢复播放
     */
    public void resumePlay() {
        if (mLivePlayViewHolder != null) {
            mLivePlayViewHolder.resumePlay();
        }
    }

    /**
     * 充值成功
     */
    public void onChargeSuccess() {
        if (mLiveType == Constants.LIVE_TYPE_TIME) {
            if (mCoinNotEnough) {
                mCoinNotEnough = false;
                HttpUtil.roomCharge(mLiveUid, mStream, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            roomChargeUpdateVotes();
                            if (mLiveRoomViewHolder != null) {
                                resumePlay();
                                mLiveRoomViewHolder.startRequestTimeCharge();
                            }
                        } else {
                            if (code == 1008) {//余额不足
                                mCoinNotEnough = true;
                                DialogUtil.showSimpleDialog(mContext, WordUtil.getString(R.string.live_coin_not_enough), false,
                                        new DialogUtil.SimpleCallback2() {
                                            @Override
                                            public void onConfirmClick(Dialog dialog, String content) {
                                                ChargeActivity.forward(mContext);
                                            }

                                            @Override
                                            public void onCancelClick() {
                                                exitLiveRoom();
                                            }
                                        });
                            }
                        }
                    }
                });
            }
        }
    }

    public void setCoinNotEnough(boolean coinNotEnough) {
        mCoinNotEnough = coinNotEnough;
    }

    /**
     * 加载礼物文件
     */
    private void loadGiftFile() {
        if (giftLoadIndex < giftList.size()) {
            for (int i = giftLoadIndex; i < giftList.size(); i++) {
                LiveGiftBean bean = giftList.get(i);
                if (bean.getType() == 1 && !TextUtils.isEmpty(bean.getSwf()) && (bean.getSwf().endsWith(".gif") || bean.getSwf().endsWith(".svga"))) {
                    GifCacheUtil.getFile(Constants.GIF_GIFT_PREFIX + bean.getId(), bean.getSwf(), mDownloadGifCallback);
                    giftLoadIndex = i;
                    break;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.showAudience();
        }
    }
}
