package com.wwsl.mdsj.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.ConfigBean;
import com.wwsl.mdsj.interfaces.OnItemClickListener;
import com.wwsl.mdsj.share.ShareItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/19.
 * 直播分享
 */

public class LiveShareAdapter extends RecyclerView.Adapter<LiveShareAdapter.Vh> {

    private List<ShareItemBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<ShareItemBean> mOnItemClickListener;

    public LiveShareAdapter(Context context) {
        mList = new ArrayList<>();
        ConfigBean configBean = AppConfig.getInstance().getConfig();
        if (configBean != null) {
            List<ShareItemBean> list = ShareItemBean.getLiveShareTypeList(configBean.getShareType());
            mList.addAll(list);
        }
//        ShareItemBean linkBean = new ShareItemBean();
//        linkBean.setType(Constants.LINK);
//        linkBean.setName(R.string.copy_link);
//        linkBean.setIcon1(R.mipmap.icon_share_link);
//        mList.add(linkBean);
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick((ShareItemBean) tag, 0);
                    }
                }
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener<ShareItemBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_live_share, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mIcon;
        TextView mName;

        public Vh(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.icon);
            mName = (TextView) itemView.findViewById(R.id.name);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(ShareItemBean bean) {
            itemView.setTag(bean);
            mIcon.setImageResource(bean.getIcon1());
            mName.setText(bean.getName());
        }
    }
}
