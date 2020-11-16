package com.wwsl.mdsj.presenter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.live.LiveAnchorActivity;
import com.wwsl.mdsj.bean.ConfigBean;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.custom.ProgressTextView2;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.ILiveLinkMicViewHolder;
import com.wwsl.mdsj.socket.SocketClient;
import com.wwsl.mdsj.socket.SocketLinkMicPkUtil;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.utils.StringUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;
import com.wwsl.mdsj.views.LiveLinkMicPkViewHolder;

/**
 * Created by cxf on 2018/11/16.
 * 主播与主播PK逻辑
 */

public class LiveLinkMicPkPresenter implements View.OnClickListener {

    private static final int WHAT_PK_WAIT_RECEIVE = 0;//收到pk申请等待 what
    private static final int WHAT_PK_WAIT_SEND = 1;//发送pk申请等待 what
    private static final int WHAT_PK_TIME = 2;//pk时间变化 what
    private static final int LINK_MIC_COUNT_MAX = 10;
    private static final int PK_TIME_MAX = 1000 * 60 * 1;//pk时间 1分钟
    private static final int PK_TIME_MAX_2 = 1000 * 30;//惩罚时间 30秒
    private Context mContext;
    private View mRoot;
    private boolean mIsAnchor;//自己是否是主播
    private SocketClient mSocketClient;
    private ViewGroup mPkContainer;
    private boolean mIsApplyDialogShow;//是否显示了申请PK的弹窗
    private boolean mAcceptPk;//是否接受连麦
    private boolean mIsPk;//是否已经Pk了
    private String mApplyUid;//正在申请Pk的主播的uid
    private String mApplyStream;//正在申请Pk的主播的stream
    private String mLiveUid;//自己主播的uid
    private String mPkUid;//正在Pk的对方主播的uid
    private ProgressTextView2 mLinkMicWaitProgress;
    private int mPkWaitCount;//Pk弹窗等待倒计时Live
    private int mPkTimeCount;//pk时间
    private PopupWindow mPkPopWindow;
    private Handler mHandler;
    private LiveLinkMicPkViewHolder mLiveLinkMicPkViewHolder;
    private String mPkTimeString1;
    private String mPkTimeString2;
    private boolean mIsPkEnd;//pk是否结束，进入惩罚时间
    private boolean mPkSend;//pk请求是否已经发送
    private int mPkSendWaitCount;//发送pk请求后的等待时间

    public LiveLinkMicPkPresenter(Context context, ILiveLinkMicViewHolder linkMicViewHolder, boolean isAnchor, View root) {
        mContext = context;
        mIsAnchor = isAnchor;
        mRoot = root;
        mPkContainer = linkMicViewHolder.getPkContainer();
        mPkTimeString1 = WordUtil.getString(R.string.live_pk_time_1);
        mPkTimeString2 = WordUtil.getString(R.string.live_pk_time_2);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WHAT_PK_WAIT_RECEIVE:
                        onApplyPkWait();
                        break;
                    case WHAT_PK_WAIT_SEND:
                        onSendPkWait();
                        break;
                    case WHAT_PK_TIME:
                        changePkTime();
                        break;
                }
            }
        };
    }

    public void setSocketClient(SocketClient socketClient) {
        mSocketClient = socketClient;
    }

    public void setLiveUid(String liveUid) {
        mLiveUid = liveUid;
    }

    /**
     * 申请pk弹窗倒计时
     */
    private void onApplyPkWait() {
        mPkWaitCount--;
        if (mPkWaitCount >= 0) {
            if (mLinkMicWaitProgress != null) {
                mLinkMicWaitProgress.setProgress(mPkWaitCount);
                if (mHandler != null) {
                    mHandler.sendEmptyMessageAtTime(WHAT_PK_WAIT_RECEIVE, SystemClock.uptimeMillis() + 1000);
                }
            }
        } else {
            if (mPkPopWindow != null) {
                mPkPopWindow.dismiss();
            }
        }
    }

    /**
     * 发送pk申请后等待倒计时
     */
    private void onSendPkWait() {
        mPkSendWaitCount--;
        if (mPkSendWaitCount >= 0) {
            nextSendPkWaitCountDown();
        } else {
            hideSendPkWait();
            if (mIsAnchor) {
                ((LiveAnchorActivity) mContext).setPkBtnVisible(true);
            }
        }
    }

    /**
     * 进入下一次pk申请等待倒计时
     */
    private void nextSendPkWaitCountDown() {
        if (mLiveLinkMicPkViewHolder != null) {
            mLiveLinkMicPkViewHolder.setPkWaitProgress(mPkSendWaitCount);
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessageAtTime(WHAT_PK_WAIT_SEND, SystemClock.uptimeMillis() + 1000);
        }
    }

    /**
     * 隐藏pk申请等待
     */
    private void hideSendPkWait() {
        mPkSend = false;
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_PK_WAIT_SEND);
        }
        if (mLiveLinkMicPkViewHolder != null) {
            mLiveLinkMicPkViewHolder.setPkWaitProgressVisible(false);
        }
    }

    /**
     * 进入下一次pk倒计时
     */
    private void nextPkTimeCountDown() {
        if (mHandler != null) {
            mHandler.sendEmptyMessageAtTime(WHAT_PK_TIME, SystemClock.uptimeMillis() + 1000);
        }
        if (mLiveLinkMicPkViewHolder != null) {
            String s = mIsPkEnd ? mPkTimeString2 : mPkTimeString1;
            mLiveLinkMicPkViewHolder.setTime(s + " " + StringUtil.getDurationText(mPkTimeCount));
        }
    }

    /**
     * pk时间倒计时
     */
    private void changePkTime() {
        mPkTimeCount -= 1000;
        if (mPkTimeCount > 0) {
            nextPkTimeCountDown();
        } else {
            if (mIsPkEnd) {
                onLinkMicPkClose();
                if (mIsAnchor) {
                    ((LiveAnchorActivity) mContext).setPkBtnVisible(true);
                }
            }
        }
    }


    /**
     * 发起主播PK申请
     */
    public void applyLinkMicPk(String pkUid, String stream) {
        if (mPkSend) {
            ToastUtil.show(R.string.link_mic_apply_waiting);
            return;
        }
        if (mIsPk) {
            ToastUtil.show(R.string.live_link_mic_cannot_pk);
            return;
        }
        mPkSend = true;
        SocketLinkMicPkUtil.linkMicPkApply(mSocketClient, pkUid, stream);
        ToastUtil.show(R.string.link_mic_apply_pk);

        if (mLiveLinkMicPkViewHolder == null) {
            mLiveLinkMicPkViewHolder = new LiveLinkMicPkViewHolder(mContext, mPkContainer);
            mLiveLinkMicPkViewHolder.addToParent();
        }
        mLiveLinkMicPkViewHolder.setPkWaitProgressVisible(true);
        mPkSendWaitCount = LINK_MIC_COUNT_MAX;
        nextSendPkWaitCountDown();
        if (mIsAnchor) {
            ((LiveAnchorActivity) mContext).setPkBtnVisible(false);
        }
    }

    /**
     * 主播与主播PK  主播收到其他主播发过来的PK申请的回调
     */
    public void onLinkMicPkApply(UserBean u, String stream) {
        if (!mIsAnchor) {
            return;
        }
        if (u == null || TextUtils.isEmpty(stream)) {
            return;
        }
        if (!TextUtils.isEmpty(mApplyUid) && mApplyUid.equals(u.getId())) {
            return;
        }
        if (!mIsPk && !mIsApplyDialogShow) {
            mApplyUid = u.getId();
            mApplyStream = stream;
            showApplyDialog(u);
        } else {
            SocketLinkMicPkUtil.linkMicPkBusy(mSocketClient, u.getId());
        }
    }

    /**
     * 显示申请PK的弹窗
     */
    private void showApplyDialog(UserBean u) {
        mIsApplyDialogShow = true;
        mAcceptPk = false;
        View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_link_mic_pk_wait, null);
        mLinkMicWaitProgress = v.findViewById(R.id.pk_wait_progress);
        v.findViewById(R.id.btn_refuse).setOnClickListener(this);
        v.findViewById(R.id.btn_accept).setOnClickListener(this);
        mPkWaitCount = LINK_MIC_COUNT_MAX;
        mPkPopWindow = new PopupWindow(v, DpUtil.dp2px(280), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPkPopWindow.setBackgroundDrawable(new ColorDrawable());
        mPkPopWindow.setOutsideTouchable(true);
        mPkPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mHandler != null) {
                    mHandler.removeMessages(WHAT_PK_WAIT_RECEIVE);
                }
                if (mAcceptPk) {
                    HttpUtil.livePkCheckLive(mApplyUid, mApplyStream, new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0) {
                                SocketLinkMicPkUtil.linkMicPkAccept(mSocketClient, mApplyUid);
                                mIsPk = true;
                            } else {
                                ToastUtil.show(msg);
                            }
                        }
                    });
                } else {
                    if (mPkWaitCount < 0) {
                        SocketLinkMicPkUtil.linkMicPkNotResponse(mSocketClient, mApplyUid);
                    } else {
                        SocketLinkMicPkUtil.linkMicPkRefuse(mSocketClient, mApplyUid);
                    }
                    mApplyUid = null;
                    mApplyStream = null;
                }
                mIsApplyDialogShow = false;
                mLinkMicWaitProgress = null;
                mPkPopWindow = null;
            }
        });
        mPkPopWindow.showAtLocation(mRoot, Gravity.CENTER, 0, 0);
        if (mHandler != null) {
            mHandler.sendEmptyMessageAtTime(WHAT_PK_WAIT_RECEIVE, SystemClock.uptimeMillis() + 1000);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_refuse://拒绝PK
                refuseLinkMic();
                break;
            case R.id.btn_accept://接受Pk
                acceptLinkMic();
                break;
        }
    }

    /**
     * 拒绝PK
     */
    private void refuseLinkMic() {
        if (mPkPopWindow != null) {
            mPkPopWindow.dismiss();
        }
    }

    /**
     * 接受PK
     */
    private void acceptLinkMic() {
        mAcceptPk = true;
        if (mPkPopWindow != null) {
            mPkPopWindow.dismiss();
        }
    }

    /**
     * pk 进度发送变化
     *
     * @param leftGift
     * @param rightGift
     */
    public void onPkProgressChanged(long leftGift, long rightGift) {
        if (mLiveLinkMicPkViewHolder != null) {
            mLiveLinkMicPkViewHolder.onProgressChanged(leftGift, rightGift);
        }
    }


    /**
     * 进房间的时候PK开始
     */
    public void onEnterRoomPkStart(String pkUid, long leftGift, long rightGift, int pkTime) {
        mIsPk = true;
        mIsPkEnd = false;
        mPkUid = pkUid;
        mApplyUid = null;
        mApplyStream = null;
        if (mLiveLinkMicPkViewHolder == null) {
            mLiveLinkMicPkViewHolder = new LiveLinkMicPkViewHolder(mContext, mPkContainer);
            mLiveLinkMicPkViewHolder.addToParent();
        }
        mLiveLinkMicPkViewHolder.showTime();
        mLiveLinkMicPkViewHolder.onEnterRoomPkStart();
        mLiveLinkMicPkViewHolder.onProgressChanged(leftGift, rightGift);
        mPkTimeCount = pkTime;
        nextPkTimeCountDown();
    }


    /**
     * 主播与主播PK 所有人收到PK开始的回调
     */
    public void onLinkMicPkStart(String pkUid) {
        mIsPk = true;
        hideSendPkWait();
        mIsPkEnd = false;
        mPkUid = pkUid;
        mApplyUid = null;
        mApplyStream = null;
        if (mLiveLinkMicPkViewHolder == null) {
            mLiveLinkMicPkViewHolder = new LiveLinkMicPkViewHolder(mContext, mPkContainer);
            mLiveLinkMicPkViewHolder.addToParent();
        }
        mLiveLinkMicPkViewHolder.startAnim();
        mLiveLinkMicPkViewHolder.showTime();
        ConfigBean configBean = AppConfig.getInstance().getConfig();
        if (configBean != null) {
            mPkTimeCount = Integer.parseInt(configBean.getPkTime()) * 1000;
        } else {
            mPkTimeCount = PK_TIME_MAX;
        }
        nextPkTimeCountDown();
        if (mIsAnchor) {
            ((LiveAnchorActivity) mContext).setPkBtnVisible(false);
        }
    }

    /**
     * 主播与主播PK PK结果的回调
     */
    public void onLinkMicPkEnd(String winUid) {
        if (mIsPkEnd) {
            return;
        }
        mIsPkEnd = true;
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_PK_TIME);
        }
        if (mLiveLinkMicPkViewHolder != null) {
            if (!TextUtils.isEmpty(winUid)) {
                if ("0".equals(winUid)) {
                    mLiveLinkMicPkViewHolder.end(0);
                    mLiveLinkMicPkViewHolder.hideTime();
                    if (mHandler != null) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onLinkMicPkClose();
                                if (mIsAnchor) {
                                    ((LiveAnchorActivity) mContext).setPkBtnVisible(true);
                                }
                            }
                        }, 3000);
                    }
                } else {
                    if (winUid.equals(mLiveUid)) {
                        mLiveLinkMicPkViewHolder.end(1);
                    } else {
                        mLiveLinkMicPkViewHolder.end(-1);
                    }
                    ConfigBean configBean = AppConfig.getInstance().getConfig();
                    if (configBean != null) {
                        mPkTimeCount = Integer.parseInt(configBean.getPunishTime()) * 1000;
                    } else {
                        mPkTimeCount = PK_TIME_MAX_2;//进入惩罚时间
                    }
                    nextPkTimeCountDown();
                }
            }

        }
    }

    /**
     * 主播与主播PK 断开连麦PK的回调
     */
    public void onLinkMicPkClose() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mPkPopWindow != null) {
            mPkPopWindow.dismiss();
        }
        mPkPopWindow = null;
        mIsPk = false;
        mIsPkEnd = false;
        hideSendPkWait();
        mPkUid = null;
        mApplyUid = null;
        mApplyStream = null;
        if (mLiveLinkMicPkViewHolder != null) {
            mLiveLinkMicPkViewHolder.removeFromParent();
            mLiveLinkMicPkViewHolder.release();
        }
        mLiveLinkMicPkViewHolder = null;
    }

    /**
     * 主播与主播Pk 对方主播拒绝Pk的回调
     */
    public void onLinkMicPkRefuse() {
        hideSendPkWait();
        if (mIsAnchor) {
            ((LiveAnchorActivity) mContext).setPkBtnVisible(true);
        }
        ToastUtil.show(R.string.link_mic_refuse_pk);
    }

    /**
     * 主播与主播Pk  对方主播无响应的回调
     */
    public void onLinkMicPkNotResponse() {
        hideSendPkWait();
        if (mIsAnchor) {
            ((LiveAnchorActivity) mContext).setPkBtnVisible(true);
        }
        ToastUtil.show(R.string.link_mic_anchor_not_response_2);
    }

    /**
     * 主播与主播Pk  对方主播正在忙的回调
     */
    public void onLinkMicPkBusy() {
        hideSendPkWait();
        if (mIsAnchor) {
            ((LiveAnchorActivity) mContext).setPkBtnVisible(true);
        }
        ToastUtil.show(R.string.link_mic_anchor_busy_2);
    }


    public void release() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        mSocketClient = null;
        if (mLiveLinkMicPkViewHolder != null) {
            mLiveLinkMicPkViewHolder.release();
        }
        mLiveLinkMicPkViewHolder = null;
    }


    public void clearData() {
        mIsApplyDialogShow = false;
        mAcceptPk = false;
        mIsPk = false;
        mApplyUid = null;
        mApplyStream = null;
        mLiveUid = null;
        mPkUid = null;
        mPkWaitCount = 0;
        mPkTimeCount = 0;
        mIsPkEnd = false;
        mPkSend = false;
        mPkSendWaitCount = 0;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mLiveLinkMicPkViewHolder != null) {
            mLiveLinkMicPkViewHolder.release();
            mLiveLinkMicPkViewHolder.removeFromParent();
        }
        mLiveLinkMicPkViewHolder = null;
    }

}
