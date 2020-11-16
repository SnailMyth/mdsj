package com.wwsl.mdsj.activity.main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.UserHomePageActivity;
import com.wwsl.mdsj.activity.message.ChatActivity;
import com.wwsl.mdsj.activity.message.MessageSecondActivity;
import com.wwsl.mdsj.activity.message.SysMessageActivity;
import com.wwsl.mdsj.adapter.MsgShortAdapter;
import com.wwsl.mdsj.bean.AdvertiseBean;
import com.wwsl.mdsj.bean.MsgShortBean;
import com.wwsl.mdsj.bean.net.MsgConservationNetBean;
import com.wwsl.mdsj.bean.net.RecommendUserBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.im.ImMessageUtil;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.utils.ClickUtil;
import com.wwsl.mdsj.utils.ZjUtils;
import com.wwsl.mdsj.views.AbsMainViewHolder;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.wwsl.mdsj.adapter.MsgShortAdapter.PAYLOAD_FOLLOW;

public class MainMessageViewHolder extends AbsMainViewHolder implements View.OnClickListener {

    private SwipeRecyclerView msgRecycler;
    private SmartRefreshLayout mRefreshLayout;
    private MsgShortAdapter msgShortAdapter;
    private List<MsgShortBean> beans;
    private List<MsgShortBean> recommendUser;
    private int recommendUserNum = 0;//用于刷新推荐用户
    private int mPage = 1;
    private int needRefreshData = 0;

    //消息数量
    private TextView badge1, badge2, badge3, badge4;

    public MainMessageViewHolder(Context context, ViewGroup parentView, AppCompatActivity activity) {
        super(context, parentView, activity);
    }

    @Override
    public void onClick(View view) {
        if (!ClickUtil.canClick()) return;
        switch (view.getId()) {
            case R.id.btnLike:
                forwardSecond(Constants.TYPE_LIKE);
                break;
            case R.id.btnDouding:
                forwardSecond(Constants.TYPE_COMMENT);
                break;
            case R.id.btnGift:
                forwardSecond(Constants.TYPE_AT_ME);
                break;
            case R.id.btnVipCenter:
                forwardSecond(Constants.TYPE_FANS);
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_message;
    }

    @Override
    public void init() {
        findView();
        beans = new ArrayList<>();
        recommendUser = new ArrayList<>();

        msgShortAdapter = new MsgShortAdapter(beans);
        msgRecycler.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));

        msgShortAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.imgRemove:
                    msgShortAdapter.removeAt(position);
                    recommendUserNum--;
                    break;
                case R.id.btnFollow:
                    MsgShortBean msgShortBean = msgShortAdapter.getData().get(position);
                    HttpUtil.setAttention(Constants.FOLLOW_FROM_MAIN_MSG, msgShortBean.getUid(), new CommonCallback<Integer>() {
                        @Override
                        public void callback(Integer bean) {
                            if (bean == 1) {
                                msgShortAdapter.getData().get(position).setFollow(true);
                                msgShortAdapter.notifyItemChanged(position, PAYLOAD_FOLLOW);
                            }
                        }
                    });
                    break;
            }
        });

        msgShortAdapter.setOnItemClickListener((adapter, view, position) -> {
            MsgShortBean msgShortBean = msgShortAdapter.getData().get(position);
            if (msgShortBean.getType() == Constants.MESSAGE_TYPE_MSG) {
                if (msgShortBean.getSubType() >= 1 && msgShortBean.getSubType() <= 4) {
                    SysMessageActivity.forward(mContext, msgShortBean.getSubType());
                    if (!msgShortBean.getUnreadNum().equals("0")) {
                        HttpUtil.readSystemMessageList(msgShortBean.getSubType());
                        msgShortAdapter.notifyItemChanged(position, MsgShortAdapter.PAYLOAD_UNREAD_CLEAR);
                    }
                } else if (msgShortBean.getSubType() == Constants.MESSAGE_SUBTYPE_FRIEND) {
                    ChatActivity.forward(mContext);
                }
            } else if (msgShortBean.getType() == Constants.MESSAGE_TYPE_TEXT_RECOMMEND) {
                UserHomePageActivity.forward(mContext, msgShortBean.getUid());
            } else if (msgShortBean.getType() == Constants.MESSAGE_TYPE_AD) {
                if (!ClickUtil.canClick()) return;
                ZjUtils.getInstance().showAd();
            }
        });
        msgRecycler.setAdapter(msgShortAdapter);
    }

    private void findView() {
        ImageView btnLike = (ImageView) findViewById(R.id.btnLike);
        ImageView btnComment = (ImageView) findViewById(R.id.btnDouding);
        ImageView btnAtMe = (ImageView) findViewById(R.id.btnGift);
        ImageView btnFans = (ImageView) findViewById(R.id.btnVipCenter);
        msgRecycler = (SwipeRecyclerView) findViewById(R.id.msgRecycler);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);

        mRefreshLayout.setRefreshHeader(new MaterialHeader(mContext));
        mRefreshLayout.setRefreshFooter(new ClassicsFooter(mContext));
        mRefreshLayout.setOnRefreshListener(this::refreshData);
//        mRefreshLayout.setOnLoadMoreListener(this::loadMoreData);
        btnLike.setOnClickListener(this);
        btnComment.setOnClickListener(this);
        btnAtMe.setOnClickListener(this);
        btnFans.setOnClickListener(this);

        badge1 = (TextView) findViewById(R.id.tv_num1);
        badge2 = (TextView) findViewById(R.id.tv_num2);
        badge3 = (TextView) findViewById(R.id.tv_num3);
        badge4 = (TextView) findViewById(R.id.tv_num4);
    }

    private void loadMoreData(RefreshLayout refreshLayout) {
        mPage++;
        loadRecommendUser(false);
    }

    private void refreshData(RefreshLayout refreshLayout) {
        mPage = 1;
        if (isFirstLoadData()) {
            initData();
        } else {
            loadData();
        }
    }

    /**
     * 我的粉丝
     */
    private void forwardSecond(int type) {
        MessageSecondActivity.forward(mContext, type, AppConfig.getInstance().getUid());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needRefreshData == 0) {
            needRefreshData = 10;
            mRefreshLayout.autoRefresh();
        } else {
            needRefreshData--;
        }
        //消息刷新
        getMsgCount();
    }

    /**
     * 获取消息数量
     */
    private void getMsgCount() {
        HttpUtil.getMsgCount(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0) return;
                try {
                    JSONArray array = new JSONArray(Arrays.toString(info));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = new JSONObject(array.optString(i));
                        //类型 0=点赞 1=评论 2=@我 3=粉我
                        int num = object.optInt("num");
                        switch (object.optInt("type")) {
                            case 0:
                                if (num > 0) {
                                    badge1.setVisibility(View.VISIBLE);
                                    badge1.setText(String.valueOf(num));
                                } else {
                                    badge1.setVisibility(View.GONE);
                                }
                                break;
                            case 1:
                                if (num > 0) {
                                    badge2.setVisibility(View.VISIBLE);
                                    badge2.setText(String.valueOf(num));
                                } else {
                                    badge2.setVisibility(View.GONE);
                                }
                                break;
                            case 2:
                                if (num > 0) {
                                    badge3.setVisibility(View.VISIBLE);
                                    badge3.setText(String.valueOf(num));
                                } else {
                                    badge3.setVisibility(View.GONE);
                                }
                                break;
                            case 3:
                                if (num > 0) {
                                    badge4.setVisibility(View.VISIBLE);
                                    badge4.setText(String.valueOf(num));
                                } else {
                                    badge4.setVisibility(View.GONE);
                                }
                                break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void initData() {
        beans.clear();
        HttpUtil.getSystemMsgConservationList(1, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    //加载系统消息
                    List<MsgConservationNetBean> list = JSON.parseArray(Arrays.toString(info), MsgConservationNetBean.class);
                    msgShortAdapter.addData(MsgConservationNetBean.parse(list));
                    //我的好友item
                    msgShortAdapter.addData(MsgShortBean.builder().type(Constants.MESSAGE_TYPE_MSG).subType(Constants.MESSAGE_SUBTYPE_FRIEND).unreadNum(ImMessageUtil.getInstance().getAllUnReadMsgCount()).build());

                    //广告item
                    List<AdvertiseBean> adInnerList = AppConfig.getInstance().getConfig().getAdInnerList();
                    if (null != adInnerList && adInnerList.size() > 0) {
                        MsgShortBean adBean = new MsgShortBean();
                        adBean.setType(Constants.MESSAGE_TYPE_AD);
                        adBean.setAdThumb(adInnerList.get(0).getThumb());
                        adBean.setAdUrl(adInnerList.get(0).getUrl());
                        msgShortAdapter.addData(adBean);
                    }

                    if (msgShortAdapter.getData().size() > 0) {
                        //获取推荐用户
                        loadRecommendUser(true);
                    } else {
                        mRefreshLayout.finishRefresh();
                    }
                }
            }

            @Override
            public void onError() {
                mRefreshLayout.finishRefresh();
            }
        });
    }

    @Override
    public void loadData() {
        //更新系统消息列表
        HttpUtil.getSystemMsgConservationList(1, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                List<MsgConservationNetBean> list = JSON.parseArray(Arrays.toString(info), MsgConservationNetBean.class);
                for (int i = 0; i < list.size(); i++) {
                    boolean update = false;
                    for (int j = 0; j < beans.size(); j++) {
                        if (beans.get(j).getType() == Constants.MESSAGE_TYPE_MSG && beans.get(j).getSubType() == list.get(i).getType()) {
                            beans.get(j).setContent(list.get(i).getContent());
                            beans.get(j).setTime(list.get(i).getTime());
                            beans.get(j).setUnreadNum(String.valueOf(list.get(i).getUnreadNum()));
                            msgShortAdapter.notifyItemChanged(j, MsgShortAdapter.PAYLOAD_UNREAD_UPDATE);
                            update = true;
                            break;
                        }
                    }
                    if (!update) {
                        //新的系统通知默认加到第一个
                        msgShortAdapter.addData(1, MsgConservationNetBean.parse(list.get(i)));
                    }
                }

                //加载5次 刷新一次推荐列表
                if (recommendUserNum == 0) {
                    loadRecommendUser(true);
                } else {
                    if (msgShortAdapter.getData().size() > 0) {
                        msgRecycler.scrollToPosition(0);
                    }
                    mRefreshLayout.finishRefresh();
                }
            }

            @Override
            public void onError() {
                mRefreshLayout.finishRefresh();
            }
        });
    }

    private void loadRecommendUser(boolean isFresh) {
        HttpUtil.getRecommendUser(mPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                List<RecommendUserBean> list = JSON.parseArray(Arrays.toString(info), RecommendUserBean.class);

                if (isFresh) {
                    //刷新列表
                    int lastIndex = msgShortAdapter.getData().size() - 1;
                    //去除之前加载的推荐用户
                    for (int i = 0; i < recommendUserNum; i++) {
                        msgShortAdapter.removeAt((lastIndex - i));
                    }

                    recommendUserNum = list.size();

                    if (list.size() > 0) {
                        //添加推荐关注标签
                        int size = msgShortAdapter.getData().size();
                        if (msgShortAdapter.getData().get(size - 1).getType() != Constants.MESSAGE_TYPE_TEXT_LABEL) {
                            msgShortAdapter.addData(MsgShortBean.builder().type(Constants.MESSAGE_TYPE_TEXT_LABEL).subType(0).build());
                        }
                    }
                    mRefreshLayout.finishRefresh();
                } else {
                    recommendUserNum += list.size();
                    mRefreshLayout.finishLoadMore();
                }

                List<MsgShortBean> recUser = new ArrayList<>();
                for (RecommendUserBean user : list) {
                    recUser.add(MsgShortBean.builder()
                            .type(Constants.MESSAGE_TYPE_TEXT_RECOMMEND)
                            .uid(user.getUid())
                            .age(user.getAge())
                            .originDes(user.getOriginDes())
                            .avatar(user.getAvatar())
                            .sex(user.getSex())
                            .name(user.getName())
                            .city(user.getCity())
                            .build());
                }


                recommendUser.addAll(recUser);
                msgShortAdapter.addData(recUser);

            }

            @Override
            public void onError() {
                if (isFresh) {
                    mRefreshLayout.finishRefresh();
                } else {
                    mRefreshLayout.finishLoadMore();
                }
            }
        });
    }

    public void updateUnreadNum(String num) {
        int index = -1;
        List<MsgShortBean> data = msgShortAdapter.getData();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getType() == Constants.MESSAGE_TYPE_MSG && data.get(i).getSubType() == Constants.MESSAGE_SUBTYPE_FRIEND) {
                index = i;
                break;
            }
        }

        if (index >= 0) {
            msgShortAdapter.getData().get(index).setUnreadNum(num);
            msgShortAdapter.notifyItemChanged(index, MsgShortAdapter.PAYLOAD_UNREAD_UPDATE);
        }
    }
}
