package com.wwsl.mdsj.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.TicketBean;
import com.wwsl.mdsj.glide.ImgLoader;

public class TicketMyAdapter extends RefreshAdapter<TicketBean> {
    private View.OnClickListener mOnClickListener;

    public TicketMyAdapter(Context context) {
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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TicketMyAdapter.Vh(mInflater.inflate(R.layout.item_ticket_my, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((TicketMyAdapter.Vh) vh).setData(mList.get(position));
    }

    class Vh extends RecyclerView.ViewHolder {
        ImageView ivAvatarThumb;
        TextView tvTitle;
        TextView tvStatus;

        public Vh(View itemView) {
            super(itemView);
            ivAvatarThumb = itemView.findViewById(R.id.ivAvatarThumb);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(TicketBean bean) {
            itemView.setTag(bean);
            ImgLoader.display(bean.getImage(), ivAvatarThumb);
            tvTitle.setText(bean.getTitle() + "(1张)");
            tvStatus.setText(bean.getStatus_msg());
        }
    }
}
