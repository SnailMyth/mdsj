package com.wwsl.mdsj.dialog;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.LiveShareAdapter;
import com.wwsl.mdsj.interfaces.OnItemClickListener;
import com.wwsl.mdsj.share.ShareItemBean;


/**
 * Created by cxf on 2018/10/19.
 * 直播分享弹窗
 */

public class LiveShareDialogFragment extends AbsDialogFragment implements OnItemClickListener<ShareItemBean> {

    private RecyclerView mRecyclerView;
    private ActionListener mActionListener;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_share;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false));
        LiveShareAdapter adapter = new LiveShareAdapter(mContext);
        adapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(ShareItemBean bean, int position) {
        if (!canClick()) {
            return;
        }
        dismiss();
        if (mActionListener != null) {
            mActionListener.onItemClick(bean.getType());
        }

    }

    public interface ActionListener {
        void onItemClick(String type);
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActionListener = null;
    }
}
