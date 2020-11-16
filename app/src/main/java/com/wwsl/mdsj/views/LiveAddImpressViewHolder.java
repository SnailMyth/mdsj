package com.wwsl.mdsj.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.live.LiveAddImpressActivity;
import com.wwsl.mdsj.activity.live.LiveAudienceActivity;
import com.wwsl.mdsj.bean.ImpressBean;
import com.wwsl.mdsj.custom.MyTextView;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.LifeCycleAdapter;
import com.wwsl.mdsj.utils.ToastUtil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cxf on 2018/10/15.
 * 添加印象
 */

public class LiveAddImpressViewHolder extends AbsLivePageViewHolder {

    private LinearLayout mGroup;
    private String mToUid;
    private LinkedList<Integer> mLinkedList;
    private HttpCallback mHttpCallback;
    private View.OnClickListener mOnClickListener;
    private LayoutInflater mInflater;
    private boolean mChanged;
    private boolean mUpdated;

    public LiveAddImpressViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
        mLinkedList = new LinkedList<>();
        mInflater = LayoutInflater.from(context);
    }

    public void setToUid(String toUid) {
        mToUid = toUid;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_impress;
    }

    @Override
    public void init() {
        super.init();
        mGroup = (LinearLayout) findViewById(R.id.group);
        findViewById(R.id.btn_save).setOnClickListener(this);
        mLifeCycleListener = new LifeCycleAdapter() {
            @Override
            public void onDestroy() {
                HttpUtil.cancel(HttpConst.GET_ALL_IMPRESS);
                HttpUtil.cancel(HttpConst.SET_IMPRESS);
            }
        };
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTextView mtv = (MyTextView) v;
                if (!mtv.isChecked()) {
                    if (mLinkedList.size() < 3) {
                        mtv.setChecked(true);
                        addId(mtv.getBean().getId());
                        mChanged = true;
                    } else {
                        ToastUtil.show(R.string.impress_add_max);
                    }
                } else {
                    removeId(mtv.getBean().getId());
                    mtv.setChecked(false);
                    mChanged = true;
                }
            }
        };
        mHttpCallback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                ToastUtil.show(msg);
                if (code == 0) {
                    mUpdated = true;
                    hide();
                }
            }
        };
    }

    private void removeId(int impressId) {
        int index = -1;
        for (int i = 0, size = mLinkedList.size(); i < size; i++) {
            if (impressId == mLinkedList.get(i)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            mLinkedList.remove(index);
        }
    }

    private void addId(int impressId) {
        mLinkedList.add(impressId);
    }

    @Override
    public void loadData() {
        initData();
    }

    @Override
    public void onHide() {
        mLinkedList.clear();
        mGroup.removeAllViews();
        removeFromParent();
        if (AppConfig.liveRoomScroll() && mContext != null && mContext instanceof LiveAudienceActivity) {
            ((LiveAudienceActivity) mContext).setScrollFrozen(false);
        }
    }

    private void initData() {
        HttpUtil.getAllImpress(mToUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    List<ImpressBean> list = JSON.parseArray(Arrays.toString(info), ImpressBean.class);
                    int line = 0;
                    int fromIndex = 0;
                    boolean hasNext = true;
                    while (hasNext) {
                        LinearLayout linearLayout = (LinearLayout) mInflater.inflate(R.layout.view_impress_line, mGroup, false);
                        int endIndex = line % 2 == 0 ? fromIndex + 4 : fromIndex + 3;
                        if (endIndex >= list.size()) {
                            endIndex = list.size();
                            hasNext = false;
                        }
                        for (int i = fromIndex; i < endIndex; i++) {
                            MyTextView item = (MyTextView) mInflater.inflate(R.layout.view_impress_item, linearLayout, false);
                            ImpressBean impressBean = list.get(i);
                            if (impressBean.isChecked()) {
                                addId(impressBean.getId());
                            }
                            item.setBean(impressBean);
                            linearLayout.addView(item);
                            item.setOnClickListener(mOnClickListener);
                        }
                        fromIndex = endIndex;
                        line++;
                        mGroup.addView(linearLayout);
                    }
                    if (AppConfig.liveRoomScroll() && mContext != null && mContext instanceof LiveAudienceActivity) {
                        ((LiveAudienceActivity) mContext).setScrollFrozen(true);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_save:
                save();
                break;
        }
    }

    private void save() {
        if (mLinkedList.size() == 0) {
            ToastUtil.show(R.string.impress_please_choose);
            return;
        }
        if (!mChanged) {
            ToastUtil.show(R.string.impress_not_changed);
            return;
        }
        String ids = "";
        for (Integer integer : mLinkedList) {
            ids += integer + ",";
        }
        if (ids.endsWith(",")) {
            ids = ids.substring(0, ids.length() - 1);
        }
        HttpUtil.setImpress(mToUid, ids, mHttpCallback);
    }

    public boolean isUpdatedImpress() {
        return mUpdated;
    }

    @Override
    public void hide() {
        if (mContext instanceof LiveAddImpressActivity) {
            ((LiveAddImpressActivity) mContext).onBackPressed();
        } else {
            super.hide();
        }
    }


}
