package com.wwsl.mdsj.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.LiveRecordBean;
import com.wwsl.mdsj.utils.StringUtil;

import cn.hutool.core.util.StrUtil;

/**
 * Created by cxf on 2018/9/30.
 */

public class LiveRecordAdapter extends RefreshAdapter<LiveRecordBean> {

    private View.OnClickListener mOnClickListener;

    public LiveRecordAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag != null) {
                    LiveRecordBean bean = (LiveRecordBean) tag;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(bean, 0);
                    }
                }
            }
        };
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_live_record, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position), position);
    }

    class Vh extends RecyclerView.ViewHolder {
        TextView mTitle;
        TextView mTime;
        TextView mNum;
        TextView duration;
        TextView votes;
        View tvDivider;

        public Vh(View itemView) {
            super(itemView);
            tvDivider = itemView.findViewById(R.id.tvDivider);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mTime = (TextView) itemView.findViewById(R.id.time);
            mNum = (TextView) itemView.findViewById(R.id.num);
            duration = (TextView) itemView.findViewById(R.id.duration);
            votes = (TextView) itemView.findViewById(R.id.votes);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(LiveRecordBean bean, int position) {
            if (position == 0) {
                tvDivider.setVisibility(View.VISIBLE);
            } else if (position == mList.size() - 1) {
                tvDivider.setVisibility(View.GONE);
            } else {
                tvDivider.setVisibility(View.VISIBLE);
            }
            if (!StrUtil.isEmpty(bean.getVotes())) {
                votes.setText(String.format("收到%s%s", bean.getVotes(), AppConfig.getInstance().getVotesName()));
            }
            duration.setText(String.format("直播时长:%s", bean.getLength()));
            itemView.setTag(bean);
            mTitle.setText(bean.getTitle());
            mTime.setText(bean.getDateEndTime());
            mNum.setText(StringUtil.toWan(bean.getNums()));
        }
    }

}
