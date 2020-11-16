package com.wwsl.mdsj.presenter;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.LiveBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.MD5Util;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;

/**
 * Created by cxf on 2017/9/29.
 */

public class LiveRoomCheckLivePresenter {

    private Context mContext;
    private int mLiveType;//直播间的类型  普通 密码 门票 计时等
    private int mLiveTypeVal;//收费价格等
    private String mLiveTypeMsg;//直播间提示信息或房间密码
    private LiveBean mLiveBean;
    private ActionListener mActionListener;

    public LiveRoomCheckLivePresenter(Context context, ActionListener actionListener) {
        mContext = context;
        mActionListener = actionListener;
    }

    /**
     * 观众 观看直播
     */
    public void checkLive(LiveBean bean) {
        mLiveBean = bean;
        HttpUtil.checkLive(bean.getUid(), bean.getStream(), mCheckLiveCallback);
    }

    private HttpCallback mCheckLiveCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                if (info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    mLiveType = obj.getIntValue("type");
                    mLiveTypeVal = obj.getIntValue("type_val");
                    mLiveTypeMsg = obj.getString("type_msg");
                    switch (mLiveType) {
                        case Constants.LIVE_TYPE_NORMAL:
                            forwardNormalRoom();
                            break;
                        case Constants.LIVE_TYPE_PWD:
                            forwardPwdRoom();
                            break;
                        case Constants.LIVE_TYPE_PAY:
                        case Constants.LIVE_TYPE_TIME:
                            forwardPayRoom();
                            break;
                    }
                }
            } else {
                ToastUtil.show(msg);
            }
        }

        @Override
        public boolean showLoadingDialog() {
            return true;
        }

        @Override
        public Dialog createLoadingDialog() {
            return DialogUtil.loadingDialog(mContext,false);
        }
    };

    /**
     * 前往普通房间
     */
    private void forwardNormalRoom() {
        enterLiveRoom();
    }

    /**
     * 前往密码房间
     */
    private void forwardPwdRoom() {
        DialogUtil.showSimpleInputDialog(mContext, WordUtil.getString(R.string.live_input_password), DialogUtil.INPUT_TYPE_NUMBER_PASSWORD, new DialogUtil.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(WordUtil.getString(R.string.live_input_password));
                    return;
                }
                String password = MD5Util.getMD5(content);
                if (mLiveTypeMsg.equalsIgnoreCase(password)) {
                    dialog.dismiss();
                    enterLiveRoom();
                } else {
                    ToastUtil.show(WordUtil.getString(R.string.live_password_error));
                }
            }
        });
    }

    /**
     * 前往付费房间
     */
    private void forwardPayRoom() {
        DialogUtil.showSimpleDialog(mContext, mLiveTypeMsg, new DialogUtil.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                roomCharge();
            }
        });
    }


    public void roomCharge() {
        if (mLiveBean == null) {
            return;
        }
        HttpUtil.roomCharge(mLiveBean.getUid(), mLiveBean.getStream(), mRoomChargeCallback);
    }

    private HttpCallback mRoomChargeCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                enterLiveRoom();
            } else {
                ToastUtil.show(msg);
            }
        }

        @Override
        public boolean showLoadingDialog() {
            return true;
        }

        @Override
        public Dialog createLoadingDialog() {
            return DialogUtil.loadingDialog(mContext,false);
        }
    };

    public void cancel() {
        mActionListener = null;
        HttpUtil.cancel(HttpConst.CHECK_LIVE);
        HttpUtil.cancel(HttpConst.ROOM_CHARGE);
    }

    /**
     * 进入直播间
     */
    private void enterLiveRoom() {
        if (mActionListener != null) {
            mActionListener.onLiveRoomChanged(mLiveBean,mLiveType, mLiveTypeVal);
        }
    }


    public interface ActionListener {
        void onLiveRoomChanged(LiveBean liveBean,int liveType, int liveTypeVal);
    }
}
