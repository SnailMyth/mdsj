package com.wwsl.mdsj.game.views;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wwsl.game.custom.GameBetCoinView;
import com.wwsl.game.custom.PokerView;
import com.wwsl.game.util.GameIconUtil;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.live.LiveActivity;
import com.wwsl.mdsj.event.GameWindowEvent;
import com.wwsl.mdsj.game.GameSoundPool;
import com.wwsl.mdsj.game.bean.BankerBean;
import com.wwsl.mdsj.game.bean.GameParam;
import com.wwsl.mdsj.game.dialog.GameNzLsDialogFragment;
import com.wwsl.mdsj.game.dialog.GameNzSfDialogFragment;
import com.wwsl.mdsj.game.dialog.GameNzSzDialogFragment;
import com.wwsl.mdsj.game.socket.SocketGameUtil;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.utils.L;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2018/10/31.
 * 开心牛仔 游戏
 */

public class GameNzViewHolder extends AbsGameViewHolder {

    private static final int WHAT_READY_END = 101;//准备倒计时结束
    private static final int WHAT_CARD_ANIM_START = 102;//角色缩小，播放发牌动画
    private static final int WHAT_BET_ANIM_DISMISS = 103;//开始下注横条消失
    private static final int WHAT_BET_COUNT_DOWN = 104;//下注倒计时
    private static final int WHAT_GAME_RESULT = 105;//揭晓游戏结果
    private static final int WHAT_GAME_NEXT = 106;//开始下次游戏
    private static final int WHAT_HIDE_RESULT = 107;//隐藏显示结果的弹窗
    private static final int MAX_REPEAT_COUNT = 6;
    private TextView mTip;//提示的横条
    private TextView mReadyCountDown;//准备开始倒计时的TextView
    private View mRoleGroup;
    private int mRepeatCount;
    private Animation mResultAnim;
    private int mSceneHeight;//场景高度
    private int mRoleHeight;//角色高度
    private View mPokerGroup;
    private PokerView[] mPokerViews;
    private View[] mRoles;
    private View[] mRoleNames;
    private TextView mBetCountDown;//下注倒计时的TextView
    private TextView mCoinTextView;//显示用户余额的TextView
    private GameBetCoinView[] mBetCoinViews;
    private ImageView[] mResults;
    private Animation mReadyAnim;//准备开始倒计时的动画
    private Animation mTipHideAnim;//提示横条隐藏的动画
    private Animation mTipShowAnim;//提示横条显示的动画
    private Animation mRoleIdleAnim; //角色待机动画
    private ValueAnimator mRoleScaleAnim;//角色缩小的动画
    private Handler mHandler;
    private int mBetCount;
    private BankerBean mBankerBean;//庄家信息
    private String mBankerLimitString;
    private ViewGroup mInnerContainer;
    private View mBankerView;
    private ImageView mBankerAvatar;//庄家头像
    private TextView mBankerName;//庄家名字
    private TextView mBankerCoin;//庄家余额
    private PokerView mBankerPokerView;//庄家的牌
    private ImageView mBankerResult;//庄家的结果
    private String mGxString;
    private String mSzString;
    private GameNzResultViewHolder mGameNzResultViewHolder;
    private GameNzSzDialogFragment mGameNzSzDialogFragment;//上庄列表
    private GameNzLsDialogFragment mGameNzLsDialogFragment;//庄家流水
    private GameNzSfDialogFragment mGameNzSfDialogFragment;//胜负记录


    public GameNzViewHolder(GameParam param, GameSoundPool gameSoundPool, BankerBean bankerBean, String bankerLimitString) {
        super(param, gameSoundPool);
        mBankerLimitString = bankerLimitString;
        mBankerBean = bankerBean;
        boolean anchor = param.isAnchor();
        mGameViewHeight = anchor ? DpUtil.dp2px(150) : DpUtil.dp2px(190);
        mInnerContainer = param.getInnerContainer();
        View bankerView = LayoutInflater.from(mContext).inflate(R.layout.game_view_nz_sz, mInnerContainer, false);
        mBankerView = bankerView;
        mBankerAvatar = (ImageView) bankerView.findViewById(R.id.avatar);
        mBankerName = (TextView) bankerView.findViewById(R.id.name);
        mBankerCoin = (TextView) bankerView.findViewById(R.id.coin);
        mBankerPokerView = (PokerView) bankerView.findViewById(R.id.pokerView);
        mBankerResult = (ImageView) bankerView.findViewById(R.id.result);
        bankerView.findViewById(R.id.group_water).setOnClickListener(this);
        bankerView.findViewById(R.id.btn_sz).setOnClickListener(this);
        if (!anchor) {
            bankerView.setTranslationY(-DpUtil.dp2px(23));
            ViewStub viewStub = (ViewStub) findViewById(R.id.view_stub);
            View view = viewStub.inflate();
            view.findViewById(R.id.btn_bet_shi).setOnClickListener(this);
            view.findViewById(R.id.btn_bet_bai).setOnClickListener(this);
            view.findViewById(R.id.btn_bet_qian).setOnClickListener(this);
            view.findViewById(R.id.btn_bet_wan).setOnClickListener(this);
            view.findViewById(R.id.btn_record).setOnClickListener(this);
            mCoinTextView = (TextView) view.findViewById(R.id.coin);
            mCoinTextView.setOnClickListener(this);
            for (View v : mBetCoinViews) {
                v.setOnClickListener(this);
            }
            mBetMoney = 1;
            HttpUtil.getCoin(new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        setLastCoin(JSONObject.parseObject(info[0]).getString("coin"));
                    }
                }
            });
        }
        mGxString = WordUtil.getString(R.string.game_nz_gx);
        mSzString = WordUtil.getString(R.string.game_nz_sz);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.game_view_nz;
    }


    @Override
    public void init() {
        mTip = (TextView) findViewById(R.id.tip);
        mReadyCountDown = (TextView) findViewById(R.id.count_down_1);
        mRoleGroup = findViewById(R.id.role_group);
        mSceneHeight = DpUtil.dp2px(150);
        mRoleHeight = DpUtil.dp2px(90);
        mPokerGroup = findViewById(R.id.pokers_group);
        mPokerViews = new PokerView[3];
        mPokerViews[0] = (PokerView) findViewById(R.id.pokers_1);
        mPokerViews[1] = (PokerView) findViewById(R.id.pokers_2);
        mPokerViews[2] = (PokerView) findViewById(R.id.pokers_3);
        mRoles = new View[3];
        mRoles[0] = findViewById(R.id.role_1);
        mRoles[1] = findViewById(R.id.role_2);
        mRoles[2] = findViewById(R.id.role_3);
        mRoleNames = new View[3];
        mRoleNames[0] = findViewById(R.id.name_1);
        mRoleNames[1] = findViewById(R.id.name_2);
        mRoleNames[2] = findViewById(R.id.name_3);
        mBetCountDown = (TextView) findViewById(R.id.count_down_2);
        mBetCoinViews = new GameBetCoinView[3];
        mBetCoinViews[0] = (GameBetCoinView) findViewById(R.id.score_1);
        mBetCoinViews[1] = (GameBetCoinView) findViewById(R.id.score_2);
        mBetCoinViews[2] = (GameBetCoinView) findViewById(R.id.score_3);
        mResults = new ImageView[3];
        mResults[0] = (ImageView) findViewById(R.id.result_1);
        mResults[1] = (ImageView) findViewById(R.id.result_2);
        mResults[2] = (ImageView) findViewById(R.id.result_3);
        //角色缩小的动画
        mRoleScaleAnim = ValueAnimator.ofFloat(mSceneHeight, mRoleHeight);
        mRoleScaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                changeRoleHeight((int) v);
            }
        });
        mRoleScaleAnim.setDuration(1000);
        mResultAnim = new ScaleAnimation(0.2f, 1, 0.2f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mResultAnim.setDuration(300);

        mRoleIdleAnim = new ScaleAnimation(1f, 1.04f, 1f, 1.04f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1f);
        mRoleIdleAnim.setRepeatCount(-1);
        mRoleIdleAnim.setRepeatMode(Animation.REVERSE);
        mRoleIdleAnim.setDuration(800);

        mReadyAnim = new ScaleAnimation(4, 1, 4, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mReadyAnim.setDuration(1000);
        mReadyAnim.setRepeatCount(MAX_REPEAT_COUNT);
        mReadyAnim.setRepeatMode(Animation.RESTART);
        mReadyAnim.setInterpolator(new AccelerateInterpolator());
        mReadyAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mReadyCountDown != null && mReadyCountDown.getVisibility() == View.VISIBLE) {
                    mReadyCountDown.setVisibility(View.INVISIBLE);//隐藏准备倒计时
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                mReadyCountDown.setText(String.valueOf(mRepeatCount));
                mRepeatCount--;
            }
        });

        mTipShowAnim = new ScaleAnimation(0.2f, 1, 0.2f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mTipShowAnim.setDuration(500);
        mTipHideAnim = new ScaleAnimation(1, 0.2f, 1, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mTipHideAnim.setDuration(500);
        mTipHideAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mTip != null && mTip.getVisibility() == View.VISIBLE) {
                    mTip.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WHAT_READY_END://准备倒计时结束
                        anchorCreateGame();
                        break;
                    case WHAT_CARD_ANIM_START://角色缩小，播放发牌动画
                        playCardAnim();
                        break;
                    case WHAT_BET_ANIM_DISMISS:
                        if (mTip != null) {
                            mTip.startAnimation(mTipHideAnim);
                        }
                        break;
                    case WHAT_BET_COUNT_DOWN://下注倒计时
                        betCountDown();
                        break;
                    case WHAT_GAME_RESULT://揭晓游戏结果
                        showGameResult(msg.arg1, (String[]) msg.obj);
                        break;
                    case WHAT_GAME_NEXT:
                        nextGame();
                        break;
                    case WHAT_HIDE_RESULT:
                        hideResultDialog();
                        break;
                }
            }
        };
    }

    /**
     * 改变角色的高度
     */
    private void changeRoleHeight(int height) {
        ViewGroup.LayoutParams params = mRoleGroup.getLayoutParams();
        params.height = height;
        mRoleGroup.setLayoutParams(params);
    }

    /**
     * 显示观众的余额
     */
    @Override
    public void setLastCoin(String coin) {
        if (mCoinTextView != null) {
            mCoinTextView.setText(coin + " " + mChargeString);
        }
    }

    @Override
    public void addToParent() {
        super.addToParent();
        if (mInnerContainer != null && mBankerView != null) {
            ViewParent parent = mBankerView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mBankerView);
            }
            mInnerContainer.addView(mBankerView);
            showBankerInfo();
            //ToastUtil.show(mGxString + mBankerBean.getBankerName() + mSzString);
        }
    }

    /**
     * 显示庄家信息
     */
    public void showBankerInfo() {
        if (mBankerBean == null) {
            return;
        }
        if (mBankerAvatar != null) {
            if ("0".equals(mBankerBean.getBankerId()) || TextUtils.isEmpty(mBankerBean.getBankerAvatar())) {
                mBankerAvatar.setImageResource(R.mipmap.icon_nz_sz_default_head);
            } else {
                ImgLoader.displayAvatar(mBankerBean.getBankerAvatar(), mBankerAvatar);
            }
        }
        if (mBankerName != null) {
            mBankerName.setText(mBankerBean.getBankerName());
        }
        if (mBankerCoin != null) {
            mBankerCoin.setText(mBankerBean.getBankerCoin());
        }
    }


    @Override
    public void removeFromParent() {
        super.removeFromParent();
        if (mBankerView != null) {
            ViewParent parent = mBankerView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mBankerView);
            }
        }
        if (mGameNzResultViewHolder != null) {
            mGameNzResultViewHolder.removeFromParent();
        }
    }


    /**
     * 处理socket回调的数据
     */
    public void handleSocket(int action, JSONObject obj) {
        if (mEnd) {
            return;
        }
        L.e(mTag, "-----handleSocket--------->" + obj.toJSONString());
        switch (action) {
            case SocketGameUtil.GAME_ACTION_OPEN_WINDOW://打开游戏窗口
                onGameWindowShow();
                break;
            case SocketGameUtil.GAME_ACTION_CREATE://游戏被创建
                onGameCreate(obj);
                break;
            case SocketGameUtil.GAME_ACTION_CLOSE://主播关闭游戏
                onGameClose();
                break;
            case SocketGameUtil.GAME_ACTION_NOTIFY_BET://开始下注
                onGameBetStart(obj);
                break;
            case SocketGameUtil.GAME_ACTION_BROADCAST_BET://收到下注消息
                onGameBetChanged(obj);
                break;
            case SocketGameUtil.GAME_ACTION_RESULT://收到游戏结果揭晓的的消息
                onGameResult(obj);
                break;
        }
    }


    /**
     * 所有人收到 打开游戏窗口的socket后，   打开游戏窗口，启动角色待机动画，进入8秒准备倒计时，
     */
    private void onGameWindowShow() {
        if (!mShowed) {
            showGameWindow();
            mBetStarted = false;
            mRepeatCount = MAX_REPEAT_COUNT;
            mReadyCountDown.setText(String.valueOf(mRepeatCount + 1));
            mReadyCountDown.startAnimation(mReadyAnim);
            if (mAnchor && mHandler != null) {
                mHandler.sendEmptyMessageDelayed(WHAT_READY_END, 7000);
            }
            if (mRoles != null) {
                for (View v : mRoles) {
                    if (v != null) {
                        v.startAnimation(mRoleIdleAnim);
                    }
                }
            }
        }
    }


    /**
     * 主播在8秒准备时间结束后，请求接口，创建游戏
     */
    @Override
    public void anchorCreateGame() {
        if (!mAnchor) {
            return;
        }
        HttpUtil.gameNiuzaiCreate(mStream, mBankerBean.getBankerId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    mGameID = obj.getString("gameid");
                    mGameToken = obj.getString("token");
                    mBetTime = obj.getIntValue("time");
                    SocketGameUtil.nzAnchorCreateGame(mSocketClient, mGameID, obj.getString("bankerlist"));
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }


    /**
     * 所有人收到游戏被创建的socket后，开始执行发牌动画
     */
    private void onGameCreate(JSONObject obj) {
        if (!mShowed) {
            showGameWindow();
            if (mTip != null && mTip.getVisibility() == View.VISIBLE) {
                mTip.setVisibility(View.INVISIBLE);
            }
            if (mRoles != null) {
                for (View v : mRoles) {
                    if (v != null) {
                        v.startAnimation(mRoleIdleAnim);
                    }
                }
            }
        }
        if (mRoleNames != null) {
            for (View name : mRoleNames) {//隐藏角色名字
                if (name != null && name.getVisibility() == View.VISIBLE) {
                    name.setVisibility(View.INVISIBLE);
                }
            }
        }
        if (mTip != null && mTipHideAnim != null && mTip.getVisibility() == View.VISIBLE) {
            mTip.startAnimation(mTipHideAnim);//横条消失
        }
        if (mRoleScaleAnim != null) {
            mRoleScaleAnim.start();//执行角色缩小动画
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(WHAT_CARD_ANIM_START, 1000);
        }
        BankerBean bankerBean = JSON.parseObject(obj.getString("bankerlist"), BankerBean.class);
        if (mBankerBean != null) {
            String bankerId = bankerBean.getBankerId();
            if (bankerId != null && !bankerId.equals(mBankerBean.getBankerId())) {
                ToastUtil.show(mGxString + bankerBean.getBankerName() + mSzString);
            }
        }
        mBankerBean = bankerBean;
        showBankerInfo();
    }


    /**
     * 角色缩小后，播放发牌动画，主播通知所有人下注
     */
    private void playCardAnim() {
        //角色靠右
        if (mBetCoinViews != null) {
            for (View v : mBetCoinViews) {
                if (v != null && v.getVisibility() == View.GONE) {
                    v.setVisibility(View.INVISIBLE);
                }
            }
        }
        //显示摆放扑克牌的外框,开始执行发牌动画
        if (mPokerGroup != null && mPokerGroup.getVisibility() != View.VISIBLE) {
            mPokerGroup.setVisibility(View.VISIBLE);
        }
        if (mPokerViews != null) {
            for (PokerView pv : mPokerViews) {
                if (pv != null) {
                    pv.sendCard();
                }
            }
        }
        if (mBankerPokerView != null) {
            mBankerPokerView.sendCard();
        }
        //主播通知所有人下注
        if (mAnchor) {
            SocketGameUtil.nzAnchorNotifyGameBet(mSocketClient, mLiveUid, mGameID, mGameToken, mBetTime);
        }
    }

    /**
     * 收到主播通知下注的socket,播放动画，开始下注倒计时
     */
    private void onGameBetStart(JSONObject obj) {
        mBetStarted = true;
        if (!mAnchor) {
            mGameID = obj.getString("gameid");
            mGameToken = obj.getString("token");
            mBetTime = obj.getIntValue("time");
        }
        mBetCount = mBetTime - 1;
        if (mBetCountDown != null) {
            if (mBetCountDown.getVisibility() != View.VISIBLE) {
                mBetCountDown.setVisibility(View.VISIBLE);
            }
            mBetCountDown.setText(String.valueOf(mBetCount));
        }
        if (mTip != null) {
            if (mTip.getVisibility() != View.VISIBLE) {
                mTip.setVisibility(View.VISIBLE);
            }
            mTip.setText(R.string.game_start_support);
            mTip.startAnimation(mTipShowAnim);
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(WHAT_BET_COUNT_DOWN, 1000);
            mHandler.sendEmptyMessageDelayed(WHAT_BET_ANIM_DISMISS, 1500);
        }
        //显示下注牌
        if (mBetCoinViews != null) {
            for (View v : mBetCoinViews) {
                if (v != null && v.getVisibility() != View.VISIBLE) {
                    v.setVisibility(View.VISIBLE);
                }
            }
        }
        playGameSound(GameSoundPool.GAME_SOUND_BET_START);
    }

    /**
     * 下注倒计时
     */
    private void betCountDown() {
        mBetCount--;
        if (mBetCount > 0) {
            mBetCountDown.setText(String.valueOf(mBetCount));
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(WHAT_BET_COUNT_DOWN, 1000);
            }
        } else {
            mBetCountDown.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * 观众下注
     */
    private void audienceBetGame(final int index) {
        HttpUtil.gameNiuzaiBet(mGameID, mBetMoney, index, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    setLastCoin(JSON.parseObject(info[0]).getString("coin"));
                    SocketGameUtil.nzAudienceBetGame(mSocketClient, mBetMoney, index);
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    /**
     * 所有人收到下注的观众socket，更新下注金额
     */
    private void onGameBetChanged(JSONObject obj) {
        String uid = obj.getString("uid");
        int money = obj.getIntValue("money");
        int index = obj.getIntValue("type") - 1;
        boolean isSelf = uid.equals(AppConfig.getInstance().getUid());
        if (isSelf) {//自己下的注
            playGameSound(GameSoundPool.GAME_SOUND_BET_SUCCESS);
        }
        if (mBetCoinViews != null) {
            if (index >= 0 && index < 3) {
                if (mBetCoinViews[index] != null) {
                    mBetCoinViews[index].updateBetVal(money, isSelf);
                }
            }
        }
    }

    /**
     * 收到游戏结果揭晓的的消息
     */
    private void onGameResult(JSONObject obj) {
        if (mTip != null) {
            if (mTip.getVisibility() != View.VISIBLE) {
                mTip.setVisibility(View.VISIBLE);
            }
            mTip.setText(R.string.game_show_result);//揭晓结果
            mTip.startAnimation(mTipShowAnim);
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(WHAT_BET_ANIM_DISMISS, 1500);
            }
        }
        String[][] result = JSON.parseObject(obj.getString("ct"), String[][].class);
        for (int i = 0; i < 3; i++) {
            Message msg = Message.obtain();
            msg.what = WHAT_GAME_RESULT;
            msg.arg1 = i;
            msg.obj = result[i];
            if (mHandler != null) {
                mHandler.sendMessageDelayed(msg, i * 2000);
            }
        }
        //显示庄家的结果
        if (mBankerPokerView != null) {
            String[] bankerResult = result[3];
            mBankerPokerView.showResult(
                    GameIconUtil.getPoker(bankerResult[0]),
                    GameIconUtil.getPoker(bankerResult[1]),
                    GameIconUtil.getPoker(bankerResult[2]),
                    GameIconUtil.getPoker(bankerResult[3]),
                    GameIconUtil.getPoker(bankerResult[4]));
            if (mBankerResult != null) {
                if (mBankerResult.getVisibility() != View.VISIBLE) {
                    mBankerResult.setVisibility(View.VISIBLE);
                }
                mBankerResult.setImageResource(GameIconUtil.getNiuZaiResult(bankerResult[7] + bankerResult[5]));
                mBankerResult.startAnimation(mResultAnim);
            }
        }
    }

    /**
     * 揭晓游戏结果
     */
    private void showGameResult(int i, String[] result) {
        if (mPokerViews[i] != null) {
            mPokerViews[i].showResult(
                    GameIconUtil.getPoker(result[0]),
                    GameIconUtil.getPoker(result[1]),
                    GameIconUtil.getPoker(result[2]),
                    GameIconUtil.getPoker(result[3]),
                    GameIconUtil.getPoker(result[4]));
        }
        if (mResults[i] != null) {
            mResults[i].setVisibility(View.VISIBLE);
            mResults[i].setImageResource(GameIconUtil.getNiuZaiResult(result[7] + result[5]));
            mResults[i].startAnimation(mResultAnim);
        }
        if (i == 2) {
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(WHAT_GAME_NEXT, 7000);//7秒后重新开始游戏
            }
            getGameResult();
        }
        playGameSound(GameSoundPool.GAME_SOUND_RESULT);
    }


    @Override
    protected void getGameResult() {
        HttpUtil.gameSettle(mGameID, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    setLastCoin(obj.getString("coin"));
                    if (mGameNzResultViewHolder == null) {
                        mGameNzResultViewHolder = new GameNzResultViewHolder(mContext, mParentView);
                        mGameNzResultViewHolder.addToParent();
                    }
                    mGameNzResultViewHolder.setData(obj.getString("gamecoin"), obj.getString("banker_profit"), mCoinName);
                    mGameNzResultViewHolder.show();
                    if (obj.getIntValue("isshow") == 1) { //只有自己是当前庄家的时候才会收到是1，1表示自己余额不够自动下庄了
                        ToastUtil.show(R.string.game_nz_sz_xz);
                    }
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(WHAT_HIDE_RESULT, 4000);
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    /**
     * 隐藏游戏输赢结果的弹窗
     */
    private void hideResultDialog() {
        if (mGameNzResultViewHolder != null) {
            mGameNzResultViewHolder.hide();
        }
    }

    /**
     * 游戏中途进入直播间的打开游戏窗口
     */
    @Override
    public void enterRoomOpenGameWindow() {
        if (!mShowed) {
            showGameWindow();
            //隐藏角色名字
            if (mRoleNames != null) {
                for (View name : mRoleNames) {
                    if (name != null && name.getVisibility() == View.VISIBLE) {
                        name.setVisibility(View.INVISIBLE);
                    }
                }
            }
            changeRoleHeight(mRoleHeight);
            mBetCount = mBetTime - 1;
            if (mBetCount > 0 && mBetCountDown != null) {
                if (mBetCountDown.getVisibility() != View.VISIBLE) {
                    mBetCountDown.setVisibility(View.VISIBLE);
                }
                mBetCountDown.setText(String.valueOf(mBetCount));
            }
            //显示下注牌
            if (mBetCoinViews != null) {
                for (int i = 0, length = mBetCoinViews.length; i < length; i++) {
                    GameBetCoinView gameBetCoinView = mBetCoinViews[i];
                    if (gameBetCoinView != null && gameBetCoinView.getVisibility() != View.VISIBLE) {
                        gameBetCoinView.setVisibility(View.VISIBLE);
                        gameBetCoinView.setBetVal(mTotalBet[i], mMyBet[i]);
                    }
                }
            }
            //显示摆放扑克牌的外框,开始执行发牌动画
            if (mPokerGroup != null && mPokerGroup.getVisibility() != View.VISIBLE) {
                mPokerGroup.setVisibility(View.VISIBLE);
            }
            if (mPokerViews != null) {
                for (PokerView pv : mPokerViews) {
                    if (pv != null) {
                        pv.sendCard();
                    }
                }
            }
            if (mBankerPokerView != null) {
                mBankerPokerView.sendCard();
            }
            //启动角色待机动画
            if (mRoles != null) {
                for (View v : mRoles) {
                    if (v != null) {
                        v.startAnimation(mRoleIdleAnim);
                    }
                }
            }
            if (mTip != null) {
                if (mTip.getVisibility() != View.VISIBLE) {
                    mTip.setVisibility(View.VISIBLE);
                }
                mTip.setText(R.string.game_start_support);
                mTip.startAnimation(mTipShowAnim);
            }
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(WHAT_BET_COUNT_DOWN, 1000);
                mHandler.sendEmptyMessageDelayed(WHAT_BET_ANIM_DISMISS, 1500);
            }
            playGameSound(GameSoundPool.GAME_SOUND_BET_START);
        }
    }


    /**
     * 开始下次游戏
     */
    @Override
    protected void nextGame() {
        mBetStarted = false;
        mRepeatCount = MAX_REPEAT_COUNT;
        if (mResults != null) {
            for (ImageView imageView : mResults) {
                if (imageView != null && imageView.getVisibility() == View.VISIBLE) {
                    imageView.setVisibility(View.INVISIBLE);
                }
            }
        }
        if (mBetCoinViews != null) {
            for (GameBetCoinView coinView : mBetCoinViews) {
                if (coinView != null) {
                    coinView.reset();
                    coinView.setVisibility(View.GONE);
                }
            }
        }
        if (mRoleNames != null) {
            for (View name : mRoleNames) {
                if (name != null && name.getVisibility() != View.VISIBLE) {
                    name.setVisibility(View.VISIBLE);
                }
            }
        }
        changeRoleHeight(mSceneHeight);
        if (mPokerGroup != null && mPokerGroup.getVisibility() == View.VISIBLE) {
            mPokerGroup.setVisibility(View.INVISIBLE);
        }
        if (mBankerPokerView != null) {
            mBankerPokerView.clearCard();
        }
        if (mBankerResult != null && mBankerResult.getVisibility() == View.VISIBLE) {
            mBankerResult.setVisibility(View.INVISIBLE);
        }
        if (mTip != null) {
            if (mTip.getVisibility() != View.VISIBLE) {
                mTip.setVisibility(View.VISIBLE);
            }
            mTip.setText(R.string.game_wait_start);
        }
        if (mReadyCountDown != null) {
            if (mReadyCountDown.getVisibility() != View.VISIBLE) {
                mReadyCountDown.setVisibility(View.VISIBLE);
            }
            mReadyCountDown.setText(String.valueOf(mRepeatCount + 1));
            mReadyCountDown.startAnimation(mReadyAnim);
        }
        if (mAnchor && mHandler != null) {
            mHandler.sendEmptyMessageDelayed(WHAT_READY_END, 7000);
        }
    }

    @Override
    public void anchorCloseGame() {
        if (mBetStarted) {
            ToastUtil.show(R.string.game_wait_end);
            return;
        }
        SocketGameUtil.nzAnchorCloseGame(mSocketClient);
        EventBus.getDefault().post(new GameWindowEvent(false));
    }


    /**
     * 主播关闭游戏的回调
     */
    private void onGameClose() {
        L.e(mTag, "---------onGameClose----------->");
        hideGameWindow();
        release();
    }

    @Override
    public void release() {
        mEnd = true;
        HttpUtil.cancel(HttpConst.GET_COIN);
        HttpUtil.cancel(HttpConst.GAME_NIUZAI_CREATE);
        HttpUtil.cancel(HttpConst.GAME_NIUZAI_BET);
        HttpUtil.cancel(HttpConst.GAME_SETTLE);
        super.release();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        if (mRoleScaleAnim != null) {
            mRoleScaleAnim.cancel();
        }
        if (mReadyCountDown != null) {
            mReadyCountDown.clearAnimation();
        }
        if (mTip != null) {
            mTip.clearAnimation();
        }
        if (mRoles != null) {
            for (View v : mRoles) {
                if (v != null) {
                    v.clearAnimation();
                }
            }
        }
        if (mResults != null) {
            for (View v : mResults) {
                if (v != null) {
                    v.clearAnimation();
                }
            }
        }
        if (mBankerResult != null) {
            mBankerResult.clearAnimation();
        }
        if (mPokerViews != null) {
            for (PokerView pokerView : mPokerViews) {
                if (pokerView != null) {
                    pokerView.recycleBitmap();
                }
            }
        }
        if (mBankerPokerView != null) {
            mBankerPokerView.recycleBitmap();
        }
        if (mGameNzResultViewHolder != null) {
            mGameNzResultViewHolder.release();
        }
        if (mGameNzSzDialogFragment != null) {
            mGameNzSzDialogFragment.dismiss();
        }
        if (mGameNzLsDialogFragment != null) {
            mGameNzLsDialogFragment.dismissAllowingStateLoss();
        }
        if (mGameNzSfDialogFragment != null) {
            mGameNzSfDialogFragment.dismissAllowingStateLoss();
        }
        L.e(mTag, "---------release----------->");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.score_1:
                audienceBetGame(1);
                break;
            case R.id.score_2:
                audienceBetGame(2);
                break;
            case R.id.score_3:
                audienceBetGame(3);
                break;
            case R.id.btn_bet_shi:
                mBetMoney = 1;
                playGameSound(GameSoundPool.GAME_SOUND_BET_CHOOSE);
                break;
            case R.id.btn_bet_bai:
                mBetMoney = 10;
                playGameSound(GameSoundPool.GAME_SOUND_BET_CHOOSE);
                break;
            case R.id.btn_bet_qian:
                mBetMoney = 100;
                playGameSound(GameSoundPool.GAME_SOUND_BET_CHOOSE);
                break;
            case R.id.btn_bet_wan:
                mBetMoney = 1000;
                playGameSound(GameSoundPool.GAME_SOUND_BET_CHOOSE);
                break;
            case R.id.coin://充值
                forwardCharge();
                break;
            case R.id.group_water://庄家流水
                showWater();
                break;
            case R.id.btn_sz://申请上下庄
                showSz();
                break;
            case R.id.btn_record://胜负记录
                showRecord();
                break;
        }
    }


    /**
     * 上庄列表
     */
    private void showSz() {
        if (mBankerBean == null) {
            return;
        }
        mGameNzSzDialogFragment = new GameNzSzDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.STREAM, mStream);
        bundle.putString(Constants.LIMIT, mBankerLimitString);
        mGameNzSzDialogFragment.setArguments(bundle);
        mGameNzSzDialogFragment.show(((LiveActivity) mContext).getSupportFragmentManager(), "GameNzSzDialogFragment");
    }


    /**
     * 庄家流水
     */
    private void showWater() {
        if (mBankerBean == null) {
            return;
        }
        mGameNzLsDialogFragment = new GameNzLsDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.UID, mBankerBean.getBankerId());
        bundle.putString(Constants.STREAM, mStream);
        mGameNzLsDialogFragment.setArguments(bundle);
        mGameNzLsDialogFragment.show(((LiveActivity) mContext).getSupportFragmentManager(), "GameNzLsDialogFragment");
    }

    /**
     * 胜负记录
     */
    private void showRecord() {
        mGameNzSfDialogFragment = new GameNzSfDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.STREAM, mStream);
        mGameNzSfDialogFragment.setArguments(bundle);
        mGameNzSfDialogFragment.show(((LiveActivity) mContext).getSupportFragmentManager(), "GameNzSfDialogFragment");
    }
}
