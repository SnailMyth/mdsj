package com.wwsl.mdsj.activity.message;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.lzy.okgo.model.Response;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.UserHomePageActivity;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.activity.video.VideoPlayActivity;
import com.wwsl.mdsj.adapter.MessageActionAdapter;
import com.wwsl.mdsj.bean.MessageShowBean;
import com.wwsl.mdsj.bean.VideoBean;
import com.wwsl.mdsj.bean.net.NetAtMeBean;
import com.wwsl.mdsj.bean.net.NetCommentBean;
import com.wwsl.mdsj.bean.net.NetFansBean;
import com.wwsl.mdsj.bean.net.NetLikeBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.http.JsonBean;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.utils.CommonUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.StrUtil;

/**
 * Created by cxf on 2018/9/29.
 * 粉丝,赞,@我,评论 界面
 */
public class MessageSecondActivity extends AbsActivity {
    private SmartRefreshLayout mRefreshLayout;
    private SwipeRecyclerView recycler;
    private MessageActionAdapter mAdapter;
    private String mToUid;
    private int type;
    private int mPage = 1;

    private List<MessageShowBean> beans;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_list;
    }

    @Override
    protected void main() {
        mToUid = getIntent().getStringExtra(Constants.TO_UID);
        type = getIntent().getIntExtra("type", 0);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        recycler = findViewById(R.id.recycler);

        if (TextUtils.isEmpty(mToUid)) {
            return;
        }

        String titile = "";
        switch (type) {
            case Constants.TYPE_LIKE:
                titile = WordUtil.getString(R.string.msg_like);
                break;
            case Constants.TYPE_COMMENT:
                titile = WordUtil.getString(R.string.msg_comment);
                break;

            case Constants.TYPE_AT_ME:
                titile = WordUtil.getString(R.string.msg_at_me);
                break;
            case Constants.TYPE_FANS:
                titile = WordUtil.getString(R.string.fans);
                break;
        }

        setTitle(titile);
        beans = new ArrayList<>();

        mAdapter = new MessageActionAdapter(new ArrayList<>());

        mAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.avatar) {
                    UserHomePageActivity.forward(MessageSecondActivity.this, beans.get(position).getUid());
                } else if (view.getId() == R.id.thumb) {
                    String videoId = beans.get(position).getVideoId();
                    if (!StrUtil.isEmpty(videoId)) {
                        goVideo(videoId);
                    }
                } else if (view.getId() == R.id.tvFocus) {
                    if (type == Constants.TYPE_FANS && mAdapter.getData().get(position).getFollow() != 2) {
                        HttpUtil.setAttention(Constants.FOLLOW_FROM_MAIN_MSG, mAdapter.getData().get(position).getUid(), new CommonCallback<Integer>() {
                            @Override
                            public void callback(Integer isAttention) {
                                onAttention(position);
                            }
                        });
                    }
                }
            }
        });

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(mAdapter);
        ClassicsHeader classicsHeader = new ClassicsHeader(this);
        mRefreshLayout.setRefreshHeader(classicsHeader);
        mRefreshLayout.setRefreshFooter(new ClassicsFooter(this));

        mRefreshLayout.setOnRefreshListener(this::refreshData);
        mRefreshLayout.setOnLoadMoreListener(this::loadMoreData);
        mRefreshLayout.setEnableAutoLoadMore(true);

        String noDataText = "暂无数据";
        switch (type) {
            case Constants.TYPE_LIKE:
                noDataText = "还没有喜欢的视频哦,去观看视频吧";
                break;
            case Constants.TYPE_COMMENT:
                noDataText = "还没有人评论你的视频哦~";
                break;
            case Constants.TYPE_AT_ME:
                noDataText = "还没有人@你哦~";
                break;
            case Constants.TYPE_FANS:
                noDataText = "你还没有粉丝哦~,赶快去添加吧~";
                break;
        }

        mAdapter.setEmptyView(CommonUtil.getEmptyView(noDataText, this, mRefreshLayout));
        mRefreshLayout.autoRefresh();
    }

    private void loadMoreData(RefreshLayout refreshLayout) {
        mPage++;
        loadData(false);
    }

    private void refreshData(RefreshLayout refreshLayout) {
        mPage = 1;
        loadData(true);
    }

    private void goVideo(String videoId) {
        showLoadCancelable(false, "加载视频...");
        HttpUtil.getVideo(videoId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && null != info[0]) {
                    dismissLoad();
                    VideoBean bean = JSON.parseObject(info[0], VideoBean.class);
                    if (bean != null) {
                        VideoPlayActivity.forward(MessageSecondActivity.this, bean);
                    } else {
                        ToastUtil.show("获取视频失败");
                    }
                } else {
                    ToastUtil.show(msg);
                    dismissLoad();
                }
            }

            @Override
            public void onError() {
                dismissLoad();
            }
        });
    }


    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpConst.GET_FOLLOW_LIST);
        super.onDestroy();
    }

    public static void forward(Context context, int type, String uid) {
        Intent intent = new Intent(context, MessageSecondActivity.class);
        intent.putExtra(Constants.TO_UID, uid);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    public void loadData(boolean isFresh) {
        switch (type) {
            case Constants.TYPE_LIKE:
                loadLike(isFresh);
                break;
            case Constants.TYPE_COMMENT:
                loadComment(isFresh);
                break;
            case Constants.TYPE_AT_ME:
                loadAtMe(isFresh);
                break;
            case Constants.TYPE_FANS:
                loadFans(isFresh);
                break;
        }
    }

    private void loadLike(boolean isFresh) {
        HttpUtil.getFavorite(mToUid, mPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                loadFinish(isFresh, code, msg, info);
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                loadDataError(isFresh);
            }
        });

    }

    private void loadComment(boolean isFresh) {
        HttpUtil.getComments(mToUid, mPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                loadFinish(isFresh, code, msg, info);
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                loadDataError(isFresh);
            }
        });
    }

    private void loadAtMe(boolean isFresh) {
        HttpUtil.getAtMeList(mToUid, mPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                loadFinish(isFresh, code, msg, info);
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                loadDataError(isFresh);
            }
        });
    }

    private void loadFans(boolean isFresh) {
        HttpUtil.getMyFansList(mToUid, mPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                loadFinish(isFresh, code, msg, info);
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                loadDataError(isFresh);
            }
        });
    }

    public void loadFinish(boolean isFresh, int code, String msg, String[] info) {
        if (code == 0) {
            List<MessageShowBean> res = null;
            switch (type) {
                case Constants.TYPE_LIKE:
                    List<NetLikeBean> likeBeans = JSON.parseArray(Arrays.toString(info), NetLikeBean.class);
                    res = MessageShowBean.parseLike(likeBeans);
                    break;
                case Constants.TYPE_COMMENT:
                    List<NetCommentBean> commentBeans = JSON.parseArray(Arrays.toString(info), NetCommentBean.class);
                    res = MessageShowBean.parseComment(commentBeans);
                    break;
                case Constants.TYPE_AT_ME:
                    List<NetAtMeBean> atMeBeans = JSON.parseArray(Arrays.toString(info), NetAtMeBean.class);
                    res = MessageShowBean.parseAtMe(atMeBeans);
                    break;
                case Constants.TYPE_FANS:
                    List<NetFansBean> netFansBeans = JSON.parseArray(Arrays.toString(info), NetFansBean.class);
                    res = MessageShowBean.parseFans(netFansBeans);
                    break;
                default:
                    res = new ArrayList<>();
                    break;
            }

            if (isFresh) {
                beans.clear();
                beans.addAll(res);
                mAdapter.setNewInstance(res);
                mRefreshLayout.finishRefresh();
            } else {
                beans.addAll(res);
                mAdapter.addData(res);
                mRefreshLayout.finishLoadMore();
            }
        } else {
            ToastUtil.show(msg);
            loadDataError(isFresh);
        }
    }

    private void onAttention(int position) {
        beans.get(position).setFollow(2);
        mAdapter.notifyItemChanged(position, MessageActionAdapter.PAYLOAD_FOLLOW);
    }

    private void loadDataError(boolean isInit) {
        if (isInit) {
            mRefreshLayout.finishRefresh();
        } else {
            mRefreshLayout.finishLoadMore();
        }
    }
}
