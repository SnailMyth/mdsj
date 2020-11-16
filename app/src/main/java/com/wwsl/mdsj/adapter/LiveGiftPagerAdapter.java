package com.wwsl.mdsj.adapter;

import android.content.Context;

import androidx.viewpager.widget.PagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.LiveGiftBean;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/7/11.
 */

public class LiveGiftPagerAdapter extends PagerAdapter {

    private List<SwipeRecyclerView> mViewList;
    private List<LiveGiftAdapter> mAdapters;
    private static final int GIFT_COUNT = 10;//每页10个礼物
    private int mPage = -1;
    private ActionListener mActionListener;

    public LiveGiftPagerAdapter(Context context, List<LiveGiftBean> giftList) {
        mViewList = new ArrayList<>();
        mAdapters = new ArrayList<>();
        int fromIndex = 0;
        int size = giftList.size();
        int pageCount = size / GIFT_COUNT;
        if (size % GIFT_COUNT > 0) {
            pageCount++;
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        String coinName = AppConfig.getInstance().getCoinName();
        LiveGiftAdapter.ActionListener actionListener = new LiveGiftAdapter.ActionListener() {
            @Override
            public void onCancel() {
                if (mPage >= 0 && mPage < mViewList.size()) {
                    LiveGiftAdapter adapter = mAdapters.get(mPage);
                    if (adapter != null) {
                        adapter.cancelChecked();
                    }
                }
            }

            @Override
            public void onItemChecked(LiveGiftBean bean) {
                mPage = bean.getPage();
                if (mActionListener != null) {
                    mActionListener.onItemChecked(bean);
                }
            }
        };
        for (int i = 0; i < pageCount; i++) {
            SwipeRecyclerView recyclerView = (SwipeRecyclerView) inflater.inflate(R.layout.view_dialog_page_recycleview, null, false);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 5, GridLayoutManager.VERTICAL, false));
            int endIndex = fromIndex + GIFT_COUNT;
            if (endIndex > size) {
                endIndex = size;
            }
            List<LiveGiftBean> list = new ArrayList<>();
            for (int j = fromIndex; j < endIndex; j++) {
                LiveGiftBean bean = giftList.get(j);
                bean.setPage(i);
                list.add(bean);
            }
            LiveGiftAdapter adapter = new LiveGiftAdapter(inflater, list, coinName);
            adapter.setActionListener(actionListener);
            recyclerView.setAdapter(adapter);
            mViewList.add(recyclerView);
            mAdapters.add(adapter);
            fromIndex = endIndex;
        }
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mViewList.get(position);
        container.addView(view);
        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));
    }

    public void release() {
        if (mViewList != null) {
            for (LiveGiftAdapter adapter : mAdapters) {
                adapter.release();
            }
        }
    }

    public interface ActionListener {
        void onItemChecked(LiveGiftBean bean);
    }


    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }
}
