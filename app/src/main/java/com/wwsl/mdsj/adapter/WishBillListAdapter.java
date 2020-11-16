package com.wwsl.mdsj.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.WishBillBean;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.interfaces.OnItemClickListener;
import com.wwsl.mdsj.interfaces.OnWishBillSendItemClickListener;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.utils.WordUtil;

public class WishBillListAdapter extends RefreshAdapter<WishBillBean> {

    private OnWishBillSendItemClickListener onWishBillSendItemClickListener;

    public WishBillListAdapter(Context context) {
        super(context);
    }

    public void setOnWishBillSendItemClickListener(OnWishBillSendItemClickListener onWishBillSendItemClickListener) {
        this.onWishBillSendItemClickListener = onWishBillSendItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_wish_bill_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    class Vh extends RecyclerView.ViewHolder {
        TextView tvNum;
        TextView tvName;
        ImageView ivAvatar;
        TextView tvDoneCount;
        TextView tvProgress;
        RecyclerView rvWishBillSendUsers;

        public Vh(View itemView) {
            super(itemView);
            tvNum = itemView.findViewById(R.id.tvNum);
            tvName = itemView.findViewById(R.id.tvName);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvDoneCount = itemView.findViewById(R.id.tvDoneCount);
            tvProgress = itemView.findViewById(R.id.tvProgress);
            rvWishBillSendUsers = itemView.findViewById(R.id.rvWishBillSendUsers);
        }

        void setData(WishBillBean bean, int position) {
            tvNum.setText(WordUtil.getString(R.string.wish_add_num) + (position + 1));
            tvName.setText(bean.getGiftname());
            ivAvatar.setBackgroundResource(0);
            ImgLoader.display(bean.getGifticon(), ivAvatar);
            tvDoneCount.setText(WordUtil.strToSpanned(WordUtil.strAddColor(bean.getSendnum(), "#333333") + "/" + bean.getNum()));
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tvProgress.getLayoutParams();
            if (bean.getSendnum().equals("0")) {
                params.width = 0;
            } else if (Integer.parseInt(bean.getSendnum()) >= Integer.parseInt(bean.getNum())) {
                params.width = DpUtil.dp2px(40);
            } else {
                params.width = DpUtil.dp2px(40) * Integer.parseInt(bean.getSendnum()) / Integer.parseInt(bean.getNum());
            }
            tvProgress.setLayoutParams(params);

            rvWishBillSendUsers.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            rvWishBillSendUsers.setHasFixedSize(true);

            WishBillSendUserAdapter adapter = new WishBillSendUserAdapter(mContext);
            adapter.setList(bean.getSendUsers());
            adapter.setOnItemClickListener(new OnItemClickListener<WishBillBean.SendUser>() {
                @Override
                public void onItemClick(WishBillBean.SendUser bean, int position) {
                    if (onWishBillSendItemClickListener != null) {
                        onWishBillSendItemClickListener.onAvatarClick(bean);
                    }
                }
            });
            rvWishBillSendUsers.setAdapter(adapter);
        }
    }
}
