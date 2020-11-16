package com.wwsl.mdsj.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.TicketBean;
import com.wwsl.mdsj.glide.ImgLoader;

public class TicketAdapter extends RefreshAdapter<TicketBean> {
    private View.OnClickListener mOnClickListener;
    //隐藏主播信息
    private boolean mHideUser;

    public TicketAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag != null) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick((TicketBean) tag, 0);
                    }
                }
            }
        };
    }

    public void setHideUser(boolean hideUser) {
        mHideUser = hideUser;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_ticket, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position));
    }

    class Vh extends RecyclerView.ViewHolder {
        ImageView ivAvatarThumb;
        TextView tvTitle;
        TextView tvName;
        TextView tvTimeStart;

        public Vh(View itemView) {
            super(itemView);
            ivAvatarThumb = itemView.findViewById(R.id.ivAvatarThumb);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvName = itemView.findViewById(R.id.tvName);
            tvTimeStart = itemView.findViewById(R.id.tvTimeStart);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(TicketBean bean) {
            itemView.setTag(bean);
            ImgLoader.display(bean.getImage(), ivAvatarThumb);
            tvTitle.setText(bean.getTitle());
            if (mHideUser) {
                tvName.setVisibility(View.GONE);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvTitle.getLayoutParams();
                params.addRule(RelativeLayout.ABOVE, R.id.tvTimeStart);
            } else {
                tvName.setText(bean.getUser_nicename());
            }
            tvTimeStart.setText(bean.getStime());
        }
    }
}
