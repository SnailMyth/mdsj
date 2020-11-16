package com.wwsl.mdsj.activity.message;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.event.VideoFollowEvent;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.im.ImListAdapter;
import com.wwsl.mdsj.im.ImMessageUtil;
import com.wwsl.mdsj.im.ImUserBean;
import com.wwsl.mdsj.im.ImUserMsgEvent;
import com.wwsl.mdsj.utils.SpUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;
import com.wwsl.mdsj.views.AbsViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChatListViewHolder extends AbsViewHolder implements View.OnClickListener, ImListAdapter.ActionListener {

    public static final int TYPE_ACTIVITY = 0;
    public static final int TYPE_DIALOG = 1;
    private int mType;

    private RecyclerView mRecyclerView;
    private ImListAdapter mAdapter;
    private ActionListener mActionListener;
    private View mBtnBack;
    private String mLiveUid;//主播的uid

    public ChatListViewHolder(Context context, ViewGroup parentView, int type) {
        super(context, parentView, type);
    }

    @Override
    protected void processArguments(Object... args) {
        mType = (int) args[0];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_chat_list;
    }

    @Override
    public void init() {
        //mNoData = findViewById(R.id.no_data);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ImListAdapter(mContext);
        mAdapter.setActionListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mBtnBack = findViewById(R.id.btn_back);
        if (mType == TYPE_ACTIVITY) {
            mBtnBack.setOnClickListener(this);
        } else {
            mBtnBack.setVisibility(View.INVISIBLE);
            View top = findViewById(R.id.top);
            top.setBackgroundColor(0xfff9fafb);
        }
        findViewById(R.id.btn_ignore).setOnClickListener(this);

        EventBus.getDefault().register(this);
        if (AppConfig.SYSTEM_MSG_APP_ICON) {
            ImageView avatar = (ImageView) findViewById(R.id.avatar);
            avatar.setImageResource(R.mipmap.icon_launcher);
        }
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public void release() {
        EventBus.getDefault().unregister(this);
        mActionListener = null;
        HttpUtil.cancel(HttpConst.GET_SYSTEM_MESSAGE_LIST);
        HttpUtil.cancel(HttpConst.GET_IM_USER_INFO);
    }

    public void setLiveUid(String liveUid) {
        mLiveUid = liveUid;
    }

    public void loadData() {
//        getSystemMessageList();
        final boolean needAnchorItem = mType == TYPE_DIALOG
                && !TextUtils.isEmpty(mLiveUid) && !mLiveUid.equals(AppConfig.getInstance().getUid());
        String uids = ImMessageUtil.getInstance().getConversationUids();
        if (TextUtils.isEmpty(uids)) {
            if (needAnchorItem) {
                uids = mLiveUid;
            } else {
                return;
            }
        } else {
            if (needAnchorItem) {
                if (!uids.contains(mLiveUid)) {
                    uids = mLiveUid + "," + uids;
                }
            }
        }
        HttpUtil.getImUserInfo(uids, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<ImUserBean> list = JSON.parseArray(Arrays.toString(info), ImUserBean.class);
                    list = ImMessageUtil.getInstance().getLastMsgInfoList(list);
                    if (mRecyclerView != null && mAdapter != null && list != null) {
                        if (needAnchorItem) {
                            int anchorItemPosition = -1;
                            for (int i = 0, size = list.size(); i < size; i++) {
                                ImUserBean bean = list.get(i);
                                if (bean != null) {
                                    if (mLiveUid.equals(bean.getId())) {
                                        anchorItemPosition = i;
                                        bean.setAnchorItem(true);
                                        if (!bean.isHasConversation()) {
                                            bean.setLastMessage(WordUtil.getString(R.string.im_live_anchor_msg));
                                        }
                                        break;
                                    }
                                }
                            }
                            if (anchorItemPosition > 0) {//把主播的会话排在最前面
                                Collections.sort(list, new Comparator<ImUserBean>() {
                                    @Override
                                    public int compare(ImUserBean bean1, ImUserBean bean2) {
                                        if (mLiveUid.equals(bean1.getId())) {
                                            return -1;
                                        } else if (mLiveUid.equals(bean2.getId())) {
                                            return 1;
                                        }
                                        return 0;
                                    }
                                });
                            }
                        }
                        mAdapter.setList(list);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                if (mActionListener != null) {
                    mActionListener.onCloseClick();
                }
                break;
            case R.id.btn_ignore:
                ignoreUnReadCount();
                break;
        }
    }


    @Override
    public void onItemClick(ImUserBean bean) {
        if (bean != null) {
            boolean res = ImMessageUtil.getInstance().markAllMessagesAsRead(bean.getId());
            if (res) {
                ImMessageUtil.getInstance().refreshAllUnReadMsgCount();
            }
            if (mActionListener != null) {
                mActionListener.onItemClick(bean);
            }
        }
    }

    @Override
    public void onItemDelete(ImUserBean bean, int size) {
        ImMessageUtil.getInstance().removeConversation(bean.getId());
    }

    public interface ActionListener {
        void onCloseClick();

        void onItemClick(ImUserBean bean);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(VideoFollowEvent e) {
        if (e != null) {
            if (mAdapter != null) {
                mAdapter.setFollow(e.getMToUid(), e.getMIsAttention());
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImUserMsgEvent(final ImUserMsgEvent e) {
        if (e != null && mRecyclerView != null && mAdapter != null) {
            int position = mAdapter.getPosition(e.getUid());
            if (position < 0) {
                HttpUtil.getImUserInfo(e.getUid(), new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            ImUserBean bean = JSON.parseObject(info[0], ImUserBean.class);
                            bean.setLastMessage(e.getLastMessage());
                            bean.setUnReadCount(e.getUnReadCount());
                            bean.setLastTime(e.getLastTime());
                            mAdapter.insertItem(bean);


                        }
                    }
                });
            } else {
                mAdapter.updateItem(e.getLastMessage(), e.getLastTime(), e.getUnReadCount(), position);
            }
        }
    }

    /**
     * 忽略未读
     */
    private void ignoreUnReadCount() {
        SpUtil.getInstance().setBooleanValue(SpUtil.HAS_SYSTEM_MSG, false);
        ImMessageUtil.getInstance().markAllConversationAsRead();
        if (mAdapter != null) {
            mAdapter.resetAllUnReadCount();
        }
        ToastUtil.show(R.string.im_msg_ignore_unread_2);
    }
}
