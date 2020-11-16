package com.wwsl.mdsj.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.WishBillBean;
import com.wwsl.mdsj.glide.ImgLoader;

public class WishBillSendUserAdapter extends RefreshAdapter<WishBillBean.SendUser> {
    private View.OnClickListener onClickListener;

    public WishBillSendUserAdapter(Context context) {
        super(context);
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mOnItemClickListener != null) {
                    int position = (int) tag;
                    mOnItemClickListener.onItemClick(mList.get(position), position);
                }
            }
        };
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_wish_bill_send_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((WishBillSendUserAdapter.Vh) vh).setData(mList.get(position), position);
    }

    class Vh extends RecyclerView.ViewHolder {
        ImageView ivAvatarBg;
        ImageView ivAvatar;
        TextView tvAvatarNum;

        public Vh(View itemView) {
            super(itemView);
            ivAvatarBg = itemView.findViewById(R.id.ivAvatarBg);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvAvatarNum = itemView.findViewById(R.id.tvAvatarNum);
            itemView.setOnClickListener(onClickListener);
        }

        void setData(WishBillBean.SendUser bean, int position) {
            itemView.setTag(position);
            ImgLoader.display(bean.getAvatarThumb(), ivAvatar);
            tvAvatarNum.setText((position + 1) + "");
            if (position == 0) {
                ivAvatarBg.setBackgroundResource(R.drawable.oval_red);
                tvAvatarNum.setBackgroundResource(R.drawable.bg_num_red);
            } else {
                ivAvatarBg.setBackgroundResource(R.drawable.oval_yellow);
                tvAvatarNum.setBackgroundResource(R.drawable.bg_num_yellow);
            }
        }
    }
}
