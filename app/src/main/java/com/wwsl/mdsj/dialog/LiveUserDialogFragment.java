package com.wwsl.mdsj.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.UserHomePageActivity;
import com.wwsl.mdsj.activity.live.LiveActivity;
import com.wwsl.mdsj.bean.ImpressBean;
import com.wwsl.mdsj.bean.LevelBean;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.custom.MyTextView;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.im.ImMessageUtil;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.utils.IconUtil;
import com.wwsl.mdsj.utils.StringUtil;
import com.wwsl.mdsj.utils.TextRender;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/15.
 * 直播间个人资料弹窗
 */

public class LiveUserDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private static final int TYPE_AUD_AUD = 1;//观众点别的观众
    private static final int TYPE_ANC_AUD = 2;//主播点观众
    private static final int TYPE_AUD_ANC = 3;//观众点主播
    private static final int TYPE_AUD_SELF = 4;//观众点自己
    private static final int TYPE_ANC_SELF = 5;//主播点自己

    private static final int SETTING_ACTION_SELF = 0;//设置 自己点自己
    private static final int SETTING_ACTION_AUD = 30;//设置 普通观众点普通观众 或所有人点超管
    private static final int SETTING_ACTION_ADM = 40;//设置 房间管理员点普通观众
    private static final int SETTING_ACTION_SUP = 60;//设置 超管点主播
    private static final int SETTING_ACTION_ANC_AUD = 501;//设置 主播点普通观众
    private static final int SETTING_ACTION_ANC_ADM = 502;//设置 主播点房间管理员
    private LinearLayout mRootGroup;
    private ImageView mAvatar;
    private ImageView mLevelAnchor;
    private ImageView mLevel;
    private ImageView mSex;
    private TextView mName;
    private TextView mID;
    private LinearLayout mImpressGroup;
    private TextView mFollow;
    private TextView mFans;
    private TextView mConsume;//消费
    private TextView mConsumeTip;
    private String mLiveUid;
    private String mToUid;
    private TextView mBtnFollow;
    private int mType;
    private int mAction;
    private String mToName;//对方的名字
    private UserBean mUserBean;
    private boolean mFollowing;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_user;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(270);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        mLiveUid = bundle.getString(Constants.LIVE_UID);
        mToUid = bundle.getString(Constants.TO_UID);
        if (TextUtils.isEmpty(mLiveUid) || TextUtils.isEmpty(mToUid)) {
            return;
        }
        mRootGroup = (LinearLayout) mRootView;
        mAvatar = (ImageView) mRootView.findViewById(R.id.avatar);
        mLevelAnchor = (ImageView) mRootView.findViewById(R.id.anchor_level);
        mLevel = (ImageView) mRootView.findViewById(R.id.level);
        mSex = (ImageView) mRootView.findViewById(R.id.sex);
        mName = (TextView) mRootView.findViewById(R.id.name);
        mID = (TextView) mRootView.findViewById(R.id.id_val);
        mImpressGroup = (LinearLayout) mRootView.findViewById(R.id.impress_group);
        mFollow = (TextView) mRootView.findViewById(R.id.follow);
        mFans = (TextView) mRootView.findViewById(R.id.fans);
        mConsume = (TextView) mRootView.findViewById(R.id.consume);
        mConsumeTip = (TextView) mRootView.findViewById(R.id.consume_tip);
        mRootView.findViewById(R.id.btn_close).setOnClickListener(this);
        getType();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View bottomView = null;
        if (mType == TYPE_AUD_ANC) {
            if (mImpressGroup.getVisibility() != View.VISIBLE) {
//                mImpressGroup.setVisibility(View.VISIBLE);
            }
            bottomView = inflater.inflate(R.layout.dialog_live_user_bottom_1, mRootGroup, true);
        } else if (mType == TYPE_AUD_AUD) {
            bottomView = inflater.inflate(R.layout.dialog_live_user_bottom_1, mRootGroup, true);
        } else if (mType == TYPE_ANC_AUD) {
            bottomView = inflater.inflate(R.layout.dialog_live_user_bottom_2, mRootGroup, true);
        } else if (mType == TYPE_AUD_SELF) {
            bottomView = inflater.inflate(R.layout.dialog_live_user_bottom_3, mRootGroup, true);
        }
        if (bottomView != null) {
            mBtnFollow = (TextView) bottomView.findViewById(R.id.btn_follow);
            if (mBtnFollow != null) {
                mBtnFollow.setOnClickListener(this);
            }
            View btnPriMsg = bottomView.findViewById(R.id.btn_pri_msg);
            if (btnPriMsg != null) {
                if (AppConfig.hideChatRoom()) {
                    View btnPriMsgDivider = bottomView.findViewById(R.id.btn_pri_msg_divider);
                    if (btnPriMsgDivider != null) {
                        btnPriMsgDivider.setVisibility(View.GONE);
                    }
                    btnPriMsg.setVisibility(View.GONE);
                } else {
                    btnPriMsg.setOnClickListener(this);
                }
            }
            View btnHomePage = bottomView.findViewById(R.id.btn_home_page);
            if (btnHomePage != null) {
                btnHomePage.setOnClickListener(this);
            }
        }
        loadData();
    }

    private void getType() {
        String uid = AppConfig.getInstance().getUid();
        if (mToUid.equals(mLiveUid)) {
            if (mLiveUid.equals(uid)) {//主播点自己
                mType = TYPE_ANC_SELF;
            } else {//观众点主播
                mType = TYPE_AUD_ANC;
            }
        } else {
            if (mLiveUid.equals(uid)) {//主播点观众
                mType = TYPE_ANC_AUD;
            } else {
                if (mToUid.equals(uid)) {//观众点自己
                    mType = TYPE_AUD_SELF;
                } else {//观众点别的观众
                    mType = TYPE_AUD_AUD;
                }
            }
        }
    }

    private void loadData() {
        HttpUtil.getLiveUser(mToUid, mLiveUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    showData(info[0]);
                }
            }
        });
    }

    private void showData(String data) {
        JSONObject obj = JSON.parseObject(data);
        mUserBean = JSON.toJavaObject(obj, UserBean.class);
        AppConfig appConfig = AppConfig.getInstance();
        mID.setText(String.format("ID:%s", mUserBean.getSpecialNameTip()));
        mToName = obj.getString("user_nicename");
        mName.setText(mToName);
        ImgLoader.displayAvatar(obj.getString("avatar"), mAvatar);

        LevelBean anchorLevelBean = appConfig.getAnchorLevel(obj.getIntValue("level_anchor"));

        if (null != anchorLevelBean) {
            ImgLoader.display(StringUtil.checkNullStr(anchorLevelBean.getThumb()), mLevelAnchor);
        }

        LevelBean levelBean = appConfig.getLevel(obj.getIntValue("level"));

        if (levelBean != null) {
            ImgLoader.display(StringUtil.checkNullStr(levelBean.getThumb()), mLevel);
        }

        mSex.setImageResource(IconUtil.getSexIcon(obj.getIntValue("sex")));
        mFollow.setText(TextRender.renderLiveUserDialogData(new BigDecimal(obj.get("follows").toString()).longValue()));
        mFans.setText(TextRender.renderLiveUserDialogData(new BigDecimal(obj.get("fans").toString()).longValue()));
        mConsume.setText(TextRender.renderLiveUserDialogData(new BigDecimal(obj.get("consumption").toString()).longValue()));
        mConsumeTip.setText(WordUtil.getString(R.string.live_user_send) + appConfig.getCoinName());
        if (mType == TYPE_AUD_ANC) {
            showImpress(obj.getString("label"));
        }
        mFollowing = obj.getIntValue("isattention") == 1;
        if (mFollowing) {
            mBtnFollow.setText(WordUtil.getString(R.string.following));
        }
        mAction = obj.getIntValue("action");
        if (mAction == SETTING_ACTION_AUD) {//设置 普通观众点普通观众 或所有人点超管
            View btnReport = mRootView.findViewById(R.id.btn_report);
            btnReport.setVisibility(View.VISIBLE);
            btnReport.setOnClickListener(this);
        } else if (mAction == SETTING_ACTION_ADM//设置 房间管理员点普通观众
                || mAction == SETTING_ACTION_SUP//设置 超管点主播
                || mAction == SETTING_ACTION_ANC_AUD//设置 主播点普通观众
                || mAction == SETTING_ACTION_ANC_ADM) {//设置 主播点房间管理员
            View btnSetting = mRootView.findViewById(R.id.btn_setting);
            btnSetting.setVisibility(View.VISIBLE);
            btnSetting.setOnClickListener(this);
        }
    }

    private void showImpress(String impressJson) {
        List<ImpressBean> list = JSON.parseArray(impressJson, ImpressBean.class);
        if (list.size() > 2) {
            list = list.subList(0, 2);
        }
        ImpressBean lastBean = new ImpressBean();
        lastBean.setName("+ " + WordUtil.getString(R.string.impress_add));
        lastBean.setColor("#ffdd00");
        list.add(lastBean);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        for (int i = 0, size = list.size(); i < size; i++) {
            MyTextView myTextView = (MyTextView) inflater.inflate(R.layout.view_impress_item_2, mImpressGroup, false);
            ImpressBean bean = list.get(i);
            if (i == size - 1) {
                myTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addImpress();
                    }
                });
            } else {
                bean.setCheck(1);
            }
            myTextView.setBean(bean);
            mImpressGroup.addView(myTextView);
        }
    }


    /**
     * 添加主播印象
     */
    private void addImpress() {
        dismiss();
        ((LiveActivity) mContext).openAddImpressWindow(mLiveUid);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                dismiss();
                break;
            case R.id.btn_follow:
                setAttention();
                break;
            case R.id.btn_pri_msg:
                openChatRoomWindow();
                break;
            case R.id.btn_home_page:
                forwardHomePage();
                break;
            case R.id.btn_setting:
                setting();
                break;
            case R.id.btn_report:
                report();
                break;
        }
    }

    /**
     * 打开私信聊天窗口
     */
    private void openChatRoomWindow() {
        if (mUserBean != null) {
            dismiss();
            boolean res = ImMessageUtil.getInstance().markAllMessagesAsRead(mToUid);
            if (res) {
                ImMessageUtil.getInstance().refreshAllUnReadMsgCount();
            }
            ((LiveActivity) mContext).openChatRoomWindow(mUserBean, mFollowing);
        }
    }

    /**
     * 关注
     */
    private void setAttention() {
        HttpUtil.setAttention(Constants.FOLLOW_FROM_LIVE, mToUid, mAttentionCallback);
    }

    private CommonCallback<Integer> mAttentionCallback = new CommonCallback<Integer>() {

        @Override
        public void callback(Integer isAttention) {
            mFollowing = isAttention == 1;
            if (mBtnFollow != null) {
                mBtnFollow.setText(mFollowing ? R.string.following : R.string.follow);
            }
            if (isAttention == 1 && mLiveUid.equals(mToUid)) {//关注了主播
                ((LiveActivity) mContext).sendSystemMessage(
                        AppConfig.getInstance().getUserBean().getUsername() + WordUtil.getString(R.string.live_follow_anchor));
            }
        }
    };

    /**
     * 跳转到个人主页
     */
    private void forwardHomePage() {
        dismiss();
        UserHomePageActivity.forward(mContext, mToUid);
    }

    /**
     * 举报
     */
    private void report() {
        DialogUtil.showSimpleDialog(mContext, WordUtil.getString(R.string.live_report), new DialogUtil.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                HttpUtil.setReport(mToUid, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                        }
                    }
                });
            }
        });
    }

    /**
     * 设置
     * <p>
     * 某个大神说，角色是权限的集合。。
     * <p>
     * 理论上，角色的权限应该有服务端以数组或集合的形式返回，这样可以在后台动态配置某种角色的权限，而不是这样口头约定写死。。。
     * 然而，是服务端这样做的，我也很无奈。。。也许他们不知道如何做成动态配置的吧。。
     * <p>
     * 我一直想通过不断重构把代码写的像艺术品，然而，最近发现，这完全是多此一举，自讨苦吃。。是我太天真了。。
     * 下面是我发现的一篇文章，说的非常好，也点醒了我。。如果你们发现当前代码写的太烂，不堪入目的话，请阅读下面的文章。
     * <p>
     * https://www.jianshu.com/p/71521541cd25?utm_campaign=haruki&utm_content=note&utm_medium=reader_share&utm_source=weixin_timeline&from=timeline
     */
    private void setting() {
        List<Integer> list = new ArrayList<>();
        switch (mAction) {
            case SETTING_ACTION_ADM://设置 房间管理员点普通观众
                list.add(R.string.live_setting_kick);
                list.add(R.string.live_setting_gap);
                break;
            case SETTING_ACTION_SUP://设置 超管点主播
                list.add(R.string.live_setting_close_live);
                list.add(R.string.live_setting_forbid_account);
                break;
            case SETTING_ACTION_ANC_AUD://设置 主播点普通观众
                list.add(R.string.live_setting_kick);
                list.add(R.string.live_setting_gap);
                list.add(R.string.live_setting_gap_list);
                list.add(R.string.live_setting_admin);
                list.add(R.string.live_setting_admin_list);
                break;
            case SETTING_ACTION_ANC_ADM://设置 主播点房间管理员
                list.add(R.string.live_setting_kick);
                list.add(R.string.live_setting_gap);
                list.add(R.string.live_setting_gap_list);
                list.add(R.string.live_setting_admin_cancel);
                list.add(R.string.live_setting_admin_list);
                break;
        }

        DialogUtil.showStringArrayDialog(mContext, list.toArray(new Integer[list.size()]), mArrayDialogCallback);
    }

    private DialogUtil.StringArrayDialogCallback mArrayDialogCallback = new DialogUtil.StringArrayDialogCallback() {
        @Override
        public void onItemClick(String text, int tag) {
            switch (tag) {
                case R.string.live_setting_kick://踢人
                    kick();
                    break;
                case R.string.live_setting_gap://禁言
                    setShutUp();
                    break;
                case R.string.live_setting_gap_list://禁言列表
                    gapList();
                    break;
                case R.string.live_setting_admin://设为管理
                case R.string.live_setting_admin_cancel://取消管理
                    setAdmin();
                    break;
                case R.string.live_setting_admin_list://管理员列表
                    adminList();
                    break;
                case R.string.live_setting_close_live://关闭直播
                    closeLive();
                    break;
                case R.string.live_setting_forbid_account://禁用账户
                    forbidAccount();
                    break;
            }
        }
    };

    /**
     * 禁言列表
     */
    private void gapList() {
        dismiss();
        ((LiveActivity) mContext).openGapListWindow();
    }

    /**
     * 查看管理员列表
     */
    private void adminList() {
        dismiss();
        ((LiveActivity) mContext).openAdminListWindow();
    }

    /**
     * 踢人
     */
    private void kick() {
        HttpUtil.kicking(mLiveUid, mToUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    ((LiveActivity) mContext).kickUser(mToUid, mToName);
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    /**
     * 禁言
     */
    private void setShutUp() {
        HttpUtil.setShutUp(mLiveUid, mToUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    ((LiveActivity) mContext).setShutUp(mToUid, mToName);
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    /**
     * 设置或取消管理员
     */
    private void setAdmin() {
        HttpUtil.setAdmin(mLiveUid, mToUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    int res = JSON.parseObject(info[0]).getIntValue("isadmin");
                    if (res == 1) {//被设为管理员
                        mAction = SETTING_ACTION_ANC_ADM;
                    } else {//被取消管理员
                        mAction = SETTING_ACTION_ANC_AUD;
                    }
                    ((LiveActivity) mContext).sendSetAdminMessage(res, mToUid, mToName);
                }
            }
        });
    }


    /**
     * 超管关闭直播间
     */
    private void closeLive() {
        dismiss();
        HttpUtil.superCloseRoom(mLiveUid, false, mSuperCloseRoomCallback);
    }

    /**
     * 超管关闭直播间并禁用主播账户
     */
    private void forbidAccount() {
        dismiss();
        HttpUtil.superCloseRoom(mLiveUid, true, mSuperCloseRoomCallback);
    }

    private HttpCallback mSuperCloseRoomCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                ((LiveActivity) mContext).superCloseRoom();
            } else {
                ToastUtil.show(msg);
            }
        }
    };

}
