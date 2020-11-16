package com.wwsl.mdsj.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.LiveFunctionBean;
import com.wwsl.mdsj.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class LiveFunctionPagerAdapter extends PagerAdapter {
    private List<RecyclerView> mViewList;
    private static final int FUNCTION_COUNT = 8;//每页8个功能
    private OnItemClickListener<Integer> onItemClickListener;

    public LiveFunctionPagerAdapter(Context context, List<LiveFunctionBean> functionList) {
        mViewList = new ArrayList<>();
        int fromIndex = 0;
        int size = functionList.size();
        int pageCount = size / FUNCTION_COUNT;
        if (size % FUNCTION_COUNT > 0) {
            pageCount++;
        }

        LayoutInflater inflater = LayoutInflater.from(context);

        for (int i = 0; i < pageCount; i++) {
            RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.view_dialog_page_recycleview, null, false);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false));
            int endIndex = fromIndex + FUNCTION_COUNT;
            if (endIndex > size) {
                endIndex = size;
            }
            List<LiveFunctionBean> list = new ArrayList<>();
            for (int j = fromIndex; j < endIndex; j++) {
                LiveFunctionBean bean = functionList.get(j);
                list.add(bean);
            }
            LiveFunctionAdapter adapter = new LiveFunctionAdapter(context, list);
            adapter.setOnItemClickListener(new OnItemClickListener<Integer>() {
                @Override
                public void onItemClick(Integer bean, int position) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(bean, position);
                    }
                }
            });
//            adapter.setActionListener(actionListener);
            recyclerView.setAdapter(adapter);
            mViewList.add(recyclerView);
            fromIndex = endIndex;
        }

    }

    public void setOnItemClickListener(OnItemClickListener<Integer> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
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
}
