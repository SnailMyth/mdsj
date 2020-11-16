package com.wwsl.mdsj.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.opensource.svgaplayer.SVGAImageView;
import com.qiniu.android.utils.StringUtils;
import com.shehuan.niv.NiceImageView;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.WebViewActivity;
import com.wwsl.mdsj.activity.live.LiveActivity;
import com.wwsl.mdsj.activity.live.LiveAnchorActivity;
import com.wwsl.mdsj.activity.live.LiveAnchorGoodsDialogFragment;
import com.wwsl.mdsj.activity.live.LiveAudienceActivity;
import com.wwsl.mdsj.activity.live.LiveAudienceGoodsDialogFragment;
import com.wwsl.mdsj.activity.me.ChargeActivity;
import com.wwsl.mdsj.adapter.LiveChatAdapter;
import com.wwsl.mdsj.adapter.LiveUserAdapter;
import com.wwsl.mdsj.adapter.LiveWishAdapter;
import com.wwsl.mdsj.adapter.WebBannerAdapter;
import com.wwsl.mdsj.bean.LevelBean;
import com.wwsl.mdsj.bean.LiveBuyGuardMsgBean;
import com.wwsl.mdsj.bean.LiveChatBean;
import com.wwsl.mdsj.bean.LiveDanMuBean;
import com.wwsl.mdsj.bean.LiveEnterRoomBean;
import com.wwsl.mdsj.bean.LiveReceiveGiftBean;
import com.wwsl.mdsj.bean.LiveUserGiftBean;
import com.wwsl.mdsj.bean.LiveWishBean;
import com.wwsl.mdsj.bean.OutActivityBean;
import com.wwsl.mdsj.bean.RedPackBean;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.bean.WishBillBean;
import com.wwsl.mdsj.bean.net.NetGoodsBean;
import com.wwsl.mdsj.custom.DrawableTextView;
import com.wwsl.mdsj.custom.TopGradual;
import com.wwsl.mdsj.dialog.H5DialogFragment;
import com.wwsl.mdsj.dialog.LiveUserDialogFragment;
import com.wwsl.mdsj.dialog.WishBillListDialogFragment;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.interfaces.LifeCycleAdapter;
import com.wwsl.mdsj.interfaces.OnItemClickListener;
import com.wwsl.mdsj.interfaces.OnWishBillSendItemClickListener;
import com.wwsl.mdsj.presenter.LiveDanmuPresenter;
import com.wwsl.mdsj.presenter.LiveEnterRoomAnimPresenter;
import com.wwsl.mdsj.presenter.LiveGiftAnimPresenter2;
import com.wwsl.mdsj.presenter.LiveLightAnimPresenter;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.L;
import com.wwsl.mdsj.utils.StringUtil;
import com.wwsl.mdsj.utils.WordUtil;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by cxf on 2018/10/9.
 * 直播间公共逻辑
 */

public class LiveRoomViewHolder extends AbsViewHolder implements View.OnClickListener {

    public static int sOffsetY = 0;
    private ViewGroup mRoot;
    private ImageView mAvatar;
    private ImageView mLevelAnchor;
    private TextView mName;
    private TextView mInvite;
    private TextView tvWatchNum;
    private TextView tvWishShow;
    private TextView tvWishShowBig;
    private ImageView ivCoupon;//优惠券
    //    private RecyclerView wishRecycler;
    private String mLiveId;
    private VerticalScrollLayout wishRecycler;
    private ScrollTextView tvBanner;
    private TextView mID;
    private View mBtnFollow;
    private TextView mVotesName;//映票名称
    private TextView mVotes;
    private TextView mGuardNum;//守护人数
    private RecyclerView mUserRecyclerView;
    private RecyclerView mChatRecyclerView;
    private LiveUserAdapter mLiveUserAdapter;
    private LiveChatAdapter mLiveChatAdapter;
    private View mBtnRedPack;
    private DrawableTextView ivMyGood;
    private ConstraintLayout audienceGoodsLayout;
    private NiceImageView audienceGoods;
    TextView redPackTime;
    private String mLiveUid;
    private String mLiveName;
    private String mTgCode;
    private String mStream;
    private LiveLightAnimPresenter mLightAnimPresenter;
    private LiveEnterRoomAnimPresenter mLiveEnterRoomAnimPresenter;
    private LiveDanmuPresenter mLiveDanmuPresenter;
    private LiveGiftAnimPresenter2 mLiveGiftAnimPresenter;
    private LiveRoomHandler mLiveRoomHandler;
    private HttpCallback mRefreshUserListCallback;
    private HttpCallback mTimeChargeCallback;
    protected int mUserListInterval;//用户列表刷新时间的间隔
    private GifImageView mGifImageView;
    private SVGAImageView mSVGAImageView;
    private TextView mLiveTimeTextView;//主播的直播时长
    private long mAnchorLiveTime;//主播直播时间
    private List<OutActivityBean> outActivityBeans;
    private String tipBannerStr;
    private BannerLayout recyclerBanner;//活动banner
    private WebBannerAdapter webBannerAdapter;
    private MyHandler myHandler;
    private LiveWishAdapter wishAdapter;

    private OnLiveFinishByBackstage onLiveFinishByBackstage;
    LinearLayout guardLayout;

    public LiveRoomViewHolder(Context context, ViewGroup parentView, GifImageView gifImageView, SVGAImageView svgaImageView) {
        super(context, parentView);
        mGifImageView = gifImageView;
        mSVGAImageView = svgaImageView;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_room;
    }

    @Override
    public void init() {
        mRoot = (ViewGroup) findViewById(R.id.root);
        initLeftPanel();

        tvBanner = (ScrollTextView) findViewById(R.id.tvBanner);
        initBannerText();//初始化顶部跑马灯文字

        mAvatar = (ImageView) findViewById(R.id.avatar);
        mLevelAnchor = (ImageView) findViewById(R.id.level_anchor);
        mName = (TextView) findViewById(R.id.name);
        mInvite = (TextView) findViewById(R.id.id_invite);
        tvWatchNum = (TextView) findViewById(R.id.tvWatchNum);
        //优惠券
        ivCoupon = (ImageView) findViewById(R.id.iv_coupon);
        ivCoupon.setTag(null);
        ivCoupon.setOnClickListener(view -> {
            if (view.getTag() == null) return;
            WebViewActivity.forward2(mContext, (String) view.getTag());
        });
//        refreshWatchNum(0);
        initWish();


        mID = (TextView) findViewById(R.id.id_val);
        mBtnFollow = findViewById(R.id.btn_follow);
        mVotesName = (TextView) findViewById(R.id.votes_name);
        mVotesName.setText(AppConfig.getInstance().getVotesName());
        mVotes = (TextView) findViewById(R.id.votes);
        mGuardNum = (TextView) findViewById(R.id.guard_num);
        //用户头像列表
        mUserRecyclerView = (RecyclerView) findViewById(R.id.user_recyclerView);
        mUserRecyclerView.setHasFixedSize(true);
        mUserRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mLiveUserAdapter = new LiveUserAdapter(mContext);
        mLiveUserAdapter.setOnItemClickListener(new OnItemClickListener<UserBean>() {
            @Override
            public void onItemClick(UserBean bean, int position) {
                showUserDialog(bean.getId());
            }
        });
        mUserRecyclerView.setAdapter(mLiveUserAdapter);
        //聊天栏
        mChatRecyclerView = (RecyclerView) findViewById(R.id.chat_recyclerView);
        mChatRecyclerView.setHasFixedSize(true);
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mChatRecyclerView.addItemDecoration(new TopGradual());
        mLiveChatAdapter = new LiveChatAdapter(mContext);
        mLiveChatAdapter.setOnItemClickListener(new OnItemClickListener<LiveChatBean>() {
            @Override
            public void onItemClick(LiveChatBean bean, int position) {
                showUserDialog(bean.getId());
            }
        });
        mChatRecyclerView.setAdapter(mLiveChatAdapter);
//        mVotesName.setText(AppConfig.getInstance().getVotesName());
        mBtnFollow.setOnClickListener(this);
        mAvatar.setOnClickListener(this);
        findViewById(R.id.btn_votes).setOnClickListener(this);
        findViewById(R.id.btn_guard).setOnClickListener(this);
        guardLayout = (LinearLayout) findViewById(R.id.btn_guard);
        mBtnRedPack = findViewById(R.id.btnRedPack);
        ivMyGood = (DrawableTextView) findViewById(R.id.ivAddGood);
        audienceGoodsLayout = (ConstraintLayout) findViewById(R.id.audienceGoodsLayout);
        audienceGoods = (NiceImageView) findViewById(R.id.audienceGoods);
        redPackTime = (TextView) findViewById(R.id.txRedPackTime);
        mBtnRedPack.setOnClickListener(this);
        audienceGoodsLayout.setOnClickListener(this);
        ivMyGood.setOnClickListener(this);
        myHandler = new MyHandler(redPackTime);
        if (mContext instanceof LiveAudienceActivity) {
            mRoot.setOnClickListener(this);
        } else {
            mLiveTimeTextView = (TextView) findViewById(R.id.live_time);
            mLiveTimeTextView.setVisibility(View.VISIBLE);
        }
        mLifeCycleListener = new LifeCycleAdapter() {
            @Override
            public void onDestroy() {
                HttpUtil.cancel(HttpConst.GET_USER_LIST);
                HttpUtil.cancel(HttpConst.TIME_CHARGE);
                HttpUtil.cancel(HttpConst.SET_ATTENTION);
                L.e("LiveRoomViewHolder-------->onDestroy");
            }
        };
        mLightAnimPresenter = new LiveLightAnimPresenter(mContext, mParentView);
        mLiveEnterRoomAnimPresenter = new LiveEnterRoomAnimPresenter(mContext, mContentView);
        mLiveRoomHandler = new LiveRoomHandler(this);
        mRefreshUserListCallback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    if (mLiveUserAdapter != null) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        List<LiveUserGiftBean> list = JSON.parseArray(obj.getString("userlist"), LiveUserGiftBean.class);
                        mLiveUserAdapter.refreshList(list);
                        refreshWatchNum(obj.getIntValue("nums"));
                        if (obj.containsKey("islive") && obj.getIntValue("islive") == 0) {
                            //后台关播
                            if (onLiveFinishByBackstage != null) {
                                onLiveFinishByBackstage.onFinish();
                            }
                        }
                    }
                }
            }
        };
        mTimeChargeCallback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (mContext instanceof LiveAudienceActivity) {
                    final LiveAudienceActivity liveAudienceActivity = (LiveAudienceActivity) mContext;
                    if (code == 0) {
                        liveAudienceActivity.roomChargeUpdateVotes();
                    } else {
                        if (mLiveRoomHandler != null) {
                            mLiveRoomHandler.removeMessages(LiveRoomHandler.WHAT_TIME_CHARGE);
                        }
                        liveAudienceActivity.pausePlay();
                        if (code == 1008) {//余额不足
                            liveAudienceActivity.setCoinNotEnough(true);
                            DialogUtil.showSimpleDialog(mContext, WordUtil.getString(R.string.live_coin_not_enough), false,
                                    new DialogUtil.SimpleCallback2() {
                                        @Override
                                        public void onConfirmClick(Dialog dialog, String content) {
                                            ChargeActivity.forward(mContext);
                                        }

                                        @Override
                                        public void onCancelClick() {
                                            liveAudienceActivity.exitLiveRoom();
                                        }
                                    });
                        }
                    }
                }
            }
        };
    }

    private void initWish() {
        tvWishShow = (TextView) findViewById(R.id.tvWishShow);
        tvWishShowBig = (TextView) findViewById(R.id.tvWishShowBig);
        tvWishShowBig.setVisibility(View.VISIBLE);
        tvWishShowBig.setOnClickListener(this);

        wishRecycler = (VerticalScrollLayout) findViewById(R.id.wishRecycler);

        wishAdapter = new LiveWishAdapter(mContext);

        wishRecycler.setAdapter(wishAdapter);
    }

    public void updateWishList() {
        HttpUtil.getWishList(mLiveUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (info != null && info.length > 0) {
                    List<LiveWishBean> beans = parseWishBean(JSON.parseArray(info[0], WishBillBean.class));
                    if (wishAdapter.getCount() > 0) {
                        wishAdapter.updateNum(beans);
                    } else {
                        wishAdapter.setList(beans);
                    }
                    if (beans.size() > 0) {
                        if (null != mLiveRoomHandler) {
                            //60s刷新礼物数量
                            mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_WISH_UPDATE, 10000);
                        }
                    }
                }
            }
        });

        if (ivCoupon.getTag() != null) return;
        HttpUtil.getCoupon(mLiveUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (info.length > 0) {
                    try {
                        org.json.JSONObject object = new org.json.JSONObject(info[0]);
                        ImgLoader.display(object.optString("thumb"), ivCoupon);
                        String link = object.optString("coupon_link");
                        if (link.length() != 0) {
                            ivCoupon.setVisibility(View.VISIBLE);
                            ivCoupon.setTag(link);
                        } else {
                            ivCoupon.setVisibility(View.GONE);
                            ivCoupon.setTag(null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private List<LiveWishBean> parseWishBean(List<WishBillBean> wishBillBeans) {
        List<LiveWishBean> liveWishBeans = new ArrayList<>();
        if (null != wishBillBeans) {
            for (WishBillBean bean : wishBillBeans) {
                liveWishBeans.add(LiveWishBean.builder()
                        .id(bean.getId())
                        .icon(bean.getGifticon())
                        .name(bean.getGiftname())
                        .num(bean.getSendnum())
                        .total(bean.getNum())
                        .build());
            }
        }
        return liveWishBeans;
    }

    public void showRedPackTime() {
        HttpUtil.getRedPackList(mStream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<RedPackBean> list = JSON.parseArray(Arrays.toString(info), RedPackBean.class);
                    int time = -1;
                    int temp = 181;

                    for (RedPackBean bean : list) {
                        int robTime = bean.getRobTime();

                        if (robTime > 0) {
                            temp = Math.min(robTime, temp);
                        }
                    }
                    if (temp < 181) {
                        time = temp - 1;
                    }

                    if (time > 0) {
                        redPackTime.setVisibility(View.VISIBLE);
                        redPackTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                        redPackTime.setText(StringUtil.getDurationText(time * 1000));
                        myHandler.sendEmptyMessageDelayed(time, 1000);
                    } else {
                        redPackTime.setVisibility(View.GONE);
                    }

                } else {
                    L.e("no redpack");
                }
            }
        });
    }

    private void updateTime(TextView textView, int robTime) {
        if (textView != null) {
            if (robTime > 0) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(StringUtil.getDurationText(robTime * 1000));
                if (myHandler != null) {
                    myHandler.sendEmptyMessageDelayed(robTime - 1, 1000);
                }
            } else {
                textView.setVisibility(View.GONE);
                showRedPackTime();
            }
        }
    }

    private void initLeftPanel() {

    }


    private void initBannerText() {
        //文字轮播
        if (!StringUtils.isNullOrEmpty(tipBannerStr)) {
            tvBanner.setText(tipBannerStr);
        } else {
            HttpUtil.getLiveInfo(new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    L.e("getLiveInfo", "onSuccess: " + info[0]);
                    Object parse = JSONObject.parse(info[0]);
                    JSONObject object = JSON.parseObject(info[0]);
                    tipBannerStr = object.getString("gun_word").intern();
                    tvBanner.setText(tipBannerStr);
                }
            });
        }


        //左边图片轮播
        recyclerBanner = (BannerLayout) findViewById(R.id.bannerActive);
        webBannerAdapter = new WebBannerAdapter(mContext, new ArrayList<>());

        webBannerAdapter.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String content = outActivityBeans.get(position).getPostExcerpt();
                if (StrUtil.isEmpty(content)) {
                    content = outActivityBeans.get(position).getPostContent();
                }
                openH5Window(content);
            }
        });

        recyclerBanner.setAdapter(webBannerAdapter);

        outActivityBeans = new ArrayList<>();
        showActivityIcon();
    }

    private void showActivityIcon() {
        recyclerBanner.setVisibility(View.VISIBLE);
        List<String> images = new ArrayList<>();
        for (OutActivityBean bean : outActivityBeans) {
            images.add(bean.getIcon());
        }

        webBannerAdapter.updateData(images);
    }

    /**
     * 显示主播头像
     */
    public void setAvatar(String url) {
        if (mAvatar != null) {
            ImgLoader.displayAvatar(url, mAvatar);
        }
    }

    public void openH5Window(String h5) {

        if (TextUtils.isEmpty(mLiveUid) || TextUtils.isEmpty(mStream)) {
            return;
        }

        H5DialogFragment fragment = new H5DialogFragment();
        if (h5.startsWith("http") || h5.startsWith("https")) {
            h5 = Html.fromHtml(h5).toString();
            fragment.setUrl(h5);
        } else {
            fragment.setH5(h5);
        }

        fragment.show(((LiveActivity) mContext).getSupportFragmentManager(), "H5DialogFragment");
    }

    /**
     * 显示主播等级
     */
    public void setAnchorLevel(int anchorLevel) {
        if (mLevelAnchor != null) {
            LevelBean levelBean = AppConfig.getInstance().getAnchorLevel(anchorLevel);
            if (levelBean != null) {
                ImgLoader.display(levelBean.getThumbIcon(), mLevelAnchor);
            }
        }
    }

    /**
     * 显示用户名
     */
    public void setName(String name) {
        if (mName != null) {
            mName.setText(name);
            mLiveName = name;
        }
    }

    public void setTgCode(String tgCode) {
        if (mInvite != null) {
            mInvite.setText(tgCode);
            mTgCode = tgCode;
        }
    }

    /**
     * 显示房间号
     */
    public void setRoomNum(String roomNum) {
        if (mID != null) {
            mID.setText(roomNum);
        }
    }

    /**
     * 显示是否关注
     */
    public void setAttention(int attention) {
        if (mBtnFollow != null) {
            if (attention == 0) {
                if (mBtnFollow.getVisibility() != View.VISIBLE) {
                    mBtnFollow.setVisibility(View.VISIBLE);
                }
            } else {
                if (mBtnFollow.getVisibility() == View.VISIBLE) {
                    mBtnFollow.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 显示刷新直播间用户列表
     */
    public void setUserList(List<LiveUserGiftBean> list) {
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.refreshList(list);
            refreshWatchNum(list.size());
        }
    }

    /**
     * 显示主播映票数
     */
    public void setVotes(String votes) {
        if (mVotes != null) {
            mVotes.setText(votes);
        }
    }

    /**
     * 显示主播守护人数
     */
    public void setGuardNum(int guardNum) {
        if (mGuardNum != null) {
            if (guardNum > 0) {
                mGuardNum.setText(guardNum + WordUtil.getString(R.string.ren));
            } else {
                mGuardNum.setText("0人");
            }
        }
    }

    public void setLiveInfo(String liveUid, String stream, int userListInterval, String liveId) {
        mLiveUid = liveUid;
        mStream = stream;
        mUserListInterval = userListInterval;
        this.mLiveId = liveId;
    }

    /**
     * 守护信息发生变化
     */
    public void onGuardInfoChanged(LiveBuyGuardMsgBean bean) {
        setGuardNum(bean.getGuardNum());
        setVotes(bean.getVotes());
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.onGuardChanged(bean.getUid(), bean.getGuardType());
        }
    }

    /**
     * 设置红包按钮是否可见
     */
    public void setRedPackBtnVisible(boolean visible) {
        if (mBtnRedPack != null) {
            if (visible) {
                if (mBtnRedPack.getVisibility() != View.VISIBLE) {
                    mBtnRedPack.setVisibility(View.VISIBLE);
                }
            } else {
                if (mBtnRedPack.getVisibility() == View.VISIBLE) {
                    mBtnRedPack.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.avatar:
                showAnchorUserDialog();
                break;
            case R.id.btn_follow:
                follow();
                break;
            case R.id.btn_votes:
                openContributeWindow();
                break;
            case R.id.btn_guard:
                ((LiveActivity) mContext).openGuardListWindow();
                break;
            case R.id.root:
                light();
                break;
            case R.id.btnRedPack:
                ((LiveActivity) mContext).openRedPackListWindow();
                break;
            case R.id.tvWishShow:
            case R.id.tvWishShowBig:
                openWishBillListWindow();
                break;
            case R.id.ivAddGood:
                openAddGoods();
                break;
            case R.id.audienceGoodsLayout:
                openLiveGoodList();
                break;
        }
    }

    private void openLiveGoodList() {
        LiveAudienceGoodsDialogFragment fragment = new LiveAudienceGoodsDialogFragment();
        fragment.setListener((view, object) -> {
            String url = (String) object;
            if (!StrUtil.isEmpty(url)) {
                WebViewActivity.forward3(mContext, url, 1);
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_ID, mLiveId);
        bundle.putString(Constants.LIVE_NAME, mLiveName);
        fragment.setArguments(bundle);
        fragment.show(((LiveActivity) mContext).getSupportFragmentManager(), "LiveAudienceGoodsDialogFragment");
    }

    private void openAddGoods() {
        LiveAnchorGoodsDialogFragment fragment = new LiveAnchorGoodsDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_ID, mLiveId);
        bundle.putString(Constants.LIVE_NAME, mLiveName);
        fragment.setArguments(bundle);
        fragment.show(((LiveActivity) mContext).getSupportFragmentManager(), "LiveAnchorGoodsDialogFragment");
    }


    /**
     * 打开心愿单窗口
     */
    private void openWishBillListWindow() {
        WishBillListDialogFragment fragment = new WishBillListDialogFragment();
        fragment.setOnWishBillSendItemClickListener(new OnWishBillSendItemClickListener() {
            @Override
            public void onAvatarClick(WishBillBean.SendUser bean) {
                showUserDialog(bean.getUid());
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_UID, mLiveUid);
        bundle.putString(Constants.LIVE_NAME, mLiveName);
        fragment.setArguments(bundle);
        fragment.show(((LiveActivity) mContext).getSupportFragmentManager(), "WishBillListDialogFragment");
    }

    /**
     * 关注主播
     */
    private void follow() {
        if (TextUtils.isEmpty(mLiveUid)) {
            return;
        }
        HttpUtil.setAttention(Constants.FOLLOW_FROM_LIVE, mLiveUid, new CommonCallback<Integer>() {
            @Override
            public void callback(Integer isAttention) {
                if (isAttention == 1) {
                    ((LiveActivity) mContext).sendSystemMessage(
                            AppConfig.getInstance().getUserBean().getUsername() + WordUtil.getString(R.string.live_follow_anchor));
                }
            }
        });
    }

    /**
     * 用户进入房间，用户列表添加该用户
     */
    public void insertUser(LiveUserGiftBean bean) {
//        if (mLiveUserAdapter != null) {
//            mLiveUserAdapter.insertItem(bean);
//            refreshWatchNum(mLiveUserAdapter.getItemCount());
//        }

        if (!TextUtils.isEmpty(mLiveUid) && mRefreshUserListCallback != null && mLiveUserAdapter != null) {
            HttpUtil.cancel(HttpConst.GET_USER_LIST);
            HttpUtil.getUserList(mLiveUid, mStream, mRefreshUserListCallback);
        }
    }

    /**
     * 用户进入房间，添加僵尸粉
     */
    public void insertUser(List<LiveUserGiftBean> list) {
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.insertList(list);
            refreshWatchNum(Integer.parseInt(tvWatchNum.getText().toString().trim()) + 1);
        }
    }

    /**
     * 用户离开房间，用户列表删除该用户
     */
    public void removeUser(String uid) {
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.removeItem(uid);
            refreshWatchNum(Integer.parseInt(tvWatchNum.getText().toString().trim()) - 1);
        }
    }

    /**
     * 刷新用户列表
     */
    private void refreshUserList() {
        if (!TextUtils.isEmpty(mLiveUid) && mRefreshUserListCallback != null && mLiveUserAdapter != null) {
            HttpUtil.cancel(HttpConst.GET_USER_LIST);
            HttpUtil.getUserList(mLiveUid, mStream, mRefreshUserListCallback);
            startRefreshUserList();
        }
    }

    /**
     * 开始刷新用户列表
     */
    public void startRefreshUserList() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_REFRESH_USER_LIST, mUserListInterval > 0 ? mUserListInterval : 60000);
        }
    }

    /**
     * 请求计时收费的扣费接口
     */
    private void requestTimeCharge() {
        if (!TextUtils.isEmpty(mLiveUid) && mTimeChargeCallback != null) {
            HttpUtil.cancel(HttpConst.TIME_CHARGE);
            HttpUtil.timeCharge(mLiveUid, mStream, mTimeChargeCallback);
            startRequestTimeCharge();
        }
    }

    /**
     * 开始请求计时收费的扣费接口
     */
    public void startRequestTimeCharge() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_TIME_CHARGE, 60000);
        }
    }


    /**
     * 添加聊天消息到聊天栏
     */
    public void insertChat(LiveChatBean bean) {
        if (mLiveChatAdapter != null) {
            mLiveChatAdapter.insertItem(bean);
        }
    }

    /**
     * 播放飘心动画
     */
    public void playLightAnim() {
        if (mLightAnimPresenter != null) {
            mLightAnimPresenter.play();
        }
    }

    /**
     * 点亮
     */
    private void light() {
        ((LiveAudienceActivity) mContext).light();
    }


    /**
     * 键盘高度变化
     */
    public void onKeyBoardChanged(int visibleHeight, int keyBoardHeight) {
        if (mRoot != null) {
            if (keyBoardHeight == 0) {
                mRoot.setTranslationY(0);
                return;
            }
            if (sOffsetY == 0) {
                mRoot.setTranslationY(-keyBoardHeight);
                return;
            }
            if (sOffsetY > 0 && sOffsetY < keyBoardHeight) {
                mRoot.setTranslationY(sOffsetY - keyBoardHeight);
            }
        }
    }

    /**
     * 聊天栏滚到最底部
     */
    public void chatScrollToBottom() {
        if (mLiveChatAdapter != null) {
            mLiveChatAdapter.scrollToBottom();
        }
    }

    /**
     * 用户进入房间 金光一闪,坐骑动画
     */
    public void onEnterRoom(LiveEnterRoomBean bean) {
        if (bean == null) {
            return;
        }
        if (mLiveEnterRoomAnimPresenter != null) {
            mLiveEnterRoomAnimPresenter.enterRoom(bean);
        }
    }

    /**
     * 显示弹幕
     */
    public void showDanmu(LiveDanMuBean bean) {
        if (mVotes != null) {
            mVotes.setText(bean.getVotes());
        }
        if (mLiveDanmuPresenter == null) {
            mLiveDanmuPresenter = new LiveDanmuPresenter(mContext, mParentView);
        }
        mLiveDanmuPresenter.showDanmu(bean);
    }

    /**
     * 显示主播的个人资料弹窗
     */
    private void showAnchorUserDialog() {
        if (TextUtils.isEmpty(mLiveUid)) {
            return;
        }
        showUserDialog(mLiveUid);
    }

    /**
     * 显示个人资料弹窗
     */
    private void showUserDialog(String toUid) {
        if (!TextUtils.isEmpty(mLiveUid) && !TextUtils.isEmpty(toUid)) {
            LiveUserDialogFragment fragment = new LiveUserDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.LIVE_UID, mLiveUid);
            bundle.putString(Constants.TO_UID, toUid);
            fragment.setArguments(bundle);
            fragment.show(((LiveActivity) mContext).getSupportFragmentManager(), "LiveUserDialogFragment");
        }
    }

    /**
     * 直播间贡献榜窗口
     */
    private void openContributeWindow() {
        ((LiveActivity) mContext).openContributeWindow();
    }


    /**
     * 显示礼物动画
     */
    public void showGiftMessage(LiveReceiveGiftBean bean) {
        mVotes.setText(bean.getVotes());
        if (mLiveGiftAnimPresenter == null) {
            mLiveGiftAnimPresenter = new LiveGiftAnimPresenter2(mContext, mContentView, mGifImageView, mSVGAImageView);
        }
        mLiveGiftAnimPresenter.showGiftAnim(bean);
    }

    /**
     * 增加主播映票数
     *
     * @param deltaVal 增加的映票数量
     */
    public void updateVotes(String deltaVal) {
        if (mVotes == null) {
            return;
        }
        String votesVal = mVotes.getText().toString().trim();
        if (TextUtils.isEmpty(votesVal)) {
            return;
        }
        try {
            double votes = Double.parseDouble(votesVal);
            double addVotes = Double.parseDouble(deltaVal);
            votes += addVotes;
            mVotes.setText(StringUtil.format(votes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ViewGroup getInnerContainer() {
        return (ViewGroup) findViewById(R.id.inner_container);
    }


    /**
     * 主播显示直播时间
     */
    private void showAnchorLiveTime() {
        if (mLiveTimeTextView != null) {
            mAnchorLiveTime += 1000;
            mLiveTimeTextView.setText(StringUtil.getDurationText(mAnchorLiveTime));
            startAnchorLiveTime();
        }


    }

    public void startAnchorLiveTime() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_ANCHOR_LIVE_TIME, 1000);
        }
    }

    /**
     * 主播切后台，50秒后关闭直播
     */
    public void anchorPause() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_ANCHOR_PAUSE, 50000);
        }
    }

    /**
     * 主播切后台后又回到前台
     */
    public void onResume() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.removeMessages(LiveRoomHandler.WHAT_ANCHOR_PAUSE);
        }


    }


    public void onStop() {

    }

    /**
     * 主播结束直播
     */
    private void anchorEndLive() {
        if (mContext instanceof LiveAnchorActivity) {
            ((LiveAnchorActivity) mContext).endLive();
        }
    }

    public void release() {
        HttpUtil.cancel(HttpConst.GET_USER_LIST);
        HttpUtil.cancel(HttpConst.TIME_CHARGE);
        HttpUtil.cancel(HttpConst.SET_ATTENTION);
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.release();
        }
        mLiveRoomHandler = null;
        if (mLightAnimPresenter != null) {
            mLightAnimPresenter.release();
        }
        if (mLiveEnterRoomAnimPresenter != null) {
            mLiveEnterRoomAnimPresenter.release();
        }
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.release();
        }
        mRefreshUserListCallback = null;
        mTimeChargeCallback = null;
    }

    public void clearData() {
        HttpUtil.cancel(HttpConst.GET_USER_LIST);
        HttpUtil.cancel(HttpConst.TIME_CHARGE);
        HttpUtil.cancel(HttpConst.SET_ATTENTION);
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.removeCallbacksAndMessages(null);
        }
        if (mAvatar != null) {
            mAvatar.setImageDrawable(null);
        }
        if (mLevelAnchor != null) {
            mLevelAnchor.setImageDrawable(null);
        }
        if (mName != null) {
            mName.setText("");
        }
        if (mID != null) {
            mID.setText("");
        }
        if (mVotes != null) {
            mVotes.setText("");
        }
        if (mGuardNum != null) {
            mGuardNum.setText("");
        }
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.clear();
            refreshWatchNum(0);
        }
        if (mLiveChatAdapter != null) {
            mLiveChatAdapter.clear();
        }
        if (mLiveEnterRoomAnimPresenter != null) {
            mLiveEnterRoomAnimPresenter.cancelAnim();
            mLiveEnterRoomAnimPresenter.resetAnimView();
        }
        if (mLiveDanmuPresenter != null) {
            mLiveDanmuPresenter.release();
            mLiveDanmuPresenter.reset();
        }
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.cancelAllAnim();
        }
    }

    public void showAudience() {
        if (null != ivMyGood) {
            ivMyGood.setVisibility(View.GONE);
        }
    }

    public void showAnchor() {
        if (null != ivMyGood) {
            ivMyGood.setVisibility(View.VISIBLE);
        }
    }


    private static class LiveRoomHandler extends Handler {

        private LiveRoomViewHolder mLiveRoomViewHolder;
        private static final int WHAT_REFRESH_USER_LIST = 1;
        private static final int WHAT_TIME_CHARGE = 2;//计时收费房间定时请求接口扣费
        private static final int WHAT_ANCHOR_LIVE_TIME = 3;//直播间主播计时
        private static final int WHAT_ANCHOR_PAUSE = 4;//主播切后台
        private static final int WHAT_WISH_UPDATE = 5;//心愿单刷新
        private static final int WHAT_GOODS_UPDATE = 6;//心愿单刷新

        public LiveRoomHandler(LiveRoomViewHolder liveRoomViewHolder) {
            mLiveRoomViewHolder = new WeakReference<>(liveRoomViewHolder).get();
        }

        @Override
        public void handleMessage(Message msg) {
            if (mLiveRoomViewHolder != null) {
                switch (msg.what) {
                    case WHAT_REFRESH_USER_LIST:
                        mLiveRoomViewHolder.refreshUserList();
                        break;
                    case WHAT_TIME_CHARGE:
                        mLiveRoomViewHolder.requestTimeCharge();
                        break;
                    case WHAT_ANCHOR_LIVE_TIME:
                        mLiveRoomViewHolder.showAnchorLiveTime();
                        break;
                    case WHAT_ANCHOR_PAUSE:
                        mLiveRoomViewHolder.anchorEndLive();
                        break;
                    case WHAT_WISH_UPDATE:
                        mLiveRoomViewHolder.updateWishList();
                        break;
                    case WHAT_GOODS_UPDATE:
                        mLiveRoomViewHolder.startDetectGoods();
                        break;
                }
            }
        }

        public void release() {
            removeCallbacksAndMessages(null);
            mLiveRoomViewHolder = null;
        }
    }

    public void refreshWatchNum(int watchNum) {
        if (watchNum < 0) {
            tvWatchNum.setText("0");
        } else {
            if (watchNum > 10000) {
                String s = new BigDecimal((long) watchNum).setScale(2, RoundingMode.HALF_UP).divide(new BigDecimal(10000)).toString();
                tvWatchNum.setText(s);
            } else {
                tvWatchNum.setText(watchNum + "");
            }
        }

    }

    public void setOnLiveFinishByBackstage(OnLiveFinishByBackstage onLiveFinishByBackstage) {
        this.onLiveFinishByBackstage = onLiveFinishByBackstage;
    }

    /**
     * 直播被后台关闭
     */
    public interface OnLiveFinishByBackstage {
        void onFinish();
    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {

        private TextView txView;

        MyHandler(TextView redPackTime) {
            txView = new WeakReference<>(redPackTime).get();
        }

        @Override
        public void handleMessage(Message msg) {
            updateTime(txView, msg.what);
        }


        void release() {
            txView = null;
        }
    }

    public void onNewRedPack() {
        if (redPackTime.getVisibility() != View.VISIBLE) {
            showRedPackTime();
        }
    }

    private final static String TAG = "LiveRoomViewHolder";

    public void startDetectGoods() {
        HttpUtil.getLiveNewGoods(mLiveId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                try {
                    if (code == 0) {
                        List<NetGoodsBean> netBean = JSON.parseArray(Arrays.toString(info), NetGoodsBean.class);
                        if (netBean.size() > 0) {
                            audienceGoodsLayout.setVisibility(View.VISIBLE);
                            Glide.with(mContext).load(netBean.get(0).getThumb()).into(audienceGoods);
                        }
                    }
                    if (mLiveRoomHandler != null) {
                        //每10秒探测一次是否存在带货商品
                        mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_GOODS_UPDATE, 10000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
