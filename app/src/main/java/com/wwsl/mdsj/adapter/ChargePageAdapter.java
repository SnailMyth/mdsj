package com.wwsl.mdsj.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.CoinBean;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/7/11.
 */

public class ChargePageAdapter extends PagerAdapter {

    private List<RecyclerView> mViewList;
    private List<ChargeItemAdapter> mAdapters;
    private static final int COIN_COUNT = 6;//每页6个Item
    private OnDialogCallBackListener listener;

    public ChargePageAdapter(Context context, List<CoinBean> coinBeanList) {

        mViewList = new ArrayList<>();
        mAdapters = new ArrayList<>();

        int fromIndex = 0;
        int size = coinBeanList.size();
        int pageCount = size / COIN_COUNT;
        if (size % COIN_COUNT > 0) {
            pageCount++;
        }

        LayoutInflater inflater = LayoutInflater.from(context);

        RecycleGridDivider divider = new RecycleGridDivider();

        for (int i = 0; i < pageCount; i++) {
            RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.view_dialog_page_recycleview, null, false);

            recyclerView.setLayoutManager(new GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false));
            recyclerView.addItemDecoration(divider);

            int endIndex = fromIndex + COIN_COUNT;
            if (endIndex > size) {
                endIndex = size;
            }

            List<CoinBean> list = new ArrayList<>();
            for (int j = fromIndex; j < endIndex; j++) {
                CoinBean bean = coinBeanList.get(j);
                list.add(bean);
            }

            ChargeItemAdapter adapter = new ChargeItemAdapter(list, i);

            adapter.setOnItemClickListener((adapter1, view, position) -> {
                ChargeItemAdapter tempAdapter = (ChargeItemAdapter) adapter1;
                int page = tempAdapter.getPage();

                for (int j = 0; j < mAdapters.size(); j++) {
                    int tempSize = mAdapters.get(j).getData().size();
                    if (page != j) {
                        for (int k = 0; k < tempSize; k++) {
                            mAdapters.get(j).getData().get(k).setSelect(false);
                        }
                    } else {
                        for (int k = 0; k < tempSize; k++) {
                            mAdapters.get(j).getData().get(k).setSelect(k == position);
                        }
                    }
                    mAdapters.get(j).notifyDataSetChanged();
                }

                if (listener != null) {
                    listener.onDialogViewClick(null, adapter1.getData().get(position));
                }
            });

            recyclerView.setAdapter(adapter);
            mAdapters.add(adapter);
            mViewList.add(recyclerView);
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

    @NotNull
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
            for (RecyclerView recyclerView : mViewList) {
                ChargeItemAdapter adapter = (ChargeItemAdapter) recyclerView.getAdapter();
                if (adapter != null) {
//                    adapter.release();
                }
            }
        }
    }

    public void setListener(OnDialogCallBackListener listener) {
        this.listener = listener;
    }

    class RecycleGridDivider extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(10, 10, 10, 10);
        }
    }
}
