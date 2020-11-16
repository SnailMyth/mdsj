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
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.ConfigBean;
import com.wwsl.mdsj.bean.LiveRoomTypeBean;
import com.wwsl.mdsj.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/8.
 */

public class LiveRoomTypeAdapter extends RecyclerView.Adapter<LiveRoomTypeAdapter.Vh> {
    private Context mContext;
    private List<LiveRoomTypeBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<LiveRoomTypeBean> mOnItemClickListener;

    public LiveRoomTypeAdapter(Context context, int checkedId) {
        mContext = context;
        mList = new ArrayList<>();
        ConfigBean configBean = AppConfig.getInstance().getConfig();
        if (configBean != null) {
            String[][] liveType = configBean.getLiveType();
            if (liveType != null) {
                List<LiveRoomTypeBean> list = LiveRoomTypeBean.getLiveTypeList(liveType);
                mList.addAll(list);
                for (LiveRoomTypeBean bean : mList) {
                    if (bean.getId() == checkedId) {
                        bean.setChecked(true);
                        break;
                    }
                }
            }
        }
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mList.get(position), position);
                    }
                }
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener<LiveRoomTypeBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_live_type, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(mList.get(position), position);
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
            mIcon = itemView.findViewById(R.id.icon);
            mName = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(LiveRoomTypeBean bean, int position) {
            itemView.setTag(position);
            mName.setText(bean.getName());
            if (bean.isChecked()) {
                mName.setTextColor(mContext.getResources().getColor(R.color.orange_lite));
                mIcon.setImageResource(bean.getCheckedIcon());
            } else {
                mName.setTextColor(0xffffffff);
                mIcon.setImageResource(bean.getUnCheckedIcon());
            }
        }
    }
}
